package com.holdenisacat.dsnexplorer.repository

import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.model.Dsn
import kotlinx.coroutines.flow.StateFlow

/** Abstraction over live DSN data access so the ViewModel can be tested in isolation. */
interface DsnRepository {

    /** Latest snapshot of DSN state, or null before the first successful refresh. */
    val dsn: StateFlow<Dsn?>

    /** Fetches configuration and live data, publishing the result. Returns LOADED or ERROR. */
    suspend fun refresh(): DataStatus
}
