package com.holdenisacat.dsnexplorer.model

/**
 * The two top-level browse modes of the app. Ordinal is persisted as the user's last choice.
 */
enum class Mode {
    SITES,
    SPACECRAFT;

    companion object {
        fun fromIndex(index: Int): Mode = entries.getOrElse(index) { SITES }
    }
}
