package com.skaggsm.mumblelinkmod.main

import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod.MODID
import net.minecraft.util.Identifier

/**
 * Created by Mitchell Skaggs on 5/28/2019.
 */
interface SendMumbleURL {
    companion object {
        val ID = Identifier(MODID, "broadcast_mumble_url")
    }
}
