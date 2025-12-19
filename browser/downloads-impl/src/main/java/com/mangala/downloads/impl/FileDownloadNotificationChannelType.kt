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

import androidx.core.app.NotificationManagerCompat
import com.mangala.app.notification.model.Channel
import com.schoolonair.wallet.component.resources.R

object FileDownloadNotificationChannelType {
    val FILE_DOWNLOADING = Channel(
        "com.duckduckgo.downloading",
        R.string.notificationChannelFileDownloading,
        NotificationManagerCompat.IMPORTANCE_LOW
    )
    val FILE_DOWNLOADED = Channel(
        "com.duckduckgo.downloaded",
        R.string.notificationChannelFileDownloaded,
        NotificationManagerCompat.IMPORTANCE_LOW
    )
}
