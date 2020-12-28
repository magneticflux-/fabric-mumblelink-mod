package com.skaggsm.mumblelinkmod.client

import com.skaggsm.mumblelinkmod.main.OldConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment

@Environment(EnvType.CLIENT)
class ClientConfig {
    var mumbleAutoLaunchOption: OldConfig.AutoLaunchOption = OldConfig.AutoLaunchOption.ACCEPT

    var mumbleDimensionYAxisAdjust: Float = 0f
}
