package com.holdenisacat.dsnexplorer.parser

import android.util.Xml
import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.model.Signal
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.model.Target
import com.holdenisacat.dsnexplorer.util.Constants
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

class DataXmlPullParserHandler {

    private val TAG: String = "DataXMLHandler"
    private var sites: ArrayList<Site> = ArrayList()
    private var dishes: ArrayList<Dish> = ArrayList()
    private var signals: ArrayList<Signal> = ArrayList()
    private var targets: ArrayList<Target> = ArrayList()
    private lateinit var dish: Dish
    private lateinit var site: Site
    private var isOrphanDish = false

    fun parse(configuration: Configuration, inputStream: InputStream): Dsn {
        try {
            val parser: XmlPullParser = Xml.newPullParser()
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true) // TODO: Not sure WTF this does
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals(Constants.STATION, ignoreCase = true)) {
                        // set site data in existing config-site object
                        val siteIndex: Int = configuration.getSiteIndexByName(
                            parser.getAttributeValue(
                                null,
                                Constants.NAME
                            )
                        )
                        site = configuration.getSiteByIndex(siteIndex)
                        dishes = site.dishes

                        site.timeUTC = parser.getAttributeValue(null, Constants.TIME_UTC).toLong()
                        site.timeZoneOffset = parser.getAttributeValue(null, Constants.TIME_ZONE_OFFSET).replace(".0", "").toLong()
                    } else if (tagname.equals(Constants.DISH, ignoreCase = true)) {
                        // set dish data in existing config-dish object
                        // get existing dish ID
                        val siteIndex: Int = configuration.getSiteIndexByName(site.name)
                        val dishIndex: Int = configuration.getDishIndexByName(
                            siteIndex,
                            parser.getAttributeValue(null, Constants.NAME)
                        )

                        // test for orphan dish
                        if (dishIndex == 999) {
                            Timber.tag(TAG).d("ORPHAN DISH: ${parser.getAttributeValue(null, Constants.NAME)}")
                            isOrphanDish = true
                        } else {
                            Timber.tag(TAG).d("DISH: ${parser.getAttributeValue(null, Constants.NAME)}")
                            dish = configuration.getSiteByIndex(siteIndex).getDishByIndex(dishIndex)

                            dish.azimuthAngle = parser.getAttributeValue(null, Constants.AZIMUTH_ANGLE).toFloat()
                            dish.elevationAngle = parser.getAttributeValue(null, Constants.ELEVATION_ANGLE).toFloat()
                            dish.windSpeed = parser.getAttributeValue(null, Constants.WINDSPEED)
                            dish.isMSPA = parser.getAttributeValue(null, Constants.IS_MSPA) == Constants.BOOLEAN_TRUE
                            dish.isArray = parser.getAttributeValue(null, Constants.IS_ARRAY) == Constants.BOOLEAN_TRUE
                            dish.isDDOR = parser.getAttributeValue(null, Constants.IS_DDOR) == Constants.BOOLEAN_TRUE
                            dish.activity = parser.getAttributeValue(null, Constants.ACTIVITY)
                            dish.dishLocation = site.name

//                            // init arrays for dish
                            signals = ArrayList()
                            targets = ArrayList()
                        }
                    } else if (tagname.equals(Constants.UP_SIGNAL, ignoreCase = true)) {
                        signals.add(
                            // <upSignal active="true" signalType="data" dataRate="0" frequency="0" band="X" power="18" spacecraft="JNO" spacecraftID="-61"/>
                            Signal(
                                parser.getAttributeValue(null, Constants.ACTIVE).toBoolean(),
                                parser.getAttributeValue(null, Constants.SIGNAL_TYPE),
                                parser.getAttributeValue(null, Constants.DATA_RATE),
                                parser.getAttributeValue(null, Constants.FREQUENCY),
                                parser.getAttributeValue(null, Constants.BAND),
                                parser.getAttributeValue(null, Constants.POWER),
                                parser.getAttributeValue(null, Constants.SPACECRAFT),
                                if (parser.getAttributeValue(null, Constants.SPACECRAFT_ID)
                                    .startsWith("-")) {
                                    parser.getAttributeValue(null, Constants.SPACECRAFT_ID).drop(1)
                                } else {
                                    parser.getAttributeValue(null, Constants.SPACECRAFT_ID)
                                },
                                SignalDirection.UP
                            )
                        )
                    } else if (tagname.equals(Constants.DOWN_SIGNAL, ignoreCase = true)) {
                        signals.add(
                            // <downSignal active="true" signalType="data" dataRate="25970" frequency="0" band="X" power="-130" spacecraft="JNO" spacecraftID="-61"/>
                            Signal(
                                parser.getAttributeValue(null, Constants.ACTIVE).toBoolean(),
                                parser.getAttributeValue(null, Constants.SIGNAL_TYPE),
                                parser.getAttributeValue(null, Constants.DATA_RATE),
                                parser.getAttributeValue(null, Constants.FREQUENCY),
                                parser.getAttributeValue(null, Constants.BAND),
                                parser.getAttributeValue(null, Constants.POWER),
                                parser.getAttributeValue(null, Constants.SPACECRAFT),
                                if (parser.getAttributeValue(null, Constants.SPACECRAFT_ID)
                                    .startsWith("-")) {
                                    parser.getAttributeValue(null, Constants.SPACECRAFT_ID).drop(1)
                                } else {
                                    parser.getAttributeValue(null, Constants.SPACECRAFT_ID)
                                },
                                SignalDirection.DOWN
                            )
                        )
                    } else if (tagname.equals(Constants.TARGET, ignoreCase = true)) {
                        val spacecraft: Spacecraft = configuration.getSpacecraftByName(
                            parser.getAttributeValue(
                                null,
                                Constants.NAME
                            )
                        )
                        if (spacecraft.name != Constants.UFO
                            && spacecraft.name != Constants.DSN) {
                            Timber.tag(TAG).d("enhancing spacecraft: ${spacecraft.name}")
                            targets.add(
                                Target(
                                    parser.getAttributeValue(null, Constants.NAME),
                                    parser.getAttributeValue(null, Constants.ID),
                                    parser.getAttributeValue(null, Constants.UPLEG_RANGE)
                                        .toFloat(),
                                    parser.getAttributeValue(null, Constants.DOWNLEG_RANGE)
                                        .toFloat(),
                                    parser.getAttributeValue(null, Constants.ROUNDTRIP_LIGHT_TIME)
                                        .toFloat(),
                                    spacecraft.friendlyName,
                                    spacecraft.thumbnail
                                )
                            )
                        } else {
                            Timber.tag(TAG).d("no config entry for: ${parser.getAttributeValue(null, Constants.NAME)}")
//                            targets.add(
//                                Target(
//                                    parser.getAttributeValue(null, Constants.NAME),
//                                    parser.getAttributeValue(null, Constants.ID),
//                                    parser.getAttributeValue(null, Constants.UPLEG_RANGE).toFloat(),
//                                    parser.getAttributeValue(null, Constants.DOWNLEG_RANGE)
//                                        .toFloat(),
//                                    parser.getAttributeValue(null, Constants.ROUNDTRIP_LIGHT_TIME)
//                                        .toFloat(),
//                                    Constants.UFO,
//                                    false
//                                )
//                            )
                        }
                    }

                    XmlPullParser.END_TAG -> if (tagname.equals(Constants.STATION, ignoreCase = true)
                    ) {
                        site.dishes = dishes
                        sites.add(site)
                        println(">>> Site creation should be complete for site: ${site.name}")
                    } else if (tagname.equals(Constants.DISH, ignoreCase = true)) {
                        if (!isOrphanDish) {
                            dish.signals = signals
                            dish.targets = targets
                        } else {
                            isOrphanDish = false
                        }
                    }

                    else -> {}
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        sites.sortedWith(compareByDescending { it.name })
        return Dsn(sites)
    }
}