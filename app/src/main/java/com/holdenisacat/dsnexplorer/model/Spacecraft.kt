package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.util.Constants
import java.util.Locale

data class Spacecraft(

    var name: String,
    var explorerName: String,
    var friendlyName: String,
    var hasThumbnail: Boolean = false,
    var id: String = "",
    var uplegRange: Float = 0f,
    var downlegRange: Float = 0f,
    var roundtripLightTime: Float = 0f,
    var flag: Int = 0,
    var rangeIsEstimated: Boolean = false
) {
    constructor() : this("", "", "")

    fun getImageUrl(): String =
        "${Constants.SPACECRAFT_IMAGES_BASE_URL}${name.lowercase(Locale.ROOT)}.jpg"
}