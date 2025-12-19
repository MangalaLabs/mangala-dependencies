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


package com.mangala.app.browser.remotemessage

import com.mangala.app.global.DispatcherProvider
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.remote.messaging.api.Action
import com.mangala.remote.messaging.api.Content
import com.mangala.remote.messaging.api.RemoteMessage
import com.mangala.remote.messaging.api.RemoteMessagingRepository
import kotlinx.coroutines.withContext


class RemoteMessagingModel (
    private val remoteMessagingRepository: RemoteMessagingRepository,
    private val pixel: Pixel,
    private val dispatchers: DispatcherProvider
) {

    val activeMessages = remoteMessagingRepository.messageFlow()

    suspend fun onMessageShown(remoteMessage: RemoteMessage) {
        withContext(dispatchers.io()) {
            tryToFireUniquePixel(remoteMessage)
            pixel.fire(AppPixelName.REMOTE_MESSAGE_SHOWN, remoteMessage.asPixelParams())
        }
    }

    private fun tryToFireUniquePixel(remoteMessage: RemoteMessage) {
        val didShow = remoteMessagingRepository.didShow(remoteMessage.id ?: "")
        if (!didShow) {
            pixel.fire(AppPixelName.REMOTE_MESSAGE_SHOWN_UNIQUE, remoteMessage.asPixelParams())
            remoteMessagingRepository.markAsShown(remoteMessage)
        }
    }

    suspend fun onMessageDismissed(remoteMessage: RemoteMessage) {
        pixel.fire(AppPixelName.REMOTE_MESSAGE_DISMISSED, remoteMessage.asPixelParams())
        withContext(dispatchers.io()) {
            remoteMessagingRepository.dismissMessage(remoteMessage.id ?: "")
        }
    }

    suspend fun onPrimaryActionClicked(remoteMessage: RemoteMessage): Action? {
        pixel.fire(AppPixelName.REMOTE_MESSAGE_PRIMARY_ACTION_CLICKED, remoteMessage.asPixelParams())
        withContext(dispatchers.io()) {
            remoteMessagingRepository.dismissMessage(remoteMessage.id ?: "")
        }
        return remoteMessage.content?.getPrimaryAction()
    }

    suspend fun onSecondaryActionClicked(remoteMessage: RemoteMessage): Action? {
        pixel.fire(AppPixelName.REMOTE_MESSAGE_SECONDARY_ACTION_CLICKED, remoteMessage.asPixelParams())
        withContext(dispatchers.io()) {
            remoteMessagingRepository.dismissMessage(remoteMessage.id ?: "")
        }
        return remoteMessage.content?.getSecondaryAction()
    }

    private fun Content.getPrimaryAction(): Action? {
        return when (this) {
            is Content.BigSingleAction -> {
                this.primaryAction
            }
            is Content.BigTwoActions -> {
                this.primaryAction
            }
            else -> null
        }
    }

    private fun Content.getSecondaryAction(): Action? {
        return when (this) {
            is Content.BigTwoActions -> {
                this.secondaryAction
            }
            else -> null
        }
    }

    private fun RemoteMessage.asPixelParams(): Map<String, String> = mapOf(Pixel.PixelParameter.CTA_SHOWN to (this.id ?: ""))
}
