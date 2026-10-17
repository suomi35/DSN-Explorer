package com.holdenisacat.dsnexplorer.parser

import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import org.junit.Assert.assertEquals
import org.junit.Test
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream

class ConfigXmlParserTest {

    private val parser = ConfigXmlParser()

    @Test
    fun parseExtractsSitesAndDishes() {
        val config: Configuration = loadConfig()

        assertEquals(2, config.sites.size)

        val madrid = config.sites.first { it.name == "mdscc" }
        assertEquals("Madrid", madrid.friendlyName)
        assertEquals("40.2413554", madrid.latitude)
        assertEquals("-4.2480085", madrid.longitude)
        assertEquals(2, madrid.dishes.size)

        val dss53 = madrid.dishes.first { it.name == "DSS53" }
        assertEquals("DSS 53", dss53.friendlyName)
        assertEquals("34M", dss53.type)

        val dss54 = madrid.dishes.first { it.name == "DSS54" }
        assertEquals("DSS 54", dss54.friendlyName)
        assertEquals("70M", dss54.type)

        val goldstone = config.sites.first { it.name == "gdscc" }
        assertEquals("Goldstone", goldstone.friendlyName)
        assertEquals(1, goldstone.dishes.size)
        assertEquals("DSS26", goldstone.dishes.first().name)
    }

    @Test
    fun parseExtractsSpacecraft() {
        val config: Configuration = loadConfig()

        assertEquals(2, config.spacecraft.size)

        val odyssey = config.spacecraft.first { it.name == "M01O" }
        assertEquals("53", odyssey.explorerName)
        assertEquals("Mars Odyssey", odyssey.friendlyName)
        assertEquals(true, odyssey.hasThumbnail)

        val maven = config.spacecraft.first { it.name == "MVN" }
        assertEquals("202", maven.explorerName)
        assertEquals("MAVEN", maven.friendlyName)
    }

    @Test
    fun siteByNameFindsCaseInsensitively() {
        val config: Configuration = loadConfig()
        assertEquals("mdscc", config.siteByName("MDSCC")!!.name)
        assertEquals("mdscc", config.siteByName("mdscc")!!.name)
        assertNull(config.siteByName("nonexistent"))
    }

    @Test
    fun spacecraftByNameFindsCaseInsensitively() {
        val config: Configuration = loadConfig()
        assertEquals("M01O", config.spacecraftByName("m01o")!!.name)
        assertNull(config.spacecraftByName("nonexistent"))
    }

    private fun loadConfig(): Configuration {
        val inputStream: InputStream = javaClass.classLoader!!.getResourceAsStream("config.xml")
            ?: error("config.xml not found on test classpath")
        return parser.parse(inputStream)
    }

    private fun assertNull(value: Any?) {
        org.junit.Assert.assertNull(value)
    }
}
