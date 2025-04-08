package com.holdenisacat.dsnexplorer.model

import com.holdenisacat.dsnexplorer.util.Constants
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

data class Site(

    var name: String,
    var friendlyName: String,
    var latitude: String,
    var longitude: String,
    var dishes: ArrayList<Dish> = ArrayList(),
    var timeUTC: Long = 0,
    var timeZoneOffset: Long = 0
) {
    constructor() : this("", "", "", "")

    fun getDishByIndex(index: Int): Dish {
        return dishes[index]
    }

    fun getDishes(meaningfulOnly: Boolean): ArrayList<Dish> {
        val matchingDishes = java.util.ArrayList<Dish>()
        for (dish in dishes) {
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
            dishes // just pass what we already have
        }
    }

    fun getNiceTime(): String {
        if (timeUTC != 0L) {
            val siteTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            siteTime.timeInMillis = timeUTC + timeZoneOffset
            val sdf: DateFormat = SimpleDateFormat("MMM d\nh:mm a")
            sdf.calendar = siteTime
            return sdf.format(siteTime.time)
        } else {
            return ""
        }
    }
}