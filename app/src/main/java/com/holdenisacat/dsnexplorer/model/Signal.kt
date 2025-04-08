package com.holdenisacat.dsnexplorer.model

data class Signal(

    var active: Boolean,
    var signalType: String,
    var dataRate: String,
    var frequency: String,
    var band: String,
    var power: String,
    var spacecraft: String,
    var spacecraftId: String,
    var direction: SignalDirection

    ) {
    constructor() : this(false, "", "10", "", "", "", "", "", SignalDirection.UP)
}