package com.skaggsm.mumblelinkmod.client

import com.skaggsm.mumblelinkmod.main.OldConfig
import me.shedaniel.fiber2cloth.api.ClothSetting
import me.shedaniel.fiber2cloth.api.ClothSetting.EnumHandler.EnumDisplayOption.DROPDOWN
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ClientConfig {
    @ClothSetting.EnumHandler(DROPDOWN)
    @ClothSetting.RequiresRestart
    var mumbleAutoLaunchOption: OldConfig.AutoLaunchOption = OldConfig.AutoLaunchOption.ACCEPT

    var mumbleDimensionYAxisAdjust: Float = 0f
}
