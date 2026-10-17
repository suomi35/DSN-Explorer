package com.holdenisacat.dsnexplorer.repository

import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.parser.ConfigXmlParser
import com.holdenisacat.dsnexplorer.parser.DataXmlParser
import com.holdenisacat.dsnexplorer.util.DsnApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException

/**
 * Single source of truth for live DSN data.
 *
 * [refresh] loads the static configuration once, then polls the live feed on
 * every call. The supplemented model (with JPL Horizons ranges where the DSN
 * feed omits them) is emitted exactly once per cycle.
 */
class DataRepository(
    private val httpClient: HttpClient,
    private val configParser: ConfigXmlParser,
    private val dataParser: DataXmlParser,
    private val rangeSupplementer: RangeSupplementer,
) {

    private val _configuration = MutableStateFlow<Configuration?>(null)
    val configuration: StateFlow<Configuration?> = _configuration.asStateFlow()

    private val _dsn = MutableStateFlow<Dsn?>(null)
    val dsn: StateFlow<Dsn?> = _dsn.asStateFlow()

    /**
     * Fetches configuration (on first call) plus live data and publishes the result.
     *
     * @throws IOException on network or payload failures so callers can distinguish
     *   transport errors from an empty-but-valid feed.
     */
    suspend fun refresh(): DataStatus = withContext(Dispatchers.IO) {
        try {
            if (_configuration.value == null) {
                fetchConfiguration()
            }
            fetchDsnData(_configuration.value ?: return@withContext DataStatus.ERROR)
            DataStatus.LOADED
        } catch (e: IOException) {
            Timber.e(e, "Failed to refresh DSN data")
            DataStatus.ERROR
        }
    }

    private suspend fun fetchConfiguration() {
        val bytes = httpClient.get(DsnApi.CONFIGURATION_URL).bodyAsBytes()
        _configuration.value = configParser.parse(bytes.inputStream())
    }

    private suspend fun fetchDsnData(configuration: Configuration) {
        val cacheBustedUrl = DsnApi.DATA_URL + System.currentTimeMillis() / 1000
        val bytes = httpClient.get(cacheBustedUrl).bodyAsBytes()
        val parsed = dataParser.parse(configuration, bytes.inputStream())
        // Ranges are supplemented before the single emission so collectors never
        // observe a half-updated model.
        _dsn.value = rangeSupplementer.supplement(parsed)
    }
}
