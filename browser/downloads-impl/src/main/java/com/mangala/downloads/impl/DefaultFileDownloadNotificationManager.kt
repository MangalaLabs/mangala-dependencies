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



package com.mangala.downloads.impl

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.annotation.AnyThread
import androidx.core.app.NotificationCompat
import com.mangala.downloads.api.FileDownloadNotificationManager

@AnyThread
class DefaultFileDownloadNotificationManager(
    private val notificationManager: NotificationManager,
    private val applicationContext: Context
) : FileDownloadNotificationManager {

    override fun showDownloadInProgressNotification() {
        mainThreadHandler().post {
            val notification = NotificationCompat.Builder(applicationContext, FileDownloadNotificationChannelType.FILE_DOWNLOADING.id)
                .setContentTitle(applicationContext.getString(com.schoolonair.wallet.component.resources.R.string.downloadInProgress))
                .setSmallIcon(com.schoolonair.wallet.component.resources.R.drawable.ic_file_download_white_24dp)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    override fun showDownloadFinishedNotification(filename: String, uri: Uri, mimeType: String?) {
        mainThreadHandler().post {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
            }

            val pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

            val notification = NotificationCompat.Builder(applicationContext, FileDownloadNotificationChannelType.FILE_DOWNLOADED.id)
                .setContentTitle(filename)
                .setContentText(applicationContext.getString(com.schoolonair.wallet.component.resources.R.string.downloadComplete))
                .setContentIntent(PendingIntent.getActivity(applicationContext, 0, intent, pendingIntentFlags))
                .setAutoCancel(true)
                .setSmallIcon(com.schoolonair.wallet.component.resources.R.drawable.ic_file_download_white_24dp)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    override fun showDownloadFailedNotification() {
        mainThreadHandler().post {
            val notification = NotificationCompat.Builder(applicationContext, FileDownloadNotificationChannelType.FILE_DOWNLOADED.id)
                .setContentTitle(applicationContext.getString(com.schoolonair.wallet.component.resources.R.string.downloadFailed))
                .setSmallIcon(com.schoolonair.wallet.component.resources.R.drawable.ic_file_download_white_24dp)
                .build()

            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun mainThreadHandler() = Handler(Looper.getMainLooper())

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}
