package com.holdenisacat.dsnexplorer.di

import com.holdenisacat.dsnexplorer.MainViewModel
import com.holdenisacat.dsnexplorer.parser.ConfigXmlPullParserHandler
import com.holdenisacat.dsnexplorer.repository.DataRepository
import com.holdenisacat.dsnexplorer.util.DsnExplorerPreferences
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { MainViewModel() }

    single { DsnExplorerPreferences(get()) }

    single { DataRepository() }

    single { ConfigXmlPullParserHandler() }
}
