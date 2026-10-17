package com.holdenisacat.dsnexplorer.parser

import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Signal
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.model.SignalType
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.util.DsnXml
import com.holdenisacat.dsnexplorer.util.Formatters
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.util.Locale

/**
 * Parses the live DSN `dsn.xml` feed, enriching the [Configuration] templates
 * with current antenna state, signals and targets.
 *
 * The handler is stateless: all parse state lives on the stack, so a single
 * instance can safely be shared and reused across refresh cycles.
 *
 * @throws XmlPullParserException, IOException on malformed input; callers decide how to surface it.
 */
class DataXmlParser {

    /** Per-dish parse scratch state, kept on the stack to make [DataXmlParser] stateless. */
    private data class DishState(
        val configDish: Dish,
        val siteName: String,
        val azimuth: Float,
        val elevation: Float,
        val windSpeed: String,
        val isMSPA: Boolean,
        val isArray: Boolean,
        val isDDOR: Boolean,
        val activity: String,
        val signals: MutableList<Signal> = mutableListOf(),
        val spacecraft: MutableList<Spacecraft> = mutableListOf(),
    ) {
        fun build(): Dish = configDish.copy(
            azimuthAngle = azimuth,
            elevationAngle = elevation,
            windSpeed = windSpeed,
            isMSPA = isMSPA,
            isArray = isArray,
            isDDOR = isDDOR,
            activity = activity,
            siteName = siteName,
            signals = signals,
            spacecraftList = spacecraft,
        )
    }

    fun parse(configuration: Configuration, inputStream: InputStream): Dsn {
        val parser: XmlPullParser = XmlPullParserFactory.newInstance().newPullParser().apply {
            setInput(inputStream, null)
        }

        val sites = mutableListOf<Site>()
        var configSite: Site? = null
        var siteDishes: MutableList<Dish>? = null
        var currentDish: DishState? = null
        var siteTimeUtc = 0L
        var siteTzOffset = 0L
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    DsnXml.STATION -> {
                        val site = configuration.siteByName(parser.attr(DsnXml.NAME))
                        if (site == null) {
                            Timber.d("Skipping unknown station: ${parser.attr(DsnXml.NAME)}")
                        }
                        configSite = site
                        siteDishes = site?.dishes?.toMutableList() ?: mutableListOf()
                        siteTimeUtc = parser.attr(DsnXml.TIME_UTC).toLongOrNull() ?: 0L
                        siteTzOffset = parser.attr(DsnXml.TIME_ZONE_OFFSET).toFloatOrNull()?.toLong() ?: 0L
                    }

                    DsnXml.DISH -> {
                        val site = configSite
                        val configDish = site?.dishes?.firstOrNull {
                            it.name.equals(parser.attr(DsnXml.NAME), ignoreCase = true)
                        }
                        currentDish = if (configDish == null) {
                            Timber.d("Skipping orphan dish: ${parser.attr(DsnXml.NAME)}")
                            null
                        } else {
                            DishState(
                                configDish = configDish,
                                siteName = site.name,
                                azimuth = parser.attr(DsnXml.AZIMUTH_ANGLE).toFloatOrNull() ?: 0f,
                                elevation = parser.attr(DsnXml.ELEVATION_ANGLE).toFloatOrNull() ?: 0f,
                                windSpeed = parser.attr(DsnXml.WIND_SPEED),
                                isMSPA = parser.attr(DsnXml.IS_MSPA) == DsnXml.BOOLEAN_TRUE,
                                isArray = parser.attr(DsnXml.IS_ARRAY) == DsnXml.BOOLEAN_TRUE,
                                isDDOR = parser.attr(DsnXml.IS_DDOR) == DsnXml.BOOLEAN_TRUE,
                                activity = parser.attr(DsnXml.ACTIVITY),
                            )
                        }
                    }

                    DsnXml.UP_SIGNAL, DsnXml.DOWN_SIGNAL -> currentDish?.signals?.add(
                        parser.readSignal(direction = if (parser.name == DsnXml.UP_SIGNAL) {
                            SignalDirection.UP
                        } else {
                            SignalDirection.DOWN
                        })
                    )

                    DsnXml.TARGET -> currentDish?.spacecraft?.addNotNull(parser.readTarget(configuration))
                }

                XmlPullParser.END_TAG -> when (parser.name) {
                    DsnXml.DISH -> {
                        val finished = currentDish
                        if (finished != null) {
                            val dishes = siteDishes
                            val index = dishes?.indexOfFirst {
                                it.name.equals(finished.configDish.name, ignoreCase = true)
                            } ?: -1
                            if (index >= 0) dishes!![index] = finished.build()
                        }
                        currentDish = null
                    }

                    DsnXml.STATION -> {
                        configSite?.let { site ->
                            sites.add(
                                site.copy(
                                    dishes = siteDishes.orEmpty(),
                                    timeUTC = siteTimeUtc,
                                    timeZoneOffset = siteTzOffset,
                                )
                            )
                        }
                        configSite = null
                        siteDishes = null
                    }
                }
            }
            eventType = parser.next()
        }

        return Dsn(sites = sites.sortedBy { it.name })
    }
    private fun XmlPullParser.readSignal(direction: SignalDirection): Signal = Signal(
        active = attr(DsnXml.ACTIVE) == DsnXml.BOOLEAN_TRUE,
        type = SignalType.fromRaw(attr(DsnXml.SIGNAL_TYPE)),
        dataRate = attr(DsnXml.DATA_RATE),
        frequency = attr(DsnXml.FREQUENCY),
        band = attr(DsnXml.BAND),
        power = attr(DsnXml.POWER),
        spacecraft = attr(DsnXml.SPACECRAFT).lowercase(Locale.ROOT),
        spacecraftId = attr(DsnXml.SPACECRAFT_ID).removePrefix("-"),
        direction = direction,
    )

    /** Builds an enriched [Spacecraft] from the config template, or null for unknown targets. */
    private fun XmlPullParser.readTarget(configuration: Configuration): Spacecraft? {
        val template = configuration.spacecraftByName(attr(DsnXml.NAME))
        if (template == null || template.isUnknownTarget) {
            Timber.d("Skipping target with no config entry: ${attr(DsnXml.NAME)}")
            return null
        }
        val uplegRange = attr(DsnXml.UPLEG_RANGE).toFloatOrNull() ?: Spacecraft.UNKNOWN_RANGE
        val downlegRange = attr(DsnXml.DOWNLEG_RANGE).toFloatOrNull() ?: Spacecraft.UNKNOWN_RANGE
        val rtlt = attr(DsnXml.ROUNDTRIP_LIGHT_TIME).toFloatOrNull()
            ?.takeIf { it > 0f }
            ?: Formatters.roundTripLightTime(uplegRange)
        return template.copy(
            id = attr(DsnXml.ID).removePrefix("-"),
            uplegRange = uplegRange,
            downlegRange = downlegRange,
            roundtripLightTime = rtlt,
            rangeIsEstimated = uplegRange <= 0f,
        )
    }

    private val Spacecraft.isUnknownTarget: Boolean
        get() = name.equals("UFO", ignoreCase = true) || name.equals("dsn", ignoreCase = true)

    private fun MutableList<Spacecraft>.addNotNull(spacecraft: Spacecraft?) {
        if (spacecraft != null) add(spacecraft)
    }

    /** Null-safe attribute lookup; the feeds omit attributes rather than emitting empty values. */
    private fun XmlPullParser.attr(name: String): String = getAttributeValue(null, name) ?: ""
}
