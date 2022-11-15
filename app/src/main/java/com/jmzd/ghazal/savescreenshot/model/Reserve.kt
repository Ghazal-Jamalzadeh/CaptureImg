package com.jmzd.ghazal.savescreenshot.model

import java.io.Serializable

/**
 * Created by Ghazal on 11/14/2022.
 */
data class Reserve (
    var num : Int = 0,
    var firstNeme : String  ="",
    var lastName : String = "",
    var mobile : String = "",
    var time : String =""
): Serializable
