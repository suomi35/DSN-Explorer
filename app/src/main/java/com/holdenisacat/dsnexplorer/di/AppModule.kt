package com.holdenisacat.dsnexplorer.di

import com.holdenisacat.dsnexplorer.MainViewModel
import com.holdenisacat.dsnexplorer.parser.ConfigXmlParser
import com.holdenisacat.dsnexplorer.parser.DataXmlParser
import com.holdenisacat.dsnexplorer.repository.DataRepository
import com.holdenisacat.dsnexplorer.repository.HorizonsRangeClient
import com.holdenisacat.dsnexplorer.repository.RangeSupplementer
import com.holdenisacat.dsnexplorer.util.DsnExplorerPreferences
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.defaultRequest
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module. [createHttpClient] is internal so tests can supply their own engine.
 */
internal fun createHttpClient(): HttpClient = HttpClient(OkHttp) {
    defaultRequest {
        headers.append("Referer", "https://eyes.nasa.gov/dsn/dsn.html")
        headers.append(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36"
        )
    }
    install(HttpRequestRetry) {
        retryOnServerErrors(maxRetries = 5)
        exponentialDelay()
    }
}

val appModule = module {

    single { createHttpClient() }

    single { ConfigXmlParser() }
    single { DataXmlParser() }

    single { HorizonsRangeClient(get()) }
    single { RangeSupplementer(get()) }
    single { DataRepository(get(), get(), get(), get()) }

    single { DsnExplorerPreferences(get()) }

    viewModel { MainViewModel(get(), get()) }
}
