package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.util.Constants
import com.holdenisacat.dsnexplorer.util.Utils

data class Dsn(

    var sites: ArrayList<Site>
) {
    constructor() : this(ArrayList<Site>())

    fun getAllActiveSpacecraft(sortAlpha: Boolean, ignoreUFOs: Boolean = true): ArrayList<Spacecraft> {
        val spacecraftList = java.util.ArrayList<Spacecraft>()
        for (site in sites) {
            for (dish in site.dishes) {
                for (spacecraft in dish.spacecraftList) {
                    var addSpacecraftToList = true
                    for (existingSpacecraft in spacecraftList) {
                        if (existingSpacecraft.name == spacecraft.name
                            || (ignoreUFOs && spacecraft.name == Constants.UFO)) {
                            addSpacecraftToList = false
                            break
                        }
                    }
                    if (addSpacecraftToList) {
                        spacecraft.flag = Utils.getSiteFlag(site.name)
                        spacecraftList.add(spacecraft)
                    }
                }
            }
        }
        if (sortAlpha) {
            spacecraftList.sortedWith(compareByDescending { it.friendlyName })
        }
        return spacecraftList
    }

    fun getAllDishesForSpacecraft(spacecraft: Spacecraft): LinkedHashSet<Dish> {
        val dishList = LinkedHashSet<Dish>()
        for (site in sites) {
            for (dish in site.dishes) {
                if (dish.signals.isNotEmpty()) {
                    for (signal in dish.signals) {
                        if (spacecraft.id == signal.spacecraftId) {
                            dishList.add(dish)
                        }
                    }
                }
            }
        }
        return dishList
    }

    fun getAllDishesBySite(siteIndex: Int): ArrayList<Dish> {
        return sites[siteIndex].dishes
    }

    fun getDishesBySite(siteIndex: Int, meaningfulOnly: Boolean): ArrayList<Dish>? {
        if (sites[siteIndex].dishes.size > 0) {
            val matchingDishes = java.util.ArrayList<Dish>()
            for (dish in sites[siteIndex].dishes) {
                // update each dish's active status
                var isActive = false
                // return only sensible dishes ;)
                for (signal in dish.signals) {
                    if (signal.signalType != Constants.NONE) {
                        isActive = true
                    }
                }
                if (isActive) {
                    dish.isActive = true
                    matchingDishes.add(dish)
                }
            }

            return if (meaningfulOnly) {
                matchingDishes
            } else {
                sites[siteIndex].dishes // just pass what we already have
            }
        } else {
            return null
        }
    }

    fun getSiteFromDish(dish: Dish): Site? {
        for (site in sites) {
            for (siteDish in site.dishes) {
                if (siteDish.name == dish.name) {
                    return site
                }
            }
        }
        return null
    }

    fun getSiteByName(name: String): Site? {
        for (site in sites) {
            if (site.name == name) {
                return site
            }
        }
        return null
    }
}