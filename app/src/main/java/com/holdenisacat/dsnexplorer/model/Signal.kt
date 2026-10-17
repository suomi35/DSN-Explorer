package com.holdenisacat.dsnexplorer.model

data class Signal(
    val active: Boolean = false,
    val type: SignalType = SignalType.NONE,
    val dataRate: String = "",
    val frequency: String = "",
    val band: String = "",
    val power: String = "",
    val spacecraft: String = "",
    val spacecraftId: String = "",
    val direction: SignalDirection = SignalDirection.UP,
)
