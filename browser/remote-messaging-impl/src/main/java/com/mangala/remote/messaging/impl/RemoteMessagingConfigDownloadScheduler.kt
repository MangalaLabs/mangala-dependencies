/*
 * Copyright (c) DuckDuckGo, Inc.
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modified from original source: https://github.com/duckduckgo/Android
 */



package com.mangala.remote.messaging.impl

import android.content.Context
//import androidx.hilt.work.HiltWorker
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import com.mangala.app.global.DispatcherProvider
import kotlinx.coroutines.withContext
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import timber.log.Timber
import java.util.concurrent.TimeUnit


//@HiltWorker
//class RemoteMessagingConfigDownloadWorker @AssistedInject constructor(
//    @Assisted context: Context,
//    @Assisted workerParameters: WorkerParameters,
//    private val downloader: RemoteMessagingConfigDownloader,
//    private val dispatcherProvider: DispatcherProvider
//) : CoroutineWorker(context, workerParameters) {
//
//    override suspend fun doWork(): Result {
//        return withContext(dispatcherProvider.io()) {
//            val result = downloader.download()
//            return@withContext if (result) {
//                Result.success()
//            } else {
//                Result.retry()
//            }
//        }
//    }
//}

class RemoteMessagingConfigDownloadWorker(
    context: Context,
    params: WorkerParameters,
    private val downloader: RemoteMessagingConfigDownloader,
    private val dispatcherProvider: DispatcherProvider,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(dispatcherProvider.io()) {
            val result = downloader.download()
            return@withContext if (result) {
                Result.success()
            } else {
                Result.retry()
            }
        }
    }
}

class RemoteMessagingConfigDownloadScheduler (
    private val workManager: WorkManager
) : LifecycleEventObserver {

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Event
    ) {
        if (event == ON_CREATE) {
            scheduleDownload()
        }
    }

    private fun scheduleDownload() {
        Timber.v("RMF: Scheduling remote config worker")
        val workerRequest = PeriodicWorkRequestBuilder<RemoteMessagingConfigDownloadWorker>(4, TimeUnit.HOURS)
            .addTag(REMOTE_MESSAGING_DOWNLOADER_WORKER_TAG)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(REMOTE_MESSAGING_DOWNLOADER_WORKER_TAG, ExistingPeriodicWorkPolicy.REPLACE, workerRequest)
    }

    companion object {
        private const val REMOTE_MESSAGING_DOWNLOADER_WORKER_TAG = "REMOTE_MESSAGING_DOWNLOADER_WORKER_TAG"
    }
}
