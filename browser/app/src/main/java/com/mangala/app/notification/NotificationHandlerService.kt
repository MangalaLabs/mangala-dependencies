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

package com.mangala.app.notification

import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationManagerCompat
import com.mangala.app.email.ui.EmailProtectionActivity
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.APPTP_WAITLIST_CODE
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.CANCEL
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.CLEAR_DATA_LAUNCH
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.EMAIL_WAITLIST_CODE
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.WEBSITE
import com.mangala.app.notification.NotificationHandlerService.NotificationEvent.APP_LAUNCH
import com.mangala.app.notification.model.NotificationSpec
import com.mangala.app.notification.model.SchedulableNotificationPlugin
import com.mangala.app.notification.model.WebsiteNotificationSpecification
import com.mangala.app.pixels.AppPixelName.NOTIFICATION_CANCELLED
import com.mangala.app.pixels.AppPixelName.NOTIFICATION_LAUNCHED
import com.mangala.app.settings.SettingsActivity
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.navigation.BrowserActivityNavigationUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


class NotificationHandlerService : IntentService("NotificationHandlerService"), KoinComponent {


    lateinit var pixel: Pixel

    val context: Context by inject<Context>()

    val notificationManager: NotificationManagerCompat by inject()

    val notificationScheduler: AndroidNotificationScheduler by inject()

    val dispatcher: DispatcherProvider by inject()

    val taskStackBuilderFactory: TaskStackBuilderFactory by inject()

    val schedulableNotificationPluginPoint: PluginPoint<SchedulableNotificationPlugin> by inject()

    @VisibleForTesting
    public override fun onHandleIntent(intent: Intent?) {
        val pixelSuffix = intent?.getStringExtra(PIXEL_SUFFIX_EXTRA) ?: return

        when (intent.type) {
            APP_LAUNCH -> onAppLaunched(pixelSuffix)
            CLEAR_DATA_LAUNCH -> onClearDataLaunched(pixelSuffix)
            CANCEL -> onCancelled(pixelSuffix)
            WEBSITE -> onWebsiteNotification(intent, pixelSuffix)
            EMAIL_WAITLIST_CODE -> onEmailWaitlistCodeReceived(pixelSuffix)
            APPTP_WAITLIST_CODE -> onAppTPWaitlistCodeReceived(pixelSuffix)
            else -> {
                schedulableNotificationPluginPoint.getPlugins().forEach {
                    when (intent.type) {
                        it.getSchedulableNotification().launchIntent -> {
                            it.onNotificationLaunched()
                        }
                        it.getSchedulableNotification().cancelIntent -> {
                            it.onNotificationCancelled()
                        }
                    }
                }
            }
        }

        if (intent.getBooleanExtra(NOTIFICATION_AUTO_CANCEL, true)) {
            val notificationId = intent.getIntExtra(NOTIFICATION_SYSTEM_ID_EXTRA, 0)
            clearNotification(notificationId)
        }
    }

    private fun onEmailWaitlistCodeReceived(pixelSuffix: String) {
        Timber.i("Email waitlist code received launched!")
        val intent = EmailProtectionActivity.intent(context)
        taskStackBuilderFactory.createTaskBuilder()
            .addNextIntentWithParentStack(intent)
            .startActivities()
        onLaunched(pixelSuffix)
    }

    private fun onAppTPWaitlistCodeReceived(pixelSuffix: String) {
        Timber.i("App Tracking Protection waitlist code received launched!")
    }

    private fun onWebsiteNotification(
        intent: Intent,
        pixelSuffix: String
    ) {
        val url = intent.getStringExtra(WebsiteNotificationSpecification.WEBSITE_KEY)
        val newIntent = BrowserActivityNavigationUtils.intent(context, queryExtra = url)
        taskStackBuilderFactory.createTaskBuilder()
            .addNextIntentWithParentStack(newIntent)
            .startActivities()
        onLaunched(pixelSuffix)
    }

    private fun onAppLaunched(pixelSuffix: String) {
        val intent = BrowserActivityNavigationUtils.intent(context, newSearch = true)
        taskStackBuilderFactory.createTaskBuilder()
            .addNextIntentWithParentStack(intent)
            .startActivities()
        onLaunched(pixelSuffix)
    }

    private fun onClearDataLaunched(pixelSuffix: String) {
        Timber.i("Clear Data Launched!")
        val intent = SettingsActivity.intent(context)
        taskStackBuilderFactory.createTaskBuilder()
            .addNextIntentWithParentStack(intent)
            .startActivities()
        onLaunched(pixelSuffix)
    }

    private fun onCancelled(pixelSuffix: String) {
        pixel.fire("${NOTIFICATION_CANCELLED.pixelName}_$pixelSuffix")
    }

    private fun onLaunched(pixelSuffix: String) {
        pixel.fire("${NOTIFICATION_LAUNCHED.pixelName}_$pixelSuffix")
    }

    private fun clearNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    object NotificationEvent {
        const val APP_LAUNCH = "com.mangala.notification.launch.app"
        const val CLEAR_DATA_LAUNCH = "com.mangala.notification.launch.clearData"
        const val CANCEL = "com.mangala.notification.cancel"
        const val WEBSITE = "com.mangala.notification.website"
        const val EMAIL_WAITLIST_CODE = "com.mangala.notification.email.waitlist.code"
        const val APPTP_WAITLIST_CODE = "com.mangala.notification.apptp.waitlist.code"
    }

    companion object {
        const val PIXEL_SUFFIX_EXTRA = "PIXEL_SUFFIX_EXTRA"
        const val NOTIFICATION_SYSTEM_ID_EXTRA = "NOTIFICATION_SYSTEM_ID"
        const val NOTIFICATION_AUTO_CANCEL = "NOTIFICATION_AUTO_CANCEL"

        fun pendingNotificationHandlerIntent(
            context: Context,
            eventType: String,
            specification: NotificationSpec
        ): PendingIntent {
            val intent = Intent(context, NotificationHandlerService::class.java)
            intent.type = eventType
            intent.putExtras(specification.bundle)
            intent.putExtra(PIXEL_SUFFIX_EXTRA, specification.pixelSuffix)
            intent.putExtra(NOTIFICATION_SYSTEM_ID_EXTRA, specification.systemId)
            intent.putExtra(NOTIFICATION_AUTO_CANCEL, specification.autoCancel)
            return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)!!
        }
    }
}
