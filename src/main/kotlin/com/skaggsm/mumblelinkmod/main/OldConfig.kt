package com.skaggsm.mumblelinkmod.main

import com.skaggsm.mumblelinkmod.main.MainMumbleLinkMod.MODID
import com.skaggsm.mumblelinkmod.main.OldConfig.AutoLaunchOption.ACCEPT
import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry

/**
 * Created by Mitchell Skaggs on 5/30/2019.
 */
@Config(name = MODID)
class OldConfig : ConfigData {
    @ConfigEntry.Category("client")
    var mumbleAutoLaunchOption: AutoLaunchOption = ACCEPT

    @ConfigEntry.Category("client")
    var mumbleDimensionYAxisAdjust: Float = 0f

    @ConfigEntry.Category("server")
    var voipClient: VoipClient = VoipClient.MUMBLE

    @ConfigEntry.Category("server")
    var mumbleServerHost: String? = null

    @ConfigEntry.Category("server")
    var mumbleServerPort: Int? = null

    @ConfigEntry.Category("server")
    var mumbleServerPath: String? = null

    @ConfigEntry.Category("server")
    var mumbleServerQuery: String? = null

    enum class AutoLaunchOption {
        IGNORE, //PROMPT,
        ACCEPT
    }

    enum class VoipClient(val scheme: String) {
        MUMBLE("mumble"),
        TEAMSPEAK("ts3server")
    }
}
