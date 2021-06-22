package com.skaggsm.mumblelinkmod.client

import me.shedaniel.fiber2cloth.api.ClothSetting
import me.shedaniel.fiber2cloth.api.ClothSetting.EnumHandler.EnumDisplayOption.DROPDOWN
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ClientConfig {
    @ClothSetting.EnumHandler(DROPDOWN)
    var clientAutoLaunchOption: AutoLaunchOption = AutoLaunchOption.ACCEPT

    var clientDimensionYAxisAdjust: Float = 0f

    enum class AutoLaunchOption {
        IGNORE, // PROMPT,
        ACCEPT
    }
}
