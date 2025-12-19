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


package com.mangala.app.privacy.cleanup

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.mangala.app.trackerdetection.db.WebTrackersBlockedDao
import com.mangala.app.global.formatters.time.DatabaseDateFormatter
import com.mangala.app.waitlist.email.EmailWaitlistCodeFetcher
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import java.util.concurrent.TimeUnit

//@Module
//@InstallIn(SingletonComponent::class)
//class TrackersDbCleanerSchedulerModule {
//
//    @Provides
//    @IntoSet
//    fun provideDeviceShieldNotificationScheduler(
//        workManager: WorkManager
//    ): LifecycleObserver {
//        return TrackersDbCleanerScheduler(workManager)
//    }
//}

val trackersDbCleanerSchedulerModule = module {
    single(createdAtStart = true) {
        TrackersDbCleanerScheduler(workManager = get())
    }
}


class TrackersDbCleanerScheduler(private val workManager: WorkManager) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        Timber.v("Scheduling Trackers Blocked DB cleaner")
        val dbCleanerWorkRequest = PeriodicWorkRequestBuilder<TrackersDbCleanerWorker>(7, TimeUnit.DAYS)
            .addTag(WORKER_TRACKERS_BLOCKED_DB_CLEANER_TAG)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(WORKER_TRACKERS_BLOCKED_DB_CLEANER_TAG, ExistingPeriodicWorkPolicy.KEEP, dbCleanerWorkRequest)
    }

    companion object {
        const val WORKER_TRACKERS_BLOCKED_DB_CLEANER_TAG = "TrackersBlockedDbCleanerTag"
    }
}

class TrackersDbCleanerWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val webTrackersBlockedDao: WebTrackersBlockedDao,
) : CoroutineWorker(context, workerParams), CoroutineScope {

    @WorkerThread
    override suspend fun doWork(): Result {

        webTrackersBlockedDao.deleteOldDataUntil(dateOfLastWeek())

        Timber.i("Clear trackers dao job finished; returning SUCCESS")
        return Result.success()
    }

    private fun dateOfLastWeek(): String {
        val midnight = LocalDateTime.now().minusDays(7)
        return DatabaseDateFormatter.timestamp(midnight)
    }
}
