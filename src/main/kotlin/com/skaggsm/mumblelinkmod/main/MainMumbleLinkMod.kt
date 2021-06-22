package com.skaggsm.mumblelinkmod.main

import com.skaggsm.mumblelinkmod.ServerOnChangeWorldCallback
import com.skaggsm.mumblelinkmod.ServerOnConnectCallback
import com.skaggsm.mumblelinkmod.ServerOnTeamsModify
import com.skaggsm.mumblelinkmod.client.ClientMumbleLinkMod.createSettings
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree
import io.netty.buffer.Unpooled
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.text.MessageFormat
import java.time.Duration
import java.time.Instant
import java.time.temporal.Temporal
import java.util.Locale
import kotlin.io.path.deleteIfExists
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.getLastModifiedTime
import kotlin.io.path.moveTo

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */
object MainMumbleLinkMod : ModInitializer {
    // Common constants
    const val MODID: String = "fabric-mumblelink-mod"
    val LOG: Logger = LogManager.getLogger(MODID)
    val SERIALIZER = JanksonValueSerializer(false)
    val configFolder: Path = FabricLoader.getInstance().configDir

    // Config files
    private val configFile = configFolder / "fabric-mumblelink-mod-main.json"
    private val oldConfigFile = configFolder / "fabric-mumblelink-mod.toml"
    private val oldConfigFileBackup = configFolder / "fabric-mumblelink-mod.toml.old"

    // Configs
    @Deprecated("Use the new scoped configs!")
    lateinit var oldConfigHolder: ConfigHolder<OldConfig>
    @Deprecated("Use the new scoped configs!")
    var oldConfig: OldConfig? = null
    lateinit var config: MainConfig
    lateinit var configTree: ConfigBranch

    override fun onInitialize() {
        handleOldConfig()
        setupConfig()
        setupEvents()
    }

    @Suppress("DEPRECATION")
    private fun setupConfig() {
        config = MainConfig()

        oldConfig?.let {
            config.voipClient = it.voipClient.updated()

            // Special handling of old-style username/password combos
            val hostParts = it.mumbleServerHost?.split('@', limit = 2)
            config.voipServerHost = hostParts?.last() ?: ""
            config.voipServerUserinfo = if (hostParts != null && hostParts.size > 1) hostParts.first() else ""

            config.voipServerPort = it.mumbleServerPort ?: -1
            config.voipServerPath = it.mumbleServerPath ?: ""
            config.voipServerQuery = it.mumbleServerQuery ?: ""
        }

        configTree = ConfigTree.builder().applyFromPojo(config, createSettings()).withName("main").build()

        if (Files.notExists(configFile)) {
            serialize()
        }

        // Verify save worked
        deserialize()
    }

    fun serialize() {
        FiberSerialization.serialize(
            configTree,
            Files.newOutputStream(configFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE),
            SERIALIZER
        )
    }

    private fun deserialize() {
        FiberSerialization.deserialize(
            configTree,
            Files.newInputStream(configFile, StandardOpenOption.READ),
            SERIALIZER
        )
    }

    @Suppress("DEPRECATION")
    private fun handleOldConfig() {
        // Load old config
        if (oldConfigFile.exists()) {
            oldConfigHolder = AutoConfig.register(OldConfig::class.java, ::Toml4jConfigSerializer)
            oldConfig = oldConfigHolder.config
            oldConfigFile.moveTo(oldConfigFileBackup)
        }

        // Expire backup
        if (oldConfigFileBackup.exists() &&
            Instant.now() - oldConfigFileBackup.getLastModifiedTime().toInstant() >= Duration.ofDays(14)
        ) {
            oldConfigFileBackup.deleteIfExists()
        }
    }

    private fun setupEvents() {
        ServerOnConnectCallback.EVENT.register(
            ServerOnConnectCallback { player ->
                sendVoipPacket(player)
            }
        )

        ServerOnChangeWorldCallback.EVENT.register(
            ServerOnChangeWorldCallback { toWorld, player ->
                sendVoipPacket(player, toWorld)
            }
        )

        ServerOnTeamsModify.EVENT.register(
            ServerOnTeamsModify { _, server ->
                sendAllVoipPackets(server)
            }
        )
    }

    private fun sendAllVoipPackets(server: MinecraftServer) {
        server.playerManager.playerList.forEach { sendVoipPacket(it) }
    }

    private fun sendVoipPacket(player: ServerPlayerEntity, toWorld: RegistryKey<World> = player.world.registryKey) {
        LOG.trace("Updating VoIP location for ${player.name.string}!")

        val dim = toWorld.value
        val dimNamespace = dim.namespace.split('_').joinToString(" ") {
            it.replaceFirstChar { c ->
                if (c.isLowerCase()) c.titlecase(
                    Locale.getDefault()
                ) else c.toString()
            }
        }
        val dimPath = dim.path.split('_').joinToString(" ") {
            it.replaceFirstChar { c ->
                if (c.isLowerCase()) c.titlecase(
                    Locale.getDefault()
                ) else c.toString()
            }
        }
        val dimId = "$dimNamespace $dimPath"

        val teamName = player.scoreboardTeam?.name ?: ""

        val templateParams: Array<Any> = arrayOf(dimId, dimNamespace, dimPath, teamName)

        val userinfo: String = config.voipServerUserinfo
        val host: String = config.voipServerHost
        val port: Int = config.voipServerPort
        val path: String = MessageFormat.format(config.voipServerPath, *templateParams)
        val query: String = MessageFormat.format(config.voipServerQuery, *templateParams)
        val fragment: String = config.voipServerFragment

        val buf = PacketByteBuf(Unpooled.buffer())
        buf.writeEnumConstant(config.voipClient)
        buf.writeString(userinfo)
        buf.writeString(host)
        buf.writeInt(port)
        buf.writeString(path)
        buf.writeString(query)
        buf.writeString(fragment)

        ServerPlayNetworking.send(player, SendMumbleURL.ID, buf)
    }
}

private operator fun Temporal.minus(other: Temporal): Duration {
    return Duration.between(other, this)
}
