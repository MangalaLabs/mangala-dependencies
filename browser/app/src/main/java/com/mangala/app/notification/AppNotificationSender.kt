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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.notification.db.NotificationDao
import com.mangala.app.notification.model.Notification
import com.mangala.app.notification.model.SchedulableNotification
import com.mangala.app.notification.model.SchedulableNotificationPlugin
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.statistics.pixels.Pixel
import timber.log.Timber

class AppNotificationSender(
    private val context: Context,
    private val pixel: Pixel,
    private val manager: NotificationManagerCompat,
    private val factory: NotificationFactory,
    private val notificationDao: NotificationDao,
    private val schedulableNotificationPluginPoint: PluginPoint<SchedulableNotificationPlugin>
) : NotificationSender {

    override suspend fun sendNotification(notification: SchedulableNotification) {
        if (!notification.canShow()) {
            Timber.v("Notification no longer showable")
            return
        }

        val specification = notification.buildSpecification()
        val launchIntent = NotificationHandlerService.pendingNotificationHandlerIntent(context, notification.launchIntent, specification)
        val cancelIntent = NotificationHandlerService.pendingNotificationHandlerIntent(context, notification.cancelIntent, specification)
        val systemNotification = factory.createNotification(specification, launchIntent, cancelIntent)
        notificationDao.insert(Notification(notification.id))
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        manager.notify(specification.systemId, systemNotification)

        val plugin = schedulableNotificationPluginPoint.getPlugins().firstOrNull {
            notification.javaClass == it.getSchedulableNotification().javaClass
        }

        if (plugin != null) {
            plugin.onNotificationShown()
        } else {
            pixel.fire("${AppPixelName.NOTIFICATION_SHOWN.pixelName}_${specification.pixelSuffix}")
        }
    }
}
