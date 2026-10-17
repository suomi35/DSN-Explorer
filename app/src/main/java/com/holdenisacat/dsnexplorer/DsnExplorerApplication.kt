package com.holdenisacat.dsnexplorer

import android.app.Application
import com.holdenisacat.dsnexplorer.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class DsnExplorerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@DsnExplorerApplication)
            modules(appModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
