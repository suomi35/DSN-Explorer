package com.holdenisacat.dsnexplorer

import android.app.Application
import com.holdenisacat.dsnexplorer.di.appModule
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant

class DsnExplorerApplication: Application() {

    init {
        appInstance = this
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DsnExplorerApplication)
            modules(appModule)
        }

        plant(DebugTree())
    }

    companion object {
        val ktorHttpClient = HttpClient {
            install(ContentNegotiation) {
                json()
            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 5)
                exponentialDelay()
            }
            headers {
                append("Host", "eyes.nasa.gov")
                append("Referer", "https://eyes.nasa.gov/dsn/dsn.html")
                append("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
            }
        }

        lateinit var appInstance: DsnExplorerApplication
    }
}