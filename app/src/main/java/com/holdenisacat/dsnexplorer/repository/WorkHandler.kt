package com.holdenisacat.dsnexplorer.repository

import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.holdenisacat.dsnexplorer.DsnExplorerApplication
import timber.log.Timber
import java.util.concurrent.TimeUnit


object WorkHandler {
    private const val TAG = "WorkHandler"
    private val workManager = WorkManager.getInstance(DsnExplorerApplication.appInstance)

    /**
     * create work request
     *
     * @param delay
     */
    fun createWork(delay: Long) {
        val constraints = Constraints.Builder().build()

        val request = OneTimeWorkRequestBuilder<DataRepositoryWorker>()

        val work = request.setConstraints(constraints)
            .addTag(TAG)
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .build()

        workManager.enqueue(work)

        Timber.tag(TAG).i(">>> work created")
    }

    /**
     * do UI related work because this method is supporting foreground work
     * If you have any other requirement then please update the conditions
     */
    fun onDoWork() {
        Timber.tag(TAG).i(">>> onDoWork()")
        createWork(5) // Run every 5 sec
    }

    /**
     * cancel enqueued work by the given tag
     */
    fun cancelWork() {
        workManager.cancelAllWorkByTag(TAG)
        Timber.i(">>> work canceled")
    }
}