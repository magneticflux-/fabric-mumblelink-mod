package com.skaggsm.mumblelinkmod

import com.skaggsm.jmumblelink.MumbleLink
import com.skaggsm.jmumblelink.MumbleLinkImpl
import com.skaggsm.mumblelinkmod.MumbleLinkMod.log
import com.skaggsm.mumblelinkmod.config.MumbleLinkConfig
import com.skaggsm.mumblelinkmod.config.MumbleLinkConfig.AutoLaunchOption.ACCEPT
import com.skaggsm.mumblelinkmod.config.MumbleLinkConfig.AutoLaunchOption.IGNORE
import com.skaggsm.mumblelinkmod.network.SendMumbleURL
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.event.client.ClientTickCallback
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.util.math.Vec3d
import java.awt.Desktop
import java.net.URI
import java.net.URISyntaxException

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
        ClientSidePacketRegistry.INSTANCE.register(SendMumbleURL.ID) { _, bytes ->
            when (MumbleLinkMod.config.config.mumbleAutoLaunchOption) {
                ACCEPT -> {
                    val voipClient = MumbleLinkConfig.VoipClient.values()[bytes.readInt()]
                    val host = bytes.readString()
                    val port = bytes.readInt()
                    val path = bytes.readString().let { if (it == "") null else it }
                    val query = bytes.readString().let { if (it == "") null else it }

                    try {
                        val uri = URI(voipClient.scheme, null, host, port, path, query, null)
                        Desktop.getDesktop().browse(uri)
                    } catch (e: URISyntaxException) {
                        log.warn("Ignoring invalid VoIP client URI \"${e.input}\"")
                    }
                }
                IGNORE -> {
                }
            }
        }

        ClientTickCallback.EVENT.register(ClientTickCallback {
            val world = it.world
            val player = it.player

            if (world != null && player != null) {
                val mumble = ensureLinked()

                val camPos = player.getCameraPosVec(1F).toLHArray
                val camDir = player.rotationVecClient.toLHArray
                val camTop = floatArrayOf(0f, 1f, 0f)

                // Make people in other dimensions far away so that they're muted.
                val yAxisAdjuster = world.dimension.type.rawId * MumbleLinkMod.config.config.mumbleDimensionYAxisAdjust
                camPos[1] += yAxisAdjuster

                mumble.uiVersion = 2
                mumble.uiTick++

                mumble.avatarPosition = camPos
                mumble.avatarFront = camDir
                mumble.avatarTop = camTop

                mumble.name = "Minecraft Mumble Link Mod"

                mumble.cameraPosition = camPos
                mumble.cameraFront = camDir
                mumble.cameraTop = camTop

                mumble.identity = player.uuidAsString

                mumble.context = "Minecraft"

                mumble.description = "A Minecraft mod that provides position data to VoIP clients."
            } else {
                ensureClosed()
            }
        })
    }

    private fun ensureLinked(): MumbleLink {
        var localMumble = mumble

        if (localMumble != null)
            return localMumble

        log.info("Linking to VoIP client...")
        localMumble = MumbleLinkImpl()
        mumble = localMumble
        log.info("Linked")

        return localMumble
    }

    private fun ensureClosed() {
        if (mumble != null) {
            log.info("Unlinking from VoIP client...")
            mumble?.close()
            mumble = null
            log.info("Unlinked")
        }
    }

    init {
        // Required to open URIs
        System.setProperty("java.awt.headless", "false")
    }
}
