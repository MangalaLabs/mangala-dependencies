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

import android.webkit.URLUtil
import androidx.annotation.WorkerThread
import com.mangala.downloads.api.DownloadCallback
import com.mangala.downloads.api.DownloadFailReason
import com.mangala.downloads.api.FileDownloader
import com.mangala.downloads.api.FileDownloader.PendingFileDownload

class AndroidFileDownloader(
    private val dataUriDownloader: DataUriDownloader,
    private val networkFileDownloader: NetworkFileDownloader
) : FileDownloader {

    @WorkerThread
    override fun download(
        pending: PendingFileDownload,
        callback: DownloadCallback
    ) {
        when {
            pending.isNetworkUrl -> networkFileDownloader.download(pending, callback)
            pending.isDataUrl -> dataUriDownloader.download(pending, callback)
            else -> callback.onError(url = pending.url, reason = DownloadFailReason.UnsupportedUrlType)
        }
    }
}

val PendingFileDownload.isDataUrl get() = URLUtil.isDataUrl(url)

val PendingFileDownload.isNetworkUrl get() = URLUtil.isNetworkUrl(url)
