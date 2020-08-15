package com.skaggsm.mumblelinkmod

import com.skaggsm.mumblelinkmod.config.MumbleLinkConfig
import com.skaggsm.mumblelinkmod.network.SendMumbleURL
import io.netty.buffer.Unpooled
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.registry.RegistryKey
import net.minecraft.world.World
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.text.MessageFormat

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */
object MumbleLinkMod : ModInitializer {
    val log: Logger = LogManager.getLogger(MumbleLink.MODID)
    lateinit var config: ConfigHolder<MumbleLinkConfig>

    override fun onInitialize() {
        config = AutoConfig.register(MumbleLinkConfig::class.java, ::Toml4jConfigSerializer)

        ServerOnConnectCallback.EVENT.register(ServerOnConnectCallback { player ->
            sendVoipPacket(player)
        })

        ServerOnChangeWorldCallback.EVENT.register(ServerOnChangeWorldCallback { toWorld, player ->
            sendVoipPacket(player, toWorld)
        })

        ServerOnTeamsModify.EVENT.register(ServerOnTeamsModify { _, server ->
            sendAllVoipPackets(server)
        })
    }

    private fun sendAllVoipPackets(server: MinecraftServer) {
        server.playerManager.playerList.forEach { sendVoipPacket(it) }
    }

    private fun sendVoipPacket(player: ServerPlayerEntity, toWorld: RegistryKey<World> = player.world.registryKey) {
        config.config.mumbleServerHost?.let { mumbleServerHost ->
            log.trace("Updating VoIP location for ${player.name.string}!")

            val dim = toWorld.value
            val dimNamespace = dim.namespace.split('_').joinToString(" ") { it.capitalize() }
            val dimPath = dim.path.split('_').joinToString(" ") { it.capitalize() }
            val dimId = "$dimNamespace $dimPath"

            val teamName = player.scoreboardTeam?.name ?: ""

            val templateParams: Array<Any> = arrayOf(dimId, dimNamespace, dimPath, teamName)

            val host: String = mumbleServerHost
            val port: Int = config.config.mumbleServerPort ?: -1
            val path: String = config.config.mumbleServerPath?.let { MessageFormat.format(it, *templateParams) } ?: ""
            val query: String = config.config.mumbleServerQuery?.let { MessageFormat.format(it, *templateParams) } ?: ""

            val buf = PacketByteBuf(Unpooled.buffer())
            buf.writeEnumConstant(config.config.voipClient)
            buf.writeString(host)
            buf.writeInt(port)
            buf.writeString(path)
            buf.writeString(query)

            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, SendMumbleURL.ID, buf)
        }
    }
}
