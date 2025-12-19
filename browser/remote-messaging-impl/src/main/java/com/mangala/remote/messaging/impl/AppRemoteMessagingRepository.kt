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



package com.mangala.remote.messaging.impl

import com.mangala.app.global.DispatcherProvider
import com.mangala.remote.messaging.api.Action
import com.mangala.remote.messaging.api.Action.ActionType
import com.mangala.remote.messaging.api.Action.DefaultBrowser
import com.mangala.remote.messaging.api.Action.Dismiss
import com.mangala.remote.messaging.api.Action.PlayStore
import com.mangala.remote.messaging.api.Action.Url
import com.mangala.remote.messaging.api.Content
import com.mangala.remote.messaging.api.Content.BigSingleAction
import com.mangala.remote.messaging.api.Content.BigTwoActions
import com.mangala.remote.messaging.api.Content.Medium
import com.mangala.remote.messaging.api.Content.MessageType
import com.mangala.remote.messaging.api.Content.Small
import com.mangala.remote.messaging.api.RemoteMessage
import com.mangala.remote.messaging.api.RemoteMessagingRepository
import com.mangala.remote.messaging.store.RemoteMessageEntity
import com.mangala.remote.messaging.store.RemoteMessageEntity.Status
import com.mangala.remote.messaging.store.RemoteMessageEntity.Status.SCHEDULED
import com.mangala.remote.messaging.store.RemoteMessagesDao
import com.mangala.remote.messaging.store.RemoteMessagingConfigRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppRemoteMessagingRepository(
    private val remoteMessagingConfigRepository: RemoteMessagingConfigRepository,
    private val remoteMessagesDao: RemoteMessagesDao,
    private val dispatchers: DispatcherProvider
) : RemoteMessagingRepository {

    private val messageMapper = MessageMapper()

    override fun activeMessage(message: RemoteMessage?) {
        if (message == null) {
            remoteMessagesDao.deleteActiveMessages()
        } else {
            val stringMessage = messageMapper.toString(message)
            remoteMessagesDao.newMessage(RemoteMessageEntity(id = message.id ?: "", message = stringMessage, status = SCHEDULED))
        }
    }

    override fun didShow(id: String) = remoteMessagesDao.messagesById(id)?.shown ?: false

    override fun markAsShown(remoteMessage: RemoteMessage) {
        val message = remoteMessagesDao.messagesById(remoteMessage.id ?: "") ?: return
        remoteMessagesDao.insert(message.copy(shown = true))
    }

    override fun messageFlow(): Flow<RemoteMessage?> {
        return remoteMessagesDao.messagesFlow().distinctUntilChanged().map {
            if (it == null || it.message.isEmpty()) return@map null

            val message = messageMapper.fromMessage(it.message) ?: return@map null
            RemoteMessage(
                id = it.id,
                content = message.content,
                emptyList(), emptyList()
            )
        }
    }

    override suspend fun dismissMessage(id: String) {
        withContext(dispatchers.io()) {
            remoteMessagesDao.updateState(id, Status.DISMISSED)
            remoteMessagingConfigRepository.invalidate()
        }
    }

    override fun dismissedMessages(): List<String> {
        return remoteMessagesDao.dismissedMessages().map { it.id }.toList()
    }

    private class MessageMapper {

        fun toString(sitePayload: RemoteMessage): String {
            return messageAdapter.toJson(sitePayload)
        }

        fun fromMessage(payload: String): RemoteMessage? {
            return runCatching {
                messageAdapter.fromJson(payload)
            }.getOrNull()
        }

        companion object {
            private val moshi = Moshi.Builder()
//                .add(KotlinJsonAdapterFactory())
                .add(
                    PolymorphicJsonAdapterFactory.of(Content::class.java, "messageType")
                        .withSubtype(Small::class.java, MessageType.SMALL.name)
                        .withSubtype(Medium::class.java, MessageType.MEDIUM.name)
                        .withSubtype(BigSingleAction::class.java, MessageType.BIG_SINGLE_ACTION.name)
                        .withSubtype(BigTwoActions::class.java, MessageType.BIG_TWO_ACTION.name)
                )
                .add(
                    PolymorphicJsonAdapterFactory.of(Action::class.java, "actionType")
                        .withSubtype(PlayStore::class.java, ActionType.PLAYSTORE.name)
                        .withSubtype(Url::class.java, ActionType.URL.name)
                        .withSubtype(Dismiss::class.java, ActionType.DISMISS.name)
                        .withSubtype(DefaultBrowser::class.java, ActionType.DEFAULT_BROWSER.name)
                )
                .add(
                    PolymorphicJsonAdapterFactory.of(Content.Placeholder::class.java, "placeholder")
//                        .withSubtype(PlayStore::class.java, ActionType.PLAYSTORE.name)
//                        .withSubtype(Url::class.java, ActionType.URL.name)
//                        .withSubtype(Dismiss::class.java, ActionType.DISMISS.name)
//                        .withSubtype(DefaultBrowser::class.java, ActionType.DEFAULT_BROWSER.name)
                )
                .add(KotlinJsonAdapterFactory())
                .build()
            val messageAdapter: JsonAdapter<RemoteMessage> = moshi.adapter(RemoteMessage::class.java)
        }
    }
}
