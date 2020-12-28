package com.skaggsm.mumblelinkmod.main

class MainConfig {

    var voipClient: OldConfig.VoipClient = OldConfig.VoipClient.MUMBLE

    var mumbleServerHost: String = ""

    /**
     * Value of -1 means undefined.
     */
    var mumbleServerPort: Int = -1

    var mumbleServerPath: String = ""

    var mumbleServerQuery: String = ""
}
