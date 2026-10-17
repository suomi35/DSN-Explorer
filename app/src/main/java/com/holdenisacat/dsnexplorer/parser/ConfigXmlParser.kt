package com.holdenisacat.dsnexplorer.parser

import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.util.DsnXml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
/**
 * Parses the static DSN `config.xml` feed into a [Configuration].
 *
 * The handler is stateless: all parse state lives on the stack, so a single
 * instance can safely be shared and reused across refresh cycles.
 *
 * @throws XmlPullParserException, IOException on malformed input; callers decide how to surface it.
 */
class ConfigXmlParser {

    fun parse(inputStream: InputStream): Configuration {
        val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser().apply {
            setInput(inputStream, null)
        }

        val sites = mutableListOf<Site>()
        val spacecraft = mutableListOf<Spacecraft>()

        var siteName = ""
        var siteFriendlyName = ""
        var siteLatitude = ""
        var siteLongitude = ""
        var siteDishes: MutableList<Dish>? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    DsnXml.SITE -> {
                        siteName = parser.attr(DsnXml.NAME)
                        siteFriendlyName = parser.attr(DsnXml.FRIENDLY_NAME)
                        siteLatitude = parser.attr(DsnXml.LATITUDE)
                        siteLongitude = parser.attr(DsnXml.LONGITUDE)
                        siteDishes = mutableListOf()
                    }

                    DsnXml.DISH -> siteDishes?.add(
                        Dish(
                            name = parser.attr(DsnXml.NAME),
                            friendlyName = parser.attr(DsnXml.FRIENDLY_NAME),
                            type = parser.attr(DsnXml.TYPE),
                        )
                    )

                    DsnXml.SPACECRAFT -> spacecraft.add(
                        Spacecraft(
                            name = parser.attr(DsnXml.NAME),
                            explorerName = parser.attr(DsnXml.EXPLORER_NAME),
                            friendlyName = parser.attr(DsnXml.FRIENDLY_NAME),
                            hasThumbnail = parser.attr(DsnXml.THUMBNAIL) == DsnXml.BOOLEAN_TRUE,
                        )
                    )
                }

                XmlPullParser.END_TAG -> if (parser.name == DsnXml.SITE) {
                    sites.add(
                        Site(
                            name = siteName,
                            friendlyName = siteFriendlyName,
                            latitude = siteLatitude,
                            longitude = siteLongitude,
                            dishes = siteDishes.orEmpty().sortedBy { it.name },
                        )
                    )
                    siteDishes = null
                }
            }
            eventType = parser.next()
        }

        return Configuration(
            sites = sites.sortedBy { it.name },
            spacecraft = spacecraft,
        )
    }

    /** Null-safe attribute lookup; the feeds omit attributes rather than emitting empty values. */
    private fun XmlPullParser.attr(name: String): String = getAttributeValue(null, name) ?: ""
}
