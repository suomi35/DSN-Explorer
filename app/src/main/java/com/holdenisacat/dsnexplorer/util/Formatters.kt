package com.holdenisacat.dsnexplorer.util

import java.util.Locale

/**
 * Pure, side-effect-free value formatting for DSN telemetry.
 *
 * Every entry point is null/blank tolerant: unparseable feed values render as `-`
 * instead of crashing the UI. All number formatting is locale-stable so previews,
 * screenshots and tests are deterministic.
 */
object Formatters {

    const val EMPTY_VALUE = "-"

    private const val SPEED_OF_LIGHT_KM_PER_SEC = 299_792.458f

    /** Formats a frequency given in Hz, e.g. `8420031518` -> `8.42 GHz`. */
    fun frequency(hz: String): String = scale(hz, "Hz", "kHz", "MHz", "GHz")

    /** Formats a data rate given in bits per second, e.g. `20000` -> `20.00 kbps`. */
    fun dataRate(bps: String): String = scale(bps, "bps", "kbps", "Mbps")

    /** Formats signal power: positive values are kW, negative values are dBm. */
    fun power(value: String): String {
        val power = value.toDoubleOrNull() ?: return EMPTY_VALUE
        val unit = if (power > 0) "kW" else "dBm"
        return "${decimal(power)} $unit"
    }

    /** Formats a distance in km with a `~` prefix when the value is an estimate. */
    fun distance(km: Float, estimated: Boolean = false): String {
        val formatted = when {
            km <= 0f -> EMPTY_VALUE
            else -> when {
                km < 1_000f -> "${decimal(km.toDouble())} km"
                km < 1_000_000f -> "${decimal((km / 1_000f).toDouble())} thousand km"
                km < 1_000_000_000f -> "${decimal((km / 1_000_000f).toDouble())} million km"
                km < 1_000_000_000_000f -> "${decimal((km / 1_000_000_000f).toDouble())} billion km"
                else -> "${decimal((km / 1_000_000_000_000f).toDouble())} trillion km"
            }
        }
        return formatted.withEstimateMarker(estimated)
    }

    /** Formats a duration in seconds with a `~` prefix when the value is an estimate. */
    fun lightTime(seconds: Float, estimated: Boolean = false): String {
        val formatted = when {
            seconds <= 0f -> EMPTY_VALUE
            seconds < 60f -> "${decimal(seconds.toDouble())} sec"
            seconds < 3_600f -> "${decimal((seconds / 60f).toDouble())} min"
            seconds < 86_400f -> "${decimal((seconds / 3_600f).toDouble())} hrs"
            seconds < 604_800f -> "${decimal((seconds / 86_400f).toDouble())} days"
            else -> "1+ wk"
        }
        return formatted.withEstimateMarker(estimated)
    }

    /** Round-trip light time in seconds for a one-way range in km, or `-1` when unknown. */
    fun roundTripLightTime(rangeKm: Float): Float =
        if (rangeKm <= 0f) UNKNOWN_RANGE else 2f * rangeKm / SPEED_OF_LIGHT_KM_PER_SEC

    private const val UNKNOWN_RANGE = -1f

    private fun scale(value: String, vararg units: String): String {
        var scaled = value.toDoubleOrNull() ?: return EMPTY_VALUE
        if (scaled < 0) return EMPTY_VALUE
        for (unit in units) {
            if (scaled < 1000.0 || unit == units.last()) {
                return "${decimal(scaled)} $unit"
            }
            scaled /= 1000.0
        }
        return EMPTY_VALUE
    }

    private fun decimal(value: Double): String = String.format(Locale.US, "%.2f", value)

    private fun String.withEstimateMarker(estimated: Boolean): String =
        if (estimated && this != EMPTY_VALUE) "~$this" else this
}
