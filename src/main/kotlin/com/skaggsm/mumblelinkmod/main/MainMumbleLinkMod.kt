package com.skaggsm.mumblelinkmod.main

import com.skaggsm.mumblelinkmod.ServerOnChangeWorldCallback
import com.skaggsm.mumblelinkmod.ServerOnConnectCallback
import com.skaggsm.mumblelinkmod.ServerOnTeamsModify
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigBranch
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree
import io.netty.buffer.Unpooled
import me.shedaniel.fiber2cloth.api.Fiber2Cloth
import net.fabricmc.api.EnvType
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
import java.util.Locale
import kotlin.io.path.div

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

    lateinit var config: MainConfig
    lateinit var configTree: ConfigBranch

    override fun onInitialize() {
        setupConfig()
        setupEvents()
    }

    @Suppress("DEPRECATION")
    private fun setupConfig() {
        config = MainConfig()

        configTree = ConfigTree.builder().applyFromPojo(config, createSettings()).withName("main").build()

        if (Files.notExists(configFile)) {
            serialize()
        }

        // Verify save worked
        deserialize()
    }

    fun createSettings(): AnnotatedSettings {
        val settingsBuilder = AnnotatedSettings.builder()
        if (FabricLoader.getInstance().environmentType == EnvType.CLIENT)
            Fiber2Cloth.configure(settingsBuilder)
        return settingsBuilder.build()
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
