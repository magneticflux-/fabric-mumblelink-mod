package com.skaggsm.mumblelinkmod.config

import com.skaggsm.mumblelinkmod.MumbleLink
import me.sargunvohra.mcmods.autoconfig1.ConfigData
import me.sargunvohra.mcmods.autoconfig1.annotation.Config
import me.sargunvohra.mcmods.autoconfig1.annotation.ConfigEntry

/**
 * Created by Mitchell Skaggs on 5/30/2019.
 */
@Config(name = MumbleLink.MODID)
class MumbleLinkConfig : ConfigData {
    @ConfigEntry.Category("client")
    var mumbleAutoLaunchOption: AutoLaunchOption = AutoLaunchOption.ACCEPT
    @ConfigEntry.Category("client")
    var mumbleDimensionYAxisAdjust: Float = 0f

    @ConfigEntry.Category("server")
    var mumbleServerHost: String? = null
    @ConfigEntry.Category("server")
    var mumbleServerPort: Int? = null
    @ConfigEntry.Category("server")
    var mumbleServerPath: String? = null
    @ConfigEntry.Category("server")
    var mumbleServerQuery: String? = null

    enum class AutoLaunchOption {
        IGNORE,
        //PROMPT,
        ACCEPT
    }
}
