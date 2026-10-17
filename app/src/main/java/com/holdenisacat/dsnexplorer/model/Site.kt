package com.holdenisacat.dsnexplorer.model

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

data class Site(
    val name: String,
    val friendlyName: String,
    val latitude: String,
    val longitude: String,
    val dishes: List<Dish> = emptyList(),
    val timeUTC: Long = 0,
    val timeZoneOffset: Long = 0,
) {
    /** Local site time formatted as `MMM d` / `h:mm a`, or empty when the feed has no clock. */
    fun niceTime(locale: Locale = Locale.getDefault()): String {
        if (timeUTC == 0L) return ""
        val siteTime = Instant.ofEpochMilli(timeUTC + timeZoneOffset).atZone(ZoneOffset.UTC)
        return NICE_TIME_FORMATTER.withLocale(locale).format(siteTime)
    }

    companion object {
        private val NICE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d\nh:mm a")
    }
}
