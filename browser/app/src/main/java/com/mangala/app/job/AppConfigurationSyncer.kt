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


package com.mangala.app.job

import android.annotation.SuppressLint
import androidx.annotation.CheckResult
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.mangala.app.global.job.AppConfigurationSyncWorkRequestBuilder
import com.mangala.app.global.job.AppConfigurationSyncWorkRequestBuilder.Companion.APP_CONFIG_SYNC_WORK_TAG

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.koin.dsl.module
import timber.log.Timber

//@Module
//@InstallIn(SingletonComponent::class)
//class AppConfigurationSyncerModule {
//    @Provides
//    @Singleton
//    @IntoSet
//    fun provideAppConfigurationSyncer(
//        appConfigurationSyncWorkRequestBuilder: AppConfigurationSyncWorkRequestBuilder,
//        workManager: WorkManager,
//        appConfigurationDownloader: ConfigurationDownloader
//    ): LifecycleObserver {
//        return AppConfigurationSyncer(appConfigurationSyncWorkRequestBuilder, workManager, appConfigurationDownloader)
//    }
//}

val appConfigurationSyncerModule = module {
    single {
        AppConfigurationSyncWorkRequestBuilder()
    }
    single {
        AppConfigurationSyncer(
            appConfigurationSyncWorkRequestBuilder = get(),
            workManager = get(),
            appConfigurationDownloader = get()
        )
    }
}

@VisibleForTesting
class AppConfigurationSyncer(
    private val appConfigurationSyncWorkRequestBuilder: AppConfigurationSyncWorkRequestBuilder,
    private val workManager: WorkManager,
    private val appConfigurationDownloader: ConfigurationDownloader
) : DefaultLifecycleObserver {

    @SuppressLint("CheckResult")
    @UiThread
    override fun onCreate(owner: LifecycleOwner) {
        scheduleImmediateSync()
            .subscribeOn(Schedulers.io())
            .doAfterTerminate {
                scheduleRegularSync()
            }
            .subscribe({}, { Timber.w("Failed to download initial app configuration ${it.localizedMessage}") })
    }

    @CheckResult
    fun scheduleImmediateSync(): Completable {
        Timber.i("Running immediate attempt to download app configuration")
        return appConfigurationDownloader.downloadTask()
    }

    fun scheduleRegularSync() {
        Timber.i("Scheduling regular sync")
        val workRequest = appConfigurationSyncWorkRequestBuilder.appConfigurationWork()
        workManager.enqueueUniquePeriodicWork(APP_CONFIG_SYNC_WORK_TAG, ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }
}
