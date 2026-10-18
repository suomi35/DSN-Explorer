package com.holdenisacat.dsnexplorer.util

/** Typed access to persisted UI state — implemented by [DsnExplorerPreferences] on Android. */
interface PreferencesStore {

    var lastSiteIndex: Int

    var lastModeIndex: Int
}
