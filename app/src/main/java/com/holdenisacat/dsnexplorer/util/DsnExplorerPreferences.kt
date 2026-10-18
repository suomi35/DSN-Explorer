package com.holdenisacat.dsnexplorer.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Typed access to the small amount of UI state we persist between sessions.
 */
class DsnExplorerPreferences(context: Context) : PreferencesStore {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("dsn_explorer_prefs", Context.MODE_PRIVATE)

    override var lastSiteIndex: Int
        get() = prefs.getInt(KEY_LAST_SITE_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_SITE_INDEX, value).apply()

    override var lastModeIndex: Int
        get() = prefs.getInt(KEY_LAST_MODE_INDEX, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_MODE_INDEX, value).apply()

    private companion object {
        const val KEY_LAST_SITE_INDEX = "lastSiteIndex"
        const val KEY_LAST_MODE_INDEX = "lastModeIndex"
    }
}
