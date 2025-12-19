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



package com.mangala.app.trackerdetection.api

import com.mangala.app.trackerdetection.model.Action
import com.mangala.app.trackerdetection.model.Rule
import com.mangala.app.trackerdetection.model.TdsDomainEntity
import com.mangala.app.trackerdetection.model.TdsEntity
import com.mangala.app.trackerdetection.model.TdsTracker
import com.squareup.moshi.FromJson
import java.util.*

class TdsJson {

    lateinit var entities: Map<String, TdsJsonEntity>
    lateinit var domains: Map<String, String?>
    lateinit var trackers: Map<String, TdsJsonTracker>

    fun jsonToEntities(): List<TdsEntity> {
        return entities.mapNotNull { (key, value) ->
            TdsEntity(key, value.displayName.takeIf { !it.isNullOrBlank() } ?: key, value.prevalence)
        }
    }

    fun jsonToDomainEntities(): List<TdsDomainEntity> {
        return domains.mapNotNull { (key, value) ->
            if (value == null) null
            else TdsDomainEntity(key, value)
        }
    }

    fun jsonToTrackers(): Map<String, TdsTracker> {
        return trackers.mapNotNull { (key, value) ->
            val domain = value.domain ?: return@mapNotNull null
            val default = value.default ?: return@mapNotNull null
            val owner = value.owner ?: return@mapNotNull null
            key to TdsTracker(domain, default, owner.name, value.categories ?: emptyList(), value.rules ?: emptyList())
        }.toMap()
    }
}

class TdsJsonEntity(
    val displayName: String?,
    val prevalence: Double
)

data class TdsJsonTracker(
    val domain: String?,
    val default: Action?,
    val owner: TdsJsonOwner?,
    val categories: List<String>?,
    val rules: List<Rule>?
)

data class TdsJsonOwner(
    val name: String
)

class ActionJsonAdapter {

    @FromJson
    fun fromJson(actionName: String): Action? {
        return Action.values().firstOrNull { it.name == actionName.uppercase(Locale.ROOT) }
    }
}
