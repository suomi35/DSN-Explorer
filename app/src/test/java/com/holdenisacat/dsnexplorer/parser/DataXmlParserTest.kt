package com.holdenisacat.dsnexplorer.parser

import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.model.SignalType
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.InputStream

class DataXmlParserTest {

    private val parser = DataXmlParser()

    @Test
    fun parseExtractsSitesDishesAndSignals() {
        val config = buildConfiguration()
        val inputStream: InputStream = javaClass.classLoader!!.getResourceAsStream("dsn.xml")
            ?: error("dsn.xml not found on test classpath")
        val dsn: Dsn = parser.parse(config, inputStream)

        assertEquals(2, dsn.sites.size)

        val madrid = dsn.sites.first { it.name == "mdscc" }
        assertNotNull(madrid)
        assertEquals(1735689600000L, madrid.timeUTC)
        assertEquals(3600000L, madrid.timeZoneOffset)

        val dss53 = madrid.dishes.first { it.name == "DSS53" }
        assertEquals(276f, dss53.azimuthAngle, 0.001f)
        assertEquals(35f, dss53.elevationAngle, 0.001f)
        assertEquals("9", dss53.windSpeed)
        assertEquals("Spacecraft Telemetry, Tracking, and Command", dss53.activity)
        assertEquals(2, dss53.signals.size)

        val upSignal = dss53.signals.first { it.direction == SignalDirection.UP }
        assertEquals(SignalType.DATA, upSignal.type)
        assertEquals("20000", upSignal.dataRate)
        assertEquals("8420031518", upSignal.frequency)
        assertEquals("X", upSignal.band)
        assertEquals("-499", upSignal.power)
        assertEquals("m01o", upSignal.spacecraft)
        assertEquals("53", upSignal.spacecraftId)
        assertTrue(upSignal.active)

        val downSignal = dss53.signals.first { it.direction == SignalDirection.DOWN }
        assertEquals(SignalType.CARRIER, downSignal.type)
        assertTrue(downSignal.active)
    }

    @Test
    fun parseExtractsSpacecraftTargets() {
        val config = buildConfiguration()
        val inputStream: InputStream = javaClass.classLoader!!.getResourceAsStream("dsn.xml")
            ?: error("dsn.xml not found on test classpath")
        val dsn: Dsn = parser.parse(config, inputStream)

        val madrid = dsn.sites.first { it.name == "mdscc" }
        val dss53 = madrid.dishes.first { it.name == "DSS53" }
        assertEquals(1, dss53.spacecraftList.size)

        val odyssey = dss53.spacecraftList.first()
        assertEquals("M01O", odyssey.name)
        assertEquals("53", odyssey.explorerName)
        assertEquals("Mars Odyssey", odyssey.friendlyName)
        assertEquals(173_000_000f, odyssey.uplegRange, 0.001f)
        assertEquals(173_000_000f, odyssey.downlegRange, 0.001f)
        assertEquals(1190f, odyssey.roundtripLightTime, 0.001f)
    }

    @Test
    fun parseSkipsOrphanDishesNotInConfiguration() {
        val emptyConfig = Configuration(sites = emptyList(), spacecraft = emptyList())
        val inputStream: InputStream = javaClass.classLoader!!.getResourceAsStream("dsn.xml")
            ?: error("dsn.xml not found on test classpath")
        val dsn: Dsn = parser.parse(emptyConfig, inputStream)

        // All stations should be skipped since no config sites exist
        assertEquals(0, dsn.sites.size)
    }

    @Test
    fun parseHandlesUnknownSignalType() {
        val config = buildConfiguration()
        val inputStream: InputStream = javaClass.classLoader!!.getResourceAsStream("dsn.xml")
            ?: error("dsn.xml not found on test classpath")
        val dsn: Dsn = parser.parse(config, inputStream)

        val gdscc = dsn.sites.first { it.name == "gdscc" }
        val dss26 = gdscc.dishes.first { it.name == "DSS26" }
        val rangingSignal = dss26.signals.first { it.type == SignalType.RANGING }
        assertNotNull(rangingSignal)
    }

    private fun buildConfiguration(): Configuration {
        return Configuration(
            sites = listOf(
                Site(
                    name = "mdscc",
                    friendlyName = "Madrid",
                    latitude = "40.2413554",
                    longitude = "-4.2480085",
                    dishes = listOf(
                        Dish(name = "DSS53", friendlyName = "DSS 53", type = "34M"),
                        Dish(name = "DSS54", friendlyName = "DSS 54", type = "70M"),
                    ),
                ),
                Site(
                    name = "gdscc",
                    friendlyName = "Goldstone",
                    latitude = "35.4267",
                    longitude = "-116.8896",
                    dishes = listOf(
                        Dish(name = "DSS26", friendlyName = "DSS 26", type = "34M"),
                    ),
                ),
            ),
            spacecraft = listOf(
                Spacecraft(name = "M01O", explorerName = "53", friendlyName = "Mars Odyssey", hasThumbnail = true),
                Spacecraft(name = "MVN", explorerName = "202", friendlyName = "MAVEN", hasThumbnail = true),
            ),
        )
    }
}
