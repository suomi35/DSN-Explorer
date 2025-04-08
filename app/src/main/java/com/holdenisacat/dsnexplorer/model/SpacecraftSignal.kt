package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.util.Constants
import java.util.Locale

data class SpacecraftSignal(

    var signalType: String,
    var power: String,
    var frequency: String,
    var dataRate: String,
    var isUp: Boolean = false,

) {
    constructor() : this("", "", "", "")

    fun getSignalTypeTitleCase(): String {
        return signalType[0].toString().uppercase(Locale.getDefault()) +
                signalType.substring(1, signalType.length)
    }

    fun getSignalTypeBackground(): Int {
        return when (signalType) {
            Constants.CARRIER_SIGNAL -> R.color.backgroundDataTypeCarrierDownHeader
            Constants.DATA_SIGNAL -> R.color.backgroundDataTypeDataUpHeader
            else -> R.color.white
        }
    }
}