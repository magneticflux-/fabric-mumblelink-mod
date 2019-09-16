package com.skaggsm.mumblelinkmod.config

import com.skaggsm.mumblelinkmod.MumbleLink
import me.sargunvohra.mcmods.autoconfig1.ConfigData
import me.sargunvohra.mcmods.autoconfig1.annotation.Config

/**
 * Created by Mitchell Skaggs on 5/30/2019.
 */
@Config(name = MumbleLink.MODID)
class MumbleLinkConfig : ConfigData {
    var mumbleAutoLaunchOption: AutoLaunchOption = AutoLaunchOption.ACCEPT

    var mumbleServerHost: String? = null
    var mumbleServerPort: Int? = null
    var mumbleServerPath: String? = "/Minecraft/{1}"
    var mumbleServerQuery: String? = null

    var mumbleDimensionYAxisAdjust: Float = 0f

    enum class AutoLaunchOption {
        IGNORE,
        PROMPT,
        ACCEPT
    }
}
