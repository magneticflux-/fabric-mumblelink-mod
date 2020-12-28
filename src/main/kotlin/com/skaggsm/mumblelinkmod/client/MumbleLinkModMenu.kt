package com.skaggsm.mumblelinkmod.client

import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod
import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod.MODID
import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
import me.shedaniel.fiber2cloth.api.Fiber2Cloth
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

/**
 * Created by Mitchell Skaggs on 5/30/2019.
 */
@Environment(EnvType.CLIENT)
class MumbleLinkModMenu : ModMenuApi {
    override fun getModId(): String = MODID

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
        ConfigScreenFactory {
            val t =
                Fiber2Cloth.create(it, modId, ClientMumbleLinkMod.unionConfigTree, "config.fabric-mumblelink-mod.title")
                    .setSaveRunnable {
                        MainMumbleLinkMod.serialize()
                        ClientMumbleLinkMod.serialize()
                    }
                    .build()
            t
                .screen
        }
}
