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

import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import org.koin.android.ext.android.inject
import timber.log.Timber

@Deprecated(
    "This is the old sync service which uses JobScheduler. " +
        "A new version, `AppConfigurationWorker` uses WorkManager and should be used going forwards."
)
class AppConfigurationJobService : JobService() {

    val jobScheduler: JobScheduler by inject()

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.i("Deprecated AppConfigurationJobService running. Unscheduling future syncs using this job")
        jobScheduler.cancel(LEGACY_APP_CONFIGURATION_JOB_ID)
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean = false

    companion object {
        const val LEGACY_APP_CONFIGURATION_JOB_ID = 1
    }
}
