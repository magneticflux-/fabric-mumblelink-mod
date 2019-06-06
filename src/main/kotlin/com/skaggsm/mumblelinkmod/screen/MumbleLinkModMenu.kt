package com.skaggsm.mumblelinkmod.screen

import com.skaggsm.mumblelinkmod.MumbleLink
import com.skaggsm.mumblelinkmod.config.MumbleLinkConfig
import io.github.prospector.modmenu.api.ModMenuApi
import me.sargunvohra.mcmods.autoconfig1.AutoConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import java.util.function.Function

/**
 * Created by Mitchell Skaggs on 5/30/2019.
 */
@Environment(EnvType.CLIENT)
class MumbleLinkModMenu : ModMenuApi {
    override fun getModId(): String = MumbleLink.MODID

    override fun getConfigScreenFactory(): Function<Screen, out Screen> =
            Function { screen ->
                AutoConfig.getConfigScreen(MumbleLinkConfig::class.java, screen).get()
            }
}
