package com.skaggsm.mumblelinkmod.client

import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod.MODID
import com.skaggsm.mumblelinkmod.main.OldConfig
import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.Screen
import java.util.function.Function

/**
 * Created by Mitchell Skaggs on 5/30/2019.
 */
@Environment(EnvType.CLIENT)
class MumbleLinkModMenu : ModMenuApi {
    override fun getModId(): String = MODID

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
            ConfigScreenFactory { screen ->
                AutoConfig.getConfigScreen(OldConfig::class.java, screen).get()
            }

    override fun getConfigScreenFactory(): Function<Screen, out Screen> =
            Function { screen ->
                AutoConfig.getConfigScreen(OldConfig::class.java, screen).get()
            }
}
