package com.skaggsm.mumblelinkmod.network

import com.skaggsm.mumblelinkmod.MumbleLink
import net.minecraft.util.Identifier

/**
 * Created by Mitchell Skaggs on 5/28/2019.
 */
interface SendMumbleURL {
    companion object {
        val ID = Identifier(MumbleLink.MODID, "broadcast_mumble_url")
    }
}
