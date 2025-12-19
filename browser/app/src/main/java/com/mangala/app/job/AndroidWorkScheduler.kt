/*
 * Copyright 2023-2024 Mangala Wallet
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
 * This file uses patterns and conventions from eos-jvm
 * (https://github.com/memtrip/eos-jvm) by memtrip LTD.
 */

package com.mangala.app.job

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DefaultDispatcherProvider
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.job.JobCleaner
import com.mangala.app.notification.AndroidNotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class AndroidWorkScheduler (
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope,
    private val notificationScheduler: AndroidNotificationScheduler,
    private val jobCleaner: JobCleaner,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        Timber.v("Scheduling work")
        appCoroutineScope.launch(dispatcherProvider.default()) {
            jobCleaner.cleanDeprecatedJobs()
            notificationScheduler.scheduleNextNotification()
        }
    }
}
