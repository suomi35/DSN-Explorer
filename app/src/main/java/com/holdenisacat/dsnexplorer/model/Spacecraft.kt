package com.holdenisacat.dsnexplorer.model

data class Spacecraft(

    var name: String,
    var explorerName: String,
    var friendlyName: String,
    var hasThumbnail: Boolean = false,
    var id: String = "",
    var uplegRange: Float = 0f,
    var downlegRange: Float = 0f,
    var roundtripLightTime: Float = 0f,
    var flag: Int = 0
) {
    constructor() : this("", "", "")
}