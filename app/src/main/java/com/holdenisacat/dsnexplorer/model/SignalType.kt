package com.holdenisacat.dsnexplorer.model

/**
 * The kind of radio signal a DSN antenna is exchanging with a spacecraft.
 *
 * [rawValue] matches the `signalType` attribute of the NASA DSN XML feed.
 */
enum class SignalType(val rawValue: String) {
    CARRIER("carrier"),
    DATA("data"),
    RANGING("ranging"),
    NONE("none");

    companion object {
        fun fromRaw(raw: String?): SignalType =
            entries.firstOrNull { it.rawValue.equals(raw, ignoreCase = true) } ?: NONE
    }
}
