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

import androidx.core.net.toUri
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.downloads.api.DownloadCallback
import com.mangala.downloads.api.DownloadCommand
import com.mangala.downloads.api.DownloadFailReason
import com.mangala.downloads.api.DownloadFailReason.*
import com.mangala.downloads.api.DownloadsRepository
import com.mangala.downloads.api.FileDownloadNotificationManager
import com.mangala.downloads.api.model.DownloadItem
import com.mangala.downloads.impl.pixels.DownloadsPixelName
import kotlinx.coroutines.channels.BufferOverflow
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider
import com.mangala.downloads.store.DownloadStatus.FINISHED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


class FileDownloadCallback (
    private val fileDownloadNotificationManager: FileDownloadNotificationManager,
    private val downloadsRepository: DownloadsRepository,
    private val pixel: Pixel,
    private val dispatchers: DispatcherProvider,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope
) : DownloadCallback {

    private val command = Channel<DownloadCommand>(1, BufferOverflow.DROP_OLDEST)

    override fun onStart(downloadItem: DownloadItem) {
        Timber.d("Download started for file ${downloadItem.fileName}")
        pixel.fire(DownloadsPixelName.DOWNLOAD_REQUEST_STARTED)
        val downloadStartedMessage = DownloadCommand.ShowDownloadStartedMessage(
            messageId = com.schoolonair.wallet.component.resources.R.string.downloadsDownloadStartedMessage,
            showNotification = downloadItem.downloadId == 0L,
            fileName = downloadItem.fileName
        )
        if (downloadStartedMessage.showNotification) {
            fileDownloadNotificationManager.showDownloadInProgressNotification()
        }
        appCoroutineScope.launch(dispatchers.io()) {
            command.send(downloadStartedMessage)
            downloadsRepository.insert(downloadItem)
        }
    }

    override fun onSuccess(downloadId: Long, contentLength: Long) {
        Timber.d("Download succeeded for file with downloadId $downloadId")
        pixel.fire(DownloadsPixelName.DOWNLOAD_REQUEST_SUCCEEDED)
        appCoroutineScope.launch(dispatchers.io()) {
            downloadsRepository.update(downloadId = downloadId, downloadStatus = FINISHED, contentLength = contentLength)
            downloadsRepository.getDownloadItem(downloadId)?.let {
                command.send(
                    DownloadCommand.ShowDownloadSuccessMessage(
                        messageId = com.schoolonair.wallet.component.resources.R.string.downloadsDownloadFinishedMessage,
                        showNotification = false,
                        fileName = it.fileName,
                        filePath = it.filePath
                    )
                )
            }
        }
    }

    override fun onSuccess(file: File, mimeType: String?) {
        Timber.d("Download succeeded for file with name ${file.name}")
        pixel.fire(DownloadsPixelName.DOWNLOAD_REQUEST_SUCCEEDED)
        fileDownloadNotificationManager.showDownloadFinishedNotification(filename = file.name, uri = file.absolutePath.toUri(), mimeType = mimeType)
        appCoroutineScope.launch(dispatchers.io()) {
            downloadsRepository.update(fileName = file.name, downloadStatus = FINISHED, contentLength = file.length())
            command.send(
                DownloadCommand.ShowDownloadSuccessMessage(
                    messageId = com.schoolonair.wallet.component.resources.R.string.downloadsDownloadFinishedMessage,
                    showNotification = true,
                    fileName = file.name,
                    filePath = file.absolutePath,
                    mimeType = mimeType
                )
            )
        }
    }

    override fun onError(downloadId: Long, reason: DownloadFailReason) {
        Timber.d("Download error for file with downloadId $downloadId and reason $reason.")
        // Called when the DownloadManager completes a download with a failed status.
        // A failed pixel is sent and the database record is cleared.
        handleFailedDownload(showNotification = false, reason = reason)
        appCoroutineScope.launch(dispatchers.io()) {
            downloadsRepository.delete(listOf(downloadId))
        }
    }

    override fun onError(url: String?, reason: DownloadFailReason) {
        Timber.d("Failed to download file with url $url and reason $reason.")
        handleFailedDownload(showNotification = true, reason = reason)
    }

    override fun onCancel(downloadId: Long) {
        // This is a cancelled download either from the app or from the notification.
        // If the database doesn't contain a record for that downloadId it means the download was cancelled by the user from the
        // application. A cancel pixel is sent as it was the user's decision to cancel the started download.
        // If there is a record in the database it will be removed as the download was cancelled from the notification and a cancel pixel sent.
        appCoroutineScope.launch(dispatchers.io()) {
            val item = downloadsRepository.getDownloadItem(downloadId)
            if (item == null) {
                Timber.d("Cancelled download file with downloadId $downloadId from the app.")
            } else {
                Timber.d("Cancelled to download file with downloadId $downloadId from the notification.")
                downloadsRepository.delete(listOf(downloadId))
            }
            pixel.fire(DownloadsPixelName.DOWNLOAD_REQUEST_CANCELLED)
        }
    }

    override fun commands(): Flow<DownloadCommand> {
        return command.receiveAsFlow()
    }

    private fun handleFailedDownload(showNotification: Boolean, reason: DownloadFailReason) {
        pixel.fire(DownloadsPixelName.DOWNLOAD_REQUEST_FAILED)
        val messageId = when (reason) {
            ConnectionRefused -> com.schoolonair.wallet.component.resources.R.string.downloadsErrorMessage
            DownloadManagerDisabled -> com.schoolonair.wallet.component.resources.R.string.downloadsDownloadManagerDisabledErrorMessage
            Other, UnsupportedUrlType, DataUriParseException -> com.schoolonair.wallet.component.resources.R.string.downloadsDownloadGenericErrorMessage
        }
        val downloadFailedMessage = DownloadCommand.ShowDownloadFailedMessage(
            messageId = messageId,
            showNotification = showNotification,
            showEnableDownloadManagerAction = reason == DownloadManagerDisabled
        )
        if (downloadFailedMessage.showNotification) {
            fileDownloadNotificationManager.showDownloadFailedNotification()
        }
        appCoroutineScope.launch(dispatchers.io()) {
            command.send(downloadFailedMessage)
        }
    }
}
