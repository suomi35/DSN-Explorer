package com.holdenisacat.dsnexplorer.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.holdenisacat.dsnexplorer.DsnExplorerApplication
import com.holdenisacat.dsnexplorer.model.Configuration
import com.holdenisacat.dsnexplorer.model.DataStatus
import com.holdenisacat.dsnexplorer.model.Dsn
import com.holdenisacat.dsnexplorer.parser.ConfigXmlPullParserHandler
import com.holdenisacat.dsnexplorer.parser.DataXmlPullParserHandler
import com.holdenisacat.dsnexplorer.util.Constants.CONFIGURATION_URL
import com.holdenisacat.dsnexplorer.util.Constants.DSN_DATA_URL
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream

class DataRepository(
    private val rangeSupplementer: RangeSupplementer,
) {

    private var configuration: Configuration by mutableStateOf(Configuration())
    private val _configurationFlow = MutableStateFlow(configuration)

    private var dsn: Dsn by mutableStateOf(Dsn())
    private val _dsnFlow = MutableStateFlow(dsn)

    val configurationFlow: StateFlow<Configuration> get() = _configurationFlow
    val dsnFlow: StateFlow<Dsn> get() = _dsnFlow

    private var configLoaded = false

    suspend fun refresh(): DataStatus {
        return try {
            if (!configLoaded || _configurationFlow.value.sites.isEmpty()) {
                fetchConfig()
                configLoaded = true
            }
            fetchDsnData(_configurationFlow.value)
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh DSN data")
            DataStatus.ERROR
        }
    }

    suspend fun fetchConfig(): DataStatus {
        return withContext(Dispatchers.IO) {
            try {
                val configurationResponse = DsnExplorerApplication.ktorHttpClient.get(CONFIGURATION_URL)
                val inputStream: InputStream = configurationResponse.bodyAsBytes().inputStream()
                val configParser = ConfigXmlPullParserHandler()
                _configurationFlow.value = configParser.parse(inputStream)
                DataStatus.LOADED
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch DSN configuration")
                DataStatus.ERROR
            }
        }
    }

    suspend fun fetchDsnData(configuration: Configuration): DataStatus {
        return withContext(Dispatchers.IO) {
            try {
                val dsnResponse = DsnExplorerApplication.ktorHttpClient.get(
                    DSN_DATA_URL + System.currentTimeMillis() / 1000
                )
                val dataParser = DataXmlPullParserHandler()
                val parsedDsn = dataParser.parse(configuration, dsnResponse.bodyAsBytes().inputStream())
                _dsnFlow.value = parsedDsn

                if (rangeSupplementer.supplement(parsedDsn)) {
                    _dsnFlow.value = parsedDsn
                }
                DataStatus.LOADED
            } catch (e: Exception) {
                Timber.e(e, "Failed to fetch live DSN data")
                DataStatus.ERROR
            }
        }
    }
}
