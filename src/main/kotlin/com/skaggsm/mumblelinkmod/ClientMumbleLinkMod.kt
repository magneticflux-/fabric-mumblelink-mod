package com.skaggsm.mumblelinkmod

import com.skaggsm.jmumblelink.MumbleLink
import com.skaggsm.jmumblelink.MumbleLinkImpl
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.client.ClientTickCallback
import net.minecraft.util.math.Vec3d

/**
 * Created by Mitchell Skaggs on 5/12/2019.
 */
object ClientMumbleLinkMod : ClientModInitializer {
    lateinit var mumble: MumbleLink

    override fun onInitializeClient() {
        println("Initializing MumbleLink Client")
        mumble = MumbleLinkImpl()

        ClientTickCallback.EVENT.register(ClientTickCallback {
            if (it.world != null /*&& !it.isInSingleplayer*/) {
                mumble.name = "Minecraft MumbleLink Mod"
                mumble.uiVersion = 2
                mumble.uiTick++

                // Location
                val location = it.player.pos.add(
                        Vec3d(0.0,
                                it.player.getEyeHeight(it.player.pose).toDouble(),
                                0.0))


                // Looking direction
                val direction = it.player.getCameraPosVec(1F)
            }
        })
    }
}
