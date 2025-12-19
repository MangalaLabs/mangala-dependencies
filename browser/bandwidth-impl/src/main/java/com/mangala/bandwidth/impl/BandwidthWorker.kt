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


package com.mangala.bandwidth.impl

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import timber.log.Timber
import java.util.concurrent.TimeUnit

//@Module
//@InstallIn(SingletonComponent::class)
//class BandwidthSchedulerModule {
//    @Provides
//    @IntoSet
//    fun provideBandwidthScheduler(workManager: WorkManager): LifecycleObserver {
//        return BandwidthScheduler(workManager)
//    }
//}

val bandwidthSchedulerModule = module {
    single{ BandwidthScheduler(get()) }

    single {
        get() as BandwidthScheduler as DefaultLifecycleObserver
    }
}


class BandwidthScheduler(
    private val workManager: WorkManager,
) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        Timber.v("Scheduling Bandwidth Worker")
        val workerRequest = PeriodicWorkRequestBuilder<BandwidthWorker>(1, TimeUnit.HOURS)
            .addTag(BANDWIDTH_WORKER_TAG)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(BANDWIDTH_WORKER_TAG, ExistingPeriodicWorkPolicy.KEEP, workerRequest)
    }

    companion object {
        const val BANDWIDTH_WORKER_TAG = "BANDWIDTH_WORKER_TAG"
    }
}

class BandwidthWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams), CoroutineScope, KoinComponent {

    private val bandwidthCollector: BandwidthCollector by inject()

    @WorkerThread
    override suspend fun doWork(): Result {

        bandwidthCollector.collect()

        Timber.i("Bandwidth job finished; returning SUCCESS")
        return Result.success()
    }
}
