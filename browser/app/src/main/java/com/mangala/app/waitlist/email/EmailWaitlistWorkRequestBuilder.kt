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

import android.content.Context
import androidx.work.*
import com.mangala.app.email.EmailManager
import com.mangala.app.email.EmailManager.FetchCodeResult
import com.mangala.app.notification.NotificationSender
import com.mangala.app.notification.model.EmailWaitlistCodeNotification
import java.util.concurrent.TimeUnit

class EmailWaitlistWorkRequestBuilder {

    fun waitlistRequestWork(withBigDelay: Boolean = true): OneTimeWorkRequest {
        val requestBuilder = OneTimeWorkRequestBuilder<EmailWaitlistWorker>()
            .setConstraints(networkAvailable())
            .addTag(EMAIL_WAITLIST_SYNC_WORK_TAG)

        if (withBigDelay) {
            requestBuilder.setInitialDelay(1, TimeUnit.DAYS)
        } else {
            requestBuilder.setInitialDelay(5, TimeUnit.MINUTES)
        }

        return requestBuilder.build()
    }

    private fun networkAvailable() = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    companion object {
        const val EMAIL_WAITLIST_SYNC_WORK_TAG = "EmailWaitlistWorker"
    }
}

class EmailWaitlistWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val emailManager: EmailManager,
    private val notificationSender: NotificationSender,
    private val notification: EmailWaitlistCodeNotification,
    private val emailWaitlistWorkRequestBuilder: EmailWaitlistWorkRequestBuilder
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        when (emailManager.fetchInviteCode()) {
            FetchCodeResult.CodeExisted -> Result.success()
            FetchCodeResult.Code -> notificationSender.sendNotification(notification)
            FetchCodeResult.NoCode -> WorkManager.getInstance(context).enqueue(emailWaitlistWorkRequestBuilder.waitlistRequestWork())
        }

        return Result.success()
    }
}
