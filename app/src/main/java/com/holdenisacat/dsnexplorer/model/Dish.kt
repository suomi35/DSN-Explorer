package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.util.DsnApi
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.math.floor

data class Dish(
    val name: String,
    val friendlyName: String,
    val type: String,
    val azimuthAngle: Float = 0f,
    val elevationAngle: Float = 0f,
    val windSpeed: String = "",
    /** Multiple Spacecraft Per Aperture: one antenna receiving downlink from up to two spacecraft. */
    val isMSPA: Boolean = false,
    /** Two or more antennas combined to act as a single larger aperture. */
    val isArray: Boolean = false,
    /** Delta-DOR: two separated antennas measuring signal arrival delay for navigation. */
    val isDDOR: Boolean = false,
    val activity: String = "",
    val siteName: String = "",
    val signals: List<Signal> = emptyList(),
    val spacecraftList: List<Spacecraft> = emptyList(),
) {
    /** A dish is considered active when at least one of its signals is meaningful. */
    val isActive: Boolean
        get() = signals.any { it.type != SignalType.NONE }

    val mode: String
        get() = when {
            isMSPA -> "MSPA"
            isArray -> "Array"
            isDDOR -> "ΔDOR"
            else -> ""
        }

    fun signalsFor(spacecraft: Spacecraft): List<Signal> =
        signals.filter { it.spacecraft.equals(spacecraft.name, ignoreCase = true) }

    fun activeSignalsFor(spacecraft: Spacecraft): List<Signal> =
        signalsFor(spacecraft).filter { it.active }

    fun signalsFor(spacecraft: Spacecraft, direction: SignalDirection): List<Signal> =
        signalsFor(spacecraft).filter { it.direction == direction }

    /**
     * Thumbnail of the dish rendered by NASA's Eyes on the Solar System.
     *
     * Azimuth/elevation are quantized to the available artwork resolution and the
     * current UTC time is clamped to a 15-minute window. [now] is injectable for tests.
     */
    fun imageUrl(now: OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC)): String {
        val azimuth = (floor(azimuthAngle / 15) * 15).toInt().let { if (it == 360) 0 else it }
        val elevation = (floor(elevationAngle / 10) * 10).toInt()

        val minutes = now.hour * 60 + now.minute
        val clampedMinutes = (floor(minutes / 15.0) * 15).toInt().let { if (it == 1440) 0 else it }

        // DSS 56 and DSS 53 artwork are only published under their sibling dish names.
        val artworkName = when (name) {
            "DSS56" -> "DSS55"
            "DSS53" -> "DSS54"
            else -> name
        }

        return "${DsnApi.DISH_IMAGES_BASE_URL}${artworkName}_${azimuth}_${elevation}_${clampedMinutes}.png"
    }
}
