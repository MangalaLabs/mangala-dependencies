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


package com.mangala.app.waitlist.email

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkManager
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.email.EmailManager
import com.mangala.app.email.EmailManager.FetchCodeResult.Code
import com.mangala.app.email.EmailManager.FetchCodeResult.CodeExisted
import com.mangala.app.email.EmailManager.FetchCodeResult.NoCode
import com.mangala.app.email.EmailManager.WaitlistState
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.notification.NotificationSender
import com.mangala.app.notification.model.SchedulableNotification
import com.mangala.app.waitlist.email.EmailWaitlistWorkRequestBuilder.Companion.EMAIL_WAITLIST_SYNC_WORK_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface EmailWaitlistCodeFetcher : DefaultLifecycleObserver {
    suspend fun fetchInviteCode()
}

class AppEmailWaitlistCodeFetcher(
    private val workManager: WorkManager,
    private val emailManager: EmailManager,
    private val notification: SchedulableNotification,
    private val notificationSender: NotificationSender,
    private val dispatcherProvider: DispatcherProvider,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope
) : EmailWaitlistCodeFetcher {

    override fun onStart(owner: LifecycleOwner) {
        appCoroutineScope.launch {
            if (emailManager.waitlistState() is WaitlistState.JoinedQueue) {
                fetchInviteCode()
            }
        }
    }

    override suspend fun fetchInviteCode() {
        appCoroutineScope.launch(dispatcherProvider.io()) {
            when (emailManager.fetchInviteCode()) {
                CodeExisted -> {
                    workManager.cancelAllWorkByTag(EMAIL_WAITLIST_SYNC_WORK_TAG)
                }
                Code -> {
                    workManager.cancelAllWorkByTag(EMAIL_WAITLIST_SYNC_WORK_TAG)
                    notificationSender.sendNotification(notification)
                }
                NoCode -> {
                    // NOOP
                }
            }
        }
    }
}
