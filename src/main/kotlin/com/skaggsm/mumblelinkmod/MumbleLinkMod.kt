package com.skaggsm.mumblelinkmod

import com.skaggsm.mumblelinkmod.network.SendMumbleURL
import io.netty.buffer.Unpooled.buffer
import net.fabricmc.api.ModInitializer
import net.minecraft.client.network.packet.CustomPayloadS2CPacket
import net.minecraft.util.PacketByteBuf

/**
 * Created by Mitchell Skaggs on 5/29/2019.
 */
object MumbleLinkMod : ModInitializer {
    override fun onInitialize() {
        ServerOnConnectCallback.EVENT.register(ServerOnConnectCallback { player ->
            println("${player.name.string} connected!")

            val url = "skaggsm.com:25003"

            val buf = PacketByteBuf(buffer())

            buf.writeString(url)

            player.networkHandler.sendPacket(CustomPayloadS2CPacket(SendMumbleURL.ID, buf))
        })
    }
}
