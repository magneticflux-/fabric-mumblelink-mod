package com.skaggsm.mumblelinkmod

import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import org.lwjgl.system.Platform

object MumbleLinkPreLaunchEntrypoint : PreLaunchEntrypoint {
    override fun onPreLaunch() {
        // This is all done in onPreLaunch because other mods might call "Desktop.getDesktop()" before we change the property, which breaks everything!

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
