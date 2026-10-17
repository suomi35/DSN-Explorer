package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.util.DsnApi
import com.holdenisacat.dsnexplorer.util.Formatters
import com.holdenisacat.dsnexplorer.util.SiteFlags

data class Spacecraft(
    val name: String,
    val explorerName: String,
    val friendlyName: String,
    val hasThumbnail: Boolean = false,
    val id: String = "",
    val uplegRange: Float = UNKNOWN_RANGE,
    val downlegRange: Float = UNKNOWN_RANGE,
    val roundtripLightTime: Float = UNKNOWN_RANGE,
    val siteName: String = "",
    val rangeIsEstimated: Boolean = false,
) {
    /** Flag of the complex currently tracking this spacecraft, for display next to its name. */
    val flagRes: Int
        get() = SiteFlags.flagFor(siteName)

    /** Earth-spacecraft distance, marked with `~` when sourced from JPL Horizons. */
    val formattedRange: String
        get() = Formatters.distance(uplegRange, rangeIsEstimated)

    /** Round-trip light time, marked with `~` when sourced from JPL Horizons. */
    val formattedRoundtripLightTime: String
        get() = Formatters.lightTime(roundtripLightTime, rangeIsEstimated)

    fun imageUrl(): String =
        "${DsnApi.SPACECRAFT_IMAGES_BASE_URL}${name.lowercase()}.jpg"

    companion object {
        /** Sentinel used by the DSN feed when a range or light-time value is unavailable. */
        const val UNKNOWN_RANGE: Float = -1f
    }
}
