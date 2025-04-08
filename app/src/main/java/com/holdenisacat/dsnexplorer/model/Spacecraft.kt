package com.holdenisacat.dsnexplorer.model

data class Spacecraft(

    var name: String,
    var explorerName: String,
    var friendlyName: String,
    var thumbnail: Boolean = false
) {
    constructor() : this("", "", "")
}