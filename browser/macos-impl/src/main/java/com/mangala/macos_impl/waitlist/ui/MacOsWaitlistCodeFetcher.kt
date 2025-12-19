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



package com.mangala.macos_impl.waitlist.ui

import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkManager
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.notification.NotificationSender
import com.mangala.app.notification.model.SchedulableNotification
import com.mangala.macos_impl.waitlist.FetchCodeResult.Code
import com.mangala.macos_impl.waitlist.FetchCodeResult.CodeExisted
import com.mangala.macos_impl.waitlist.FetchCodeResult.NoCode
import com.mangala.macos_impl.waitlist.MacOsWaitlistManager
import com.mangala.macos_impl.waitlist.ui.MacOsWaitlistWorkRequestBuilder.Companion.MACOS_WAITLIST_SYNC_WORK_TAG
import com.mangala.macos_store.MacOsWaitlistState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//@Singleton
class MacOsWaitlistCodeFetcher(
    private val workManager: WorkManager,
    private val macOsWaitlistManager: MacOsWaitlistManager,
    private val notification: SchedulableNotification,
    private val notificationSender: NotificationSender,
    private val dispatcherProvider: DispatcherProvider,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope
) : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Event) {
        if (event == Event.ON_START) {
            appCoroutineScope.launch(dispatcherProvider.io()) {
                if (macOsWaitlistManager.getState() is MacOsWaitlistState.JoinedWaitlist) {
                    fetchInviteCode()
                }
            }
        }
    }

    private suspend fun fetchInviteCode() {
        when (macOsWaitlistManager.fetchInviteCode()) {
            CodeExisted -> {
                workManager.cancelAllWorkByTag(MACOS_WAITLIST_SYNC_WORK_TAG)
            }
            Code -> {
                workManager.cancelAllWorkByTag(MACOS_WAITLIST_SYNC_WORK_TAG)
                notificationSender.sendNotification(notification)
            }
            NoCode -> {
                // NOOP
            }
        }
    }
}
