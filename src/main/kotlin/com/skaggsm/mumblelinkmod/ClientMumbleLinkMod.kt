package com.skaggsm.mumblelinkmod

import com.skaggsm.jmumblelink.MumbleLink
import com.skaggsm.jmumblelink.MumbleLinkImpl
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.client.ClientTickCallback
import net.minecraft.util.math.Vec3d

/**
 * Convert to a float 3-array in a left-handed coordinate system.
 * Minecraft is right-handed by default, Mumble needs left-handed.
 *
 * @see <a href="https://wiki.mumble.info/wiki/Link#Coordinate_system">Coordinate system</a>
 */
private val Vec3d.toLHArray: FloatArray
    get() = floatArrayOf(x.toFloat(), y.toFloat(), -z.toFloat())

/**
 * Created by Mitchell Skaggs on 5/12/2019.
 */
object ClientMumbleLinkMod : ClientModInitializer {
    private var mumble: MumbleLink? = null

    override fun onInitializeClient() {
        ClientTickCallback.EVENT.register(ClientTickCallback {
            if (it.world != null) {
                val mumble = ensureLinked()

                val camPos = it.player.getCameraPosVec(1F).toLHArray
                val camDir = it.player.rotationVecClient.toLHArray
                val camTop = floatArrayOf(0f, 1f, 0f)

                mumble.uiVersion = 2
                mumble.uiTick++

                mumble.avatarPosition = camPos
                mumble.avatarFront = camDir
                mumble.avatarTop = camTop

                mumble.name = "Minecraft MumbleLink Mod"

                mumble.cameraPosition = camPos
                mumble.cameraFront = camDir
                mumble.cameraTop = camTop

                mumble.identity = it.player.uuidAsString

                mumble.context = "${it.world.dimension.type}-${it.player.scoreboardTeam?.name}"

                mumble.description = "A Minecraft mod that provides position data to Mumble."
            } else {
                ensureClosed()
            }
        })
    }

    private fun ensureLinked(): MumbleLink {
        var localMumble = mumble

        if (localMumble != null)
            return localMumble

        println("Linking to Mumble...")
        localMumble = MumbleLinkImpl()
        mumble = localMumble
        println("Linked")

        return localMumble
    }

    private fun ensureClosed() {
        if (mumble != null) {
            println("Unlinking from Mumble...")
            mumble?.close()
            mumble = null
            println("Unlinked")
        }
    }
}
