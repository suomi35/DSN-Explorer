package com.holdenisacat.dsnexplorer.util

import android.util.Log
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import timber.log.Timber
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.floor

object Utils {

    private val formatter: NumberFormat = DecimalFormat("#0.00")
    private val formatterTenths: NumberFormat = DecimalFormat("#0.0")
    private val formatterWhole: NumberFormat = DecimalFormat("#0")

    fun getSiteFlag(name: String?): Int {
        return when (name) {
            "mdscc" -> R.drawable.flag_mdscc
            "gdscc" -> R.drawable.flag_gdscc
            "cdscc" -> R.drawable.flag_cdscc
            else -> R.drawable.flag_unknown
        }
    }

    fun frequencyToString(frequency: String): String { // assumes frequency in Hz
        when (frequency.isNotEmpty()) {
            true -> {
                val frequencyNum = frequency.toDouble()
                return when {
                    frequencyNum < 1000f -> "${formatter.format(frequencyNum)} Hz"
                    frequencyNum < 1000000f -> "${formatter.format((frequencyNum / 1000))} kHz"
                    frequencyNum < 1000000000f -> "${formatter.format((frequencyNum / 1000000))} MHz"
                    frequencyNum < 1000000000000f -> "${formatter.format((frequencyNum / 1000000000))} GHz"
                    else -> "1+ THz"
                }
            }

            false -> return "-"
        }
    }

    fun dataRateToString(dataRate: String): String { // assumes dataRate in b/sec
        when (dataRate.isNotEmpty()) {
            true -> {
                val dataRateDouble = dataRate.toDouble()
                return when {
                    dataRateDouble < 1000f -> "${formatter.format(dataRateDouble)} bps"
                    dataRateDouble < 1000000f -> "${formatter.format((dataRateDouble / 1000))} kbps"
                    dataRateDouble < 1000000000f -> "${formatter.format((dataRateDouble / 1000000))} Mbps"
                    else -> "1+ Tb/sec"
                }
            }

            false -> return "-"
        }
    }

    fun powerToString(power: String): String { // assumes power in kW or dBm (if negative)
        when (power.isNotEmpty()) {
            true -> {
                val powerNum = power.toDouble()
                return when (powerNum > 0) {
                    true -> "${formatter.format(powerNum)} kW"
                    false -> "${formatter.format(powerNum)} dBm"
                }
            }

            false -> return "-"
        }
    }

    fun getSiteMinutes(timeUTC: Long): Int {
        if (timeUTC != 0L) {
            val siteTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            siteTime.timeInMillis = timeUTC
            val hour = siteTime[Calendar.HOUR_OF_DAY]
            Timber.tag("Utils").d("hour: $hour")
            val minute = siteTime[Calendar.MINUTE]
            Timber.tag("Utils").d("minute: $minute")
            return ((hour * 60) + minute)
        } else {
            return 0
        }
    }

    fun angleToString(angle: Float): String { // assumes speed in km/hr
        return if (angle != 0.0f) {
            "${formatterTenths.format(angle.toDouble())}º"
        } else {
            "-"
        }
    }

    fun speedToString(speed: Float): String { // assumes speed in km/hr
        return if (speed != 0.0f) {
            "${formatterTenths.format(speed.toDouble())} km/hr"
        } else {
            "-"
        }
    }

    fun distanceToString(distance: Float): String { // assumes distance in km
        println(">>> distance IN: $distance")
        return if (distance != 0.0f) {
            when {
                distance == -1f -> "-"
                distance < 1000f -> "${formatter.format((distance / 1f).toDouble())} km"
                distance < 1000000f -> "${formatter.format((distance / 1000f).toDouble())} thousand km"
                distance < 1000000000f -> "${formatter.format((distance / 1000000f).toDouble())} million km"
                distance < 1000000000000f -> "${formatter.format((distance / 1000000000f).toDouble())} billion km"
                distance < 1000000000000000f -> "${formatter.format((distance / 1000000000000f).toDouble())} trillion km" // hey, someday
                else -> "1+ quadrillion km" // hey, some Eon
            }
        } else {
            "-"
        }
    }

    fun lightTimeToString(lightTime: Float): String { // assumes lightTime in sec
        return if (lightTime != 0.0f) {
            when {
                lightTime == -1f -> "-"
                lightTime < 60 -> "${formatter.format((lightTime / 1).toDouble())} sec"
                lightTime < 3600 -> "${formatter.format((lightTime / 60).toDouble())} min"
                lightTime < 86400 -> "${formatter.format((lightTime / 3600).toDouble())} hrs"
                lightTime < 604800 -> "${formatter.format((lightTime / 86400).toDouble())} days"
                else -> "1+ wk"
            }
        } else {
            return "-"
        }
    }
}