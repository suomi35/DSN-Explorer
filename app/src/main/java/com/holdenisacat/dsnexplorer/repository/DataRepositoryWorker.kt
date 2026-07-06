package com.holdenisacat.dsnexplorer.repository

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.holdenisacat.dsnexplorer.model.DataStatus
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class DataRepositoryWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams), KoinComponent {

    private val dataRepository: DataRepository by inject()

    override suspend fun doWork(): Result {
        Timber.tag(TAG).i("Refreshing DSN data")

        WorkHandler.onDoWork()

        return when (dataRepository.refresh()) {
            DataStatus.LOADED -> Result.success()
            DataStatus.ERROR -> Result.retry()
            else -> Result.success()
        }
    }

    companion object {
        const val WORK_NAME = "MyWorker"
        private const val TAG = "DataRepositoryWorker"
    }
}
