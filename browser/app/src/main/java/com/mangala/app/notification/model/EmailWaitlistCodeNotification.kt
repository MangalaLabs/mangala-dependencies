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

package com.mangala.app.notification.model

import android.content.Context
import android.os.Bundle
import com.mangala.app.email.db.EmailDataStore
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.CANCEL
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.EMAIL_WAITLIST_CODE
import com.mangala.app.notification.NotificationRegistrar
import com.mangala.app.notification.db.NotificationDao
import timber.log.Timber

class EmailWaitlistCodeNotification(
    private val context: Context,
    private val notificationDao: NotificationDao,
    private val emailDataStore: EmailDataStore
) : SchedulableNotification {

    override val id = "com.mangala.email.waitlist"
    override val launchIntent = EMAIL_WAITLIST_CODE
    override val cancelIntent = CANCEL

    override suspend fun canShow(): Boolean {

        if (notificationDao.exists(id) || !emailDataStore.sendNotification) {
            Timber.v("Notification already seen")
            return false
        }

        return true
    }

    override suspend fun buildSpecification(): NotificationSpec {
        return EmailWaitlistCodeSpecification(context, emailDataStore.inviteCode)
    }
}

class EmailWaitlistCodeSpecification(
    context: Context,
    code: String?
) : NotificationSpec {
    override val channel = NotificationRegistrar.ChannelType.EMAIL_WAITLIST
    override val systemId = NotificationRegistrar.NotificationId.EmailWaitlist
    override val name = context.getString(com.schoolonair.wallet.component.resources.R.string.waitlistNotificationTitle)
    override val icon = com.schoolonair.wallet.component.resources.R.drawable.notification_logo
    override val title: String = context.getString(com.schoolonair.wallet.component.resources.R.string.waitlistNotificationTitle)
    override val description: String = context.getString(com.schoolonair.wallet.component.resources.R.string.waitlistNotificationDescription)
    override val launchButton: String? = null
    override val closeButton: String? = null
    override val pixelSuffix = "ec"
    override val autoCancel = true
    override val bundle: Bundle = Bundle()
    override val color: Int = com.schoolonair.wallet.component.resources.R.color.ic_launcher_red_background
}
