package com.skaggsm.mumblelinkmod.client

import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod
import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod.MODID
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.fiber2cloth.api.Fiber2Cloth
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

/**
 * Created by Mitchell Skaggs on 5/30/2019.
 */
@Environment(EnvType.CLIENT)
class MumbleLinkModMenu : ModMenuApi {

    override fun getModConfigScreenFactory() = ConfigScreenFactory {
        val t = Fiber2Cloth.create(it, MODID, ClientMumbleLinkMod.unionConfigTree, "config.fabric-mumblelink-mod.title")
            .setSaveRunnable {
                MainMumbleLinkMod.serialize()
                ClientMumbleLinkMod.serialize()
            }
            .build()
        t.screen
    }
}
