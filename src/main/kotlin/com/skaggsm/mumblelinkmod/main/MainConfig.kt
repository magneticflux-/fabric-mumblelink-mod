package com.skaggsm.mumblelinkmod.main

import me.shedaniel.fiber2cloth.api.ClothSetting
import me.shedaniel.fiber2cloth.api.ClothSetting.EnumHandler.EnumDisplayOption

class MainConfig {

    /**
     * Used for the "scheme" part of the URI.
     */
    @ClothSetting.EnumHandler(EnumDisplayOption.DROPDOWN)
    var voipClient: VoipClient = VoipClient.MUMBLE

    /**
     * Used for the "userinfo" part of the URI.
     */
    var voipServerUserinfo: String = ""

    /**
     * Used for the "host" part of the URI.
     */
    var voipServerHost: String = ""

    /**
     * Used for the "port" part of the URI.
     * Value of -1 means undefined.
     */
    var voipServerPort: Int = -1

    /**
     * Used for the "path" part of the URI.
     */
    var voipServerPath: String = ""

    /**
     * Used for the "query" part of the URI.
     */
    var voipServerQuery: String = ""

    /**
     * Used for the "fragment" part of the URI.
     */
    var voipServerFragment: String = ""

    enum class VoipClient(val scheme: String) {
        MUMBLE("mumble"),
        TEAMSPEAK("ts3server");
    }
}
