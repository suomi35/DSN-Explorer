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
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.InputStream

class DataRepository {

    private var configuration: Configuration by mutableStateOf(Configuration())
    private val _configurationFlow = MutableStateFlow(configuration)

    private var dsn: Dsn by mutableStateOf(Dsn())
    private val _dsnFlow = MutableStateFlow(dsn)

    val configurationFlow: StateFlow<Configuration> get() = _configurationFlow
    val dsnFlow: StateFlow<Dsn> get() = _dsnFlow

    suspend fun fetchConfig(): DataStatus {
        val dataStatus: DataStatus

        withContext(Dispatchers.IO) {
            val configurationResponse = DsnExplorerApplication.ktorHttpClient.get(CONFIGURATION_URL)
//            println(">>> configurationResponse: ${configurationResponse.bodyAsText()}")

            val inputStream: InputStream = configurationResponse.bodyAsBytes().inputStream()
            val configParser = ConfigXmlPullParserHandler()
            _configurationFlow.value = configParser.parse(inputStream)

            // dataStatus = DataStatus.ERROR
            dataStatus = DataStatus.LOADED
        }

        return dataStatus
    }

    suspend fun fetchDsnData(configuration: Configuration): DataStatus {
        val dataStatus: DataStatus

        withContext(Dispatchers.IO) {
            val dsnResponse = DsnExplorerApplication.ktorHttpClient.get(DSN_DATA_URL
                    + System.currentTimeMillis() / 1000)
//            println(">>> dsnResponse: ${dsnResponse.bodyAsText()}")

            val dataParser = DataXmlPullParserHandler()
            _dsnFlow.value = dataParser.parse(configuration, dsnResponse.bodyAsBytes().inputStream())

            // dataStatus = DataStatus.ERROR
            dataStatus = DataStatus.LOADED
        }

        return dataStatus
    }
}