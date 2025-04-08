package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.util.Constants
import com.holdenisacat.dsnexplorer.util.Utils

data class Dsn(

    var sites: ArrayList<Site>
) {
    constructor() : this(ArrayList<Site>())

    fun getAllActiveTargets(sortAlpha: Boolean, ignoreUFOs: Boolean = true): ArrayList<Target> {
        val targets = java.util.ArrayList<Target>()
        for (site in sites) {
            for (dish in site.dishes) {
                for (target in dish.targets) {
                    var addTargetToList = true
                    for (existingTarget in targets) {
                        if (existingTarget.name == target.name
                            || (ignoreUFOs && target.name == Constants.UFO)) {
                            addTargetToList = false
                            break
                        }
                    }
                    if (addTargetToList) {
                        target.flag = Utils.getSiteFlag(site.name)
                        targets.add(target)
                    }
                }
            }
        }
        if (sortAlpha) {
            targets.sortedWith(compareByDescending { it.friendlyName })
        }
        return targets
    }

    fun getAllDishesForTarget(target: Target): LinkedHashSet<Dish> {
        val dishList = LinkedHashSet<Dish>()
        for (site in sites) {
            for (dish in site.dishes) {
                if (dish.signals.isNotEmpty()) {
                    for (signal in dish.signals) {
                        if (target.id == signal.spacecraftId) {
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