package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.util.Constants
import java.util.Locale

data class Configuration(

    var sites: ArrayList<Site>,
    var spacecraftList: ArrayList<Spacecraft>,
    var sitesHash: Int = 0
) {
    constructor() : this(ArrayList<Site>(), ArrayList<Spacecraft>())

    fun getSiteByIndex(index: Int): Site {
        return sites[index]
    }

    fun getSiteIndexByName(name: String): Int {
        for (site in sites) {
            if (site.name == name) {
                return sites.indexOf(site)
            }
        }
        return 999 // high number that should never be reached
    }

    fun getDishIndexByName(siteIndex: Int, name: String): Int {
        for (dish in sites[siteIndex].dishes) {
            if (dish.name == name) {
                return sites[siteIndex].dishes.indexOf(dish)
            }
        }
        return 999 // high number that should never be reached
    }

    fun getSpacecraftByName(name: String): Spacecraft {
        for (spacecraft in spacecraftList) {
            if (spacecraft.name == name.lowercase(Locale.getDefault())) {
                return spacecraft
            }
        }
        return Spacecraft(Constants.UFO, Constants.UFO, Constants.UFO)
    }
}