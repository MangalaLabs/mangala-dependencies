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


package com.mangala.app.statistics.api

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import timber.log.Timber
import java.util.concurrent.TimeUnit


class OfflinePixelScheduler(
    private val workManager: WorkManager
) : LifecycleEventObserver {

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        if (event == Lifecycle.Event.ON_CREATE) {
            scheduleOfflinePixels()
        }
    }

    private fun scheduleOfflinePixels() {

        Timber.v("Scheduling offline pixels to be sent")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<OfflinePixelWorker>(SERVICE_INTERVAL, SERVICE_TIME_UNIT)
            .addTag(WORK_REQUEST_TAG)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, BACKOFF_INTERVAL, BACKOFF_TIME_UNIT)
            .build()

        workManager.enqueueUniquePeriodicWork(WORK_REQUEST_TAG, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    companion object {
        private const val WORK_REQUEST_TAG = "com.duckduckgo.statistics.offlinepixels.schedule"
        private const val SERVICE_INTERVAL = 3L
        private val SERVICE_TIME_UNIT = TimeUnit.HOURS
        private const val BACKOFF_INTERVAL = 10L
        private val BACKOFF_TIME_UNIT = TimeUnit.MINUTES
    }
}

//@HiltWorker
//open class OfflinePixelWorker @AssistedInject constructor(
//    @Assisted val context: Context,
//    @Assisted params: WorkerParameters,
//    private val offlinePixelSender: OfflinePixelSender
//) : CoroutineWorker(context, params) {
//
//    override suspend fun doWork(): Result {
//        return try {
//            offlinePixelSender
//                .sendOfflinePixels()
//                .blockingAwait()
//            Result.success()
//        } catch (e: Exception) {
//            Result.failure()
//        }
//    }
//}


class OfflinePixelWorker(
    context: Context,
    params: WorkerParameters,
    private val offlinePixelSender: OfflinePixelSender
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            offlinePixelSender
                .sendOfflinePixels()
                .blockingAwait()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
