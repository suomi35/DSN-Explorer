package com.holdenisacat.dsnexplorer.parser

import android.util.Xml
import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.Site
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.util.Constants
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class ConfigXmlPullParserHandler {

    private var sites: ArrayList<Site> = ArrayList()
    private var dishes: ArrayList<Dish> = ArrayList()
    private var spacecraftList: ArrayList<Spacecraft> = ArrayList()

    fun parse(inputStream: InputStream): Configuration {
        try {
            val parser: XmlPullParser = Xml.newPullParser()
//            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true) // TODO: Not sure WTF this does
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var site: Site? = null
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagname = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> if (tagname.equals(Constants.SITE, ignoreCase = true)) {
                        // create a new instance of site
                        site = Site(
                            parser.getAttributeValue(null, Constants.NAME),
                            parser.getAttributeValue(null, Constants.FRIENDLY_NAME),
                            parser.getAttributeValue(null, Constants.LATITUDE),
                            parser.getAttributeValue(null, Constants.LONGITUDE)
                        )
                        dishes = ArrayList()
                    } else if (tagname.equals(Constants.DISH, ignoreCase = true)) {
                        // create a new instance of dish and add to list
                        dishes!!.add(
                            Dish(
                                parser.getAttributeValue(null, Constants.NAME),
                                parser.getAttributeValue(null, Constants.FRIENDLY_NAME),
                                parser.getAttributeValue(null, Constants.TYPE)
                            )
                        )
                    } else if (tagname.equals(Constants.SPACECRAFT, ignoreCase = true)) {
                        if (parser.getAttributeValue(null, Constants.THUMBNAIL) != null) {
                            spacecraftList!!.add(
                                Spacecraft(
                                    parser.getAttributeValue(null, Constants.NAME),
                                    parser.getAttributeValue(null, Constants.EXPLORER_NAME),
                                    parser.getAttributeValue(null, Constants.FRIENDLY_NAME),
                                    parser.getAttributeValue(
                                        null,
                                        Constants.THUMBNAIL
                                    ) == Constants.BOOLEAN_TRUE
                                )
                            )
                        } else {
                            spacecraftList!!.add(
                                Spacecraft(
                                    parser.getAttributeValue(null, Constants.NAME),
                                    parser.getAttributeValue(null, Constants.EXPLORER_NAME),
                                    parser.getAttributeValue(null, Constants.FRIENDLY_NAME)
                                )
                            )
                        }
                    }

                    XmlPullParser.END_TAG -> if (tagname.equals(Constants.SITE, ignoreCase = true)) {
                        // add any configDishes to site
                        dishes?.let { site?.dishes = it }

                        // add site sites to list
                        site?.let { sites.add(it) }
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

        return Configuration(sites, spacecraftList)
    }
}