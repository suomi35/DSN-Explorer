package com.holdenisacat.dsnexplorer.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.holdenisacat.dsnexplorer.util.DsnExplorerAppPreferences.Companion.LAST_SITE_INDEX
import com.holdenisacat.dsnexplorer.util.DsnExplorerAppPreferences.Companion.SHOW_ONLY_MEANINGFUL_DISHES
import com.holdenisacat.dsnexplorer.util.DsnExplorerAppPreferences.Companion.EMPTY_STRING
import com.holdenisacat.dsnexplorer.util.DsnExplorerAppPreferences.Companion.LAST_MODE
import com.holdenisacat.dsnexplorer.util.DsnExplorerAppPreferences.Companion.LAST_MODE_INDEX
import com.holdenisacat.dsnexplorer.util.DsnExplorerAppPreferences.Companion.SHOW_ALL_ACTIVE_SPACECRAFT


class DsnExplorerPreferences(context: Context) : DsnExplorerAppPreferences {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun set(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun set(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    override operator fun set(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    override operator fun get(key: String, defaultVal: String?): String? {
        val aux: String? = if (defaultVal == null) {
            prefs.getString(key, EMPTY_STRING)
        } else {
            prefs.getString(key, defaultVal)
        }
        return if (defaultVal == null && aux == EMPTY_STRING) {
            null
        } else {
            aux
        }
    }

    override operator fun get(key: String, defaultVal: Boolean): Boolean {
        return prefs.getBoolean(key, defaultVal)
    }

    override operator fun get(key: String, defaultVal: Int): Int {
        return prefs.getInt(key, defaultVal)
    }

    override var lastSiteIndex: Int
        get() = get(LAST_SITE_INDEX, 0)
        set(value) {
            set(LAST_SITE_INDEX, value)
        }

    override var showOnlyMeaningfulDishes: Boolean
        get() = get(SHOW_ONLY_MEANINGFUL_DISHES, true)
        set(value) {
            set(SHOW_ONLY_MEANINGFUL_DISHES, value)
        }

    override var showAllActiveSpacecraft: Boolean
        get() = get(SHOW_ALL_ACTIVE_SPACECRAFT, false)
        set(value) {
            set(SHOW_ALL_ACTIVE_SPACECRAFT, value)
        }

    override var lastMode: String
        get() = get(LAST_MODE, EMPTY_STRING).toString()
        set(value) {
            set(LAST_MODE, value)
        }

    override var lastModeIndex: Int
        get() = get(LAST_MODE_INDEX, 0)
        set(value) {
            set(LAST_MODE_INDEX, value)
        }
}