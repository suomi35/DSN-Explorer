package com.holdenisacat.dsnexplorer.util

interface DsnExplorerAppPreferences {

    var lastSiteIndex: Int

    var showOnlyMeaningfulDishes: Boolean

    var showAllActiveSpacecraft: Boolean

    var lastMode: String

    var lastModeIndex: Int

    fun set(key: String, value: String)

    fun set(key: String, value: Boolean)

    fun set(key: String, value: Int)

    fun get(key: String, defaultVal: String?): String?

    fun get(key: String, defaultVal: Boolean): Boolean

    fun get(key: String, defaultVal: Int): Int

    companion object {
        const val EMPTY_STRING = ""
        const val LAST_SITE_INDEX = "lastSiteIndex"
        const val SHOW_ONLY_MEANINGFUL_DISHES = "showOnlyMeaningfulDishes"
        const val SHOW_ALL_ACTIVE_SPACECRAFT = "showAllActiveSpacecraft"
        const val LAST_MODE = "lastMode"
        const val LAST_MODE_INDEX = "lastModeIndex"
    }
}