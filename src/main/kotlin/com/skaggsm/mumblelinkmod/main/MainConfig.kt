package com.skaggsm.mumblelinkmod.main

import me.shedaniel.fiber2cloth.api.ClothSetting
import me.shedaniel.fiber2cloth.api.ClothSetting.EnumHandler.EnumDisplayOption.DROPDOWN

class MainConfig {

    @ClothSetting.EnumHandler(DROPDOWN)
    var voipClient: OldConfig.VoipClient = OldConfig.VoipClient.MUMBLE

    var mumbleServerHost: String = ""

    /**
     * Value of -1 means undefined.
     */
    var mumbleServerPort: Int = -1

    var mumbleServerPath: String = ""

    var mumbleServerQuery: String = ""
}
