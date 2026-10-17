package com.holdenisacat.dsnexplorer.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun frequencyFormatsHzValues() {
        assertEquals("8.42 GHz", Formatters.frequency("8420031518"))
        assertEquals("2.45 GHz", Formatters.frequency("2450000000"))
        assertEquals("1.50 MHz", Formatters.frequency("1500000"))
        assertEquals("5.00 kHz", Formatters.frequency("5000"))
        assertEquals("100.00 Hz", Formatters.frequency("100"))
    }

    @Test
    fun frequencyHandlesUnparseable() {
        assertEquals("-", Formatters.frequency(""))
        assertEquals("-", Formatters.frequency("not-a-number"))
        assertEquals("-", Formatters.frequency("abc123"))
    }

    @Test
    fun dataRateFormatsBpsValues() {
        assertEquals("20.00 kbps", Formatters.dataRate("20000"))
        assertEquals("1.50 Mbps", Formatters.dataRate("1500000"))
        assertEquals("500.00 bps", Formatters.dataRate("500"))
    }

    @Test
    fun dataRateHandlesUnparseable() {
        assertEquals("-", Formatters.dataRate(""))
        assertEquals("-", Formatters.dataRate("abc"))
    }

    @Test
    fun powerFormatsPositiveAsKw() {
        assertEquals("10.50 kW", Formatters.power("10.5"))
        assertEquals("0.01 kW", Formatters.power("0.01"))
    }

    @Test
    fun powerFormatsZeroAsDbm() {
        assertEquals("0.00 dBm", Formatters.power("0"))
    }

    @Test
    fun powerFormatsNegativeAsDbm() {
        assertEquals("-499.00 dBm", Formatters.power("-499"))
        assertEquals("-0.50 dBm", Formatters.power("-0.5"))
    }

    @Test
    fun powerHandlesUnparseable() {
        assertEquals("-", Formatters.power(""))
        assertEquals("-", Formatters.power("not-a-number"))
    }

    @Test
    fun distanceFormatsKmRanges() {
        assertEquals("500.00 km", Formatters.distance(500f))
        assertEquals("1.50 thousand km", Formatters.distance(1500f))
        assertEquals("2.50 million km", Formatters.distance(2_500_000f))
        assertEquals("1.50 billion km", Formatters.distance(1_500_000_000f))
        assertEquals("2.00 trillion km", Formatters.distance(2_000_000_000_000f))
    }

    @Test
    fun distanceReturnsEmptyForZeroOrNegative() {
        assertEquals("-", Formatters.distance(0f))
        assertEquals("-", Formatters.distance(-1f))
    }

    @Test
    fun distanceAddsEstimateMarker() {
        assertEquals("~500.00 km", Formatters.distance(500f, estimated = true))
        assertEquals("500.00 km", Formatters.distance(500f, estimated = false))
        assertEquals("-", Formatters.distance(0f, estimated = true))
    }

    @Test
    fun lightTimeFormatsDurations() {
        assertEquals("30.00 sec", Formatters.lightTime(30f))
        assertEquals("1.50 min", Formatters.lightTime(90f))
        assertEquals("2.00 hrs", Formatters.lightTime(7200f))
        assertEquals("1.00 days", Formatters.lightTime(86400f))
        assertEquals("1+ wk", Formatters.lightTime(700000f))
    }

    @Test
    fun lightTimeReturnsEmptyForZeroOrNegative() {
        assertEquals("-", Formatters.lightTime(0f))
        assertEquals("-", Formatters.lightTime(-1f))
    }

    @Test
    fun lightTimeAddsEstimateMarker() {
        assertEquals("~30.00 sec", Formatters.lightTime(30f, estimated = true))
        assertEquals("30.00 sec", Formatters.lightTime(30f, estimated = false))
    }

    @Test
    fun roundTripLightTimeReturnsMinusOneForUnknown() {
        assertEquals(-1f, Formatters.roundTripLightTime(-1f), 0.001f)
        assertEquals(-1f, Formatters.roundTripLightTime(0f), 0.001f)
    }

    @Test
    fun roundTripLightTimeComputesFromRange() {
        // 149597870.7 km * 2 / 299792.458 km/s ≈ 998.0 seconds
        val result = Formatters.roundTripLightTime(149_597_870.7f)
        assertEquals(998.0f, result, 0.5f)
    }
}
