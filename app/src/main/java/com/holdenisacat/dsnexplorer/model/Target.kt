package com.holdenisacat.dsnexplorer.model

data class Target(

    var name: String,
    var id: String,
    var uplegRange: Float,
    var downlegRange: Float,
    var roundtripLightTime: Float,
    var friendlyName: String,
    var hasThumbnail: Boolean,
    var flag: Int = 0
) {
    constructor() : this("", "", 0F, 0F, 0F, "", false)
}