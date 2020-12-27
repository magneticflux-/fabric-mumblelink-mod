package com.skaggsm.mumblelinkmod.client

import com.skaggsm.jmumblelink.MumbleLink
import com.skaggsm.jmumblelink.MumbleLinkImpl
import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod.config
import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod.log
import com.skaggsm.mumblelinkmod.main.OldConfig
import com.skaggsm.mumblelinkmod.main.OldConfig.AutoLaunchOption.ACCEPT
import com.skaggsm.mumblelinkmod.main.SendMumbleURL
import com.skaggsm.mumblelinkmod.toLHArray
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.PacketContext
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.PacketByteBuf
import org.lwjgl.system.Platform
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.net.URI
import java.net.URISyntaxException

/**
 * Created by Mitchell Skaggs on 5/12/2019.
 */
object ClientMumbleLinkMod : ClientModInitializer {
    private var mumble: MumbleLink? = null

    private fun packetConsumer(context: PacketContext, bytes: PacketByteBuf) {
        val voipClient = bytes.readEnumConstant(OldConfig.VoipClient::class.java)
        val hostParts = bytes.readString().split('@', limit = 2)
        val host = hostParts.last()
        val userinfo = if (hostParts.size > 1) hostParts.first() else null
        val port = bytes.readInt()
        val path = bytes.readString().let { if (it == "") null else it }
        val query = bytes.readString().let { if (it == "") null else it }

        try {
            val uri = URI(voipClient.scheme, userinfo, host, port, path, query, null)
            ensureNotHeadless()
            Desktop.getDesktop().browse(uri)
        } catch (e: URISyntaxException) {
            log.warn("Ignoring invalid VoIP client URI \"${e.input}\"")
        }
    }

    /**
     * Runs after [MainMumbleLinkMod.onInitialize].
     */
    override fun onInitializeClient() {
        when (config.config.mumbleAutoLaunchOption) {
            ACCEPT -> ClientSidePacketRegistry.INSTANCE.register(SendMumbleURL.ID, ClientMumbleLinkMod::packetConsumer)
            else -> {
            }
        }

        ClientTickEvents.START_CLIENT_TICK.register(ClientTickEvents.StartTick {
            val world = it.world
            val player = it.player

            if (world != null && player != null) {
                val mumble = ensureLinked()

                val camPos = player.getCameraPosVec(1F).toLHArray
                val camDir = player.rotationVecClient.toLHArray
                val camTop = floatArrayOf(0f, 1f, 0f)

                // Make people in other dimensions far away so that they're muted.
                val yAxisAdjuster = world.dimension.hashCode() * config.config.mumbleDimensionYAxisAdjust
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

    private fun ensureNotHeadless() {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("Unable to unset headless earlier (are you using OptiFine?), doing it with nasty reflection now!")
            val headlessField = GraphicsEnvironment::class.java.getDeclaredField("headless")
            headlessField.isAccessible = true
            headlessField[null] = false
        }
    }

    init {
        // OptiFine needs java.awt.headless=true on Mac because it accidentally uses an AWT class that triggers JNI stuff on load if not headless.
        // That JNI stuff fails on Mac because of course it does, so we skip it now and set java.awt.headless=false on-demand later (after OptiFine already triggered the AWT "booby-trap".
        if (FabricLoader.getInstance().isModLoaded("optifabric") && Platform.get() == Platform.MACOSX) {
            System.err.println("OptiFine needs java.awt.headless=true right now, so we'll set it later with a reflection hack!")
        } else {
            // If Optifine isn't loaded, we can just set it here and skip the hassle later.
            // Required to open URIs
            System.setProperty("java.awt.headless", "false")
        }
    }
}
