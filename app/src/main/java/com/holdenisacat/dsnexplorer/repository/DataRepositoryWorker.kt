package com.holdenisacat.dsnexplorer.repository

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class DataRepositoryWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams), KoinComponent {

    private val dataRepository: DataRepository by inject<DataRepository>()

    override fun doWork(): Result {
        Timber.tag(TAG).i(">>> doWork")

        //you can notify back to user
        WorkHandler.onDoWork()

        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.fetchConfig()
        }

        return Result.success()
    }

    companion object {
        // unique name for the work
        const val WORK_NAME = "MyWorker"
        private const val TAG = "MyWork"

    }
}