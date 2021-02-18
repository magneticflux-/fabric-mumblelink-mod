package com.skaggsm.mumblelinkmod.mixin;

import com.skaggsm.mumblelinkmod.MumbleLinkMod;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.main.Main;
import org.lwjgl.system.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This class derived from https://github.com/comp500/ScreenshotToClipboard/blob/1.16-fabric/src/main/java/link/infra/screenshotclipboard/mixin/AWTHackMixin.java, licensed MIT.
 */
@Mixin(Main.class)
public class AWTHackMixin {
    // Inject as early as possible (but after Main statics execute), and disable java.awt.headless on non-macOS systems
    @Inject(method = "main", at = @At("HEAD"))
    private static void awtHack(CallbackInfo ci) {
        // OptiFine needs java.awt.headless=true on Mac because it accidentally uses an AWT class that triggers JNI stuff on load if not headless.
        // That JNI stuff fails on Mac because of course it does, so we skip it now and set java.awt.headless=false on-demand later (after OptiFine already triggered the AWT "booby-trap").
        if (FabricLoader.getInstance().isModLoaded("optifabric") && Platform.get() == Platform.MACOSX) {
            MumbleLinkMod.INSTANCE.getLog().error("OptiFine needs java.awt.headless=true right now, so we'll set it later with a reflection hack!");
        } else {
            // If OptiFine isn't loaded, we can just set it here and skip the hassle later.
            // Required to open URIs
            MumbleLinkMod.INSTANCE.getLog().info("No suspicious mods loaded or not on macOS, so setting java.awt.headless=false now!");
            System.setProperty("java.awt.headless", "false");
        }
    }

}
