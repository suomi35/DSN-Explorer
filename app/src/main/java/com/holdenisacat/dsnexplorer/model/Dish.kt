package com.holdenisacat.dsnexplorer.model

import android.net.Uri
import com.holdenisacat.dsnexplorer.util.Constants
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.math.floor

data class Dish(

    var name: String,
    var friendlyName: String,
    var type: String,
    var azimuthAngle: Float = 0f,
    var elevationAngle: Float = 0f,
    var windSpeed: String = "",
    var isMSPA: Boolean = false, // Multiple Spacecraft Per Aperture (MSPA) capability of the DSN, which allows a single DSN antenna to receive downlink from up to two spacecraft simultaneously.
    var isArray: Boolean = false,
    var isDDOR: Boolean = false, // Delta-DOR uses two widely separated antennas to simultaneously track a transmitting probe in order to measure the time difference (delay time) between signals arriving at the two stations. The technique of measuring this delay is named Differential One-way Range (DOR).
    var activity: String = "",
    var isActive: Boolean = false,
    var dishLocation: String = "",
    var signals: ArrayList<Signal> = ArrayList(),
    var spacecraftList: ArrayList<Spacecraft> = ArrayList()
) {
    constructor() : this("", "", "")

    fun getSpacecraftSignalsByDirection(spacecraft: Spacecraft, direction: SignalDirection): ArrayList<Signal> {
        val matchingSignals: ArrayList<Signal> = ArrayList()
        for (signal in signals) {
            if (signal.spacecraft.equals(spacecraft.name, ignoreCase = true) && signal.direction == direction) {
                matchingSignals.add(signal)
            }
        }
        return matchingSignals
    }

    fun getSpacecraftSignals(spacecraft: Spacecraft): ArrayList<Signal> {
        val matchingSignals: ArrayList<Signal> = ArrayList()
        for (signal in signals) {
            if (signal.spacecraft.equals(spacecraft.name, ignoreCase = true)) {
                matchingSignals.add(signal)
            }
        }
        return matchingSignals
    }

    fun getSpacecraftActiveSignals(spacecraft: Spacecraft): ArrayList<Signal> {
        val matchingSignals: ArrayList<Signal> = ArrayList()
        for (signal in signals) {
            if (signal.spacecraft.equals(spacecraft.name, ignoreCase = true) && signal.active) {
                matchingSignals.add(signal)
            }
        }
        return matchingSignals
    }

    fun getDishMode(): String {
        return if (isMSPA) {
            "MSPA"
        } else if (isArray) {
            "Array"
        } else if (isDDOR) {
            "ΔDOR"
        } else {
            ""
        }
    }

    fun getDishImageUrl(): Uri {
        var azimuth = (floor(azimuthAngle / 15) * 15).toInt()
        val elevation = (floor(elevationAngle / 10) * 10).toInt()
        if (azimuth == 360) {
            azimuth = 0
        }

        // Add the minutes in the day passed so far, for thumbnail viewing.
        val utcTime = OffsetDateTime.now(ZoneOffset.UTC)
        val minutes = utcTime.hour * 60 + utcTime.minute
        var clampedMinutes = (floor((minutes / 15).toDouble()) * 15).toInt()
        if (clampedMinutes == 1440) {
            clampedMinutes = 0
        }
        // Hack here to get DSS 56 thumbnail showing as DSS 55
        val dishName = when (name) {
            "DSS56" -> "DSS55"
            "DSS53" -> "DSS54"
            else -> name
        }

        return Uri.parse("${Constants.DISH_IMAGES_BASE_URL}${dishName}_${azimuth}_${elevation}_${clampedMinutes}.png")
    }
}