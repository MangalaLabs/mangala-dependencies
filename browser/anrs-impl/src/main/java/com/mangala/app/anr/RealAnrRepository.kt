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


package com.mangala.app.anr

import com.mangala.anrs.api.Anr
import com.mangala.app.anrs.store.AnrEntity
import com.mangala.app.anrs.store.AnrsDatabase
import com.mangala.anrs.api.AnrRepository

class RealAnrRepository (private val anrDatabase: AnrsDatabase) : AnrRepository {
    override fun getAllAnrs(): List<Anr> {
        return anrDatabase.arnDao().getAnrs().asAnrs()
    }

    override fun peekMostRecentAnr(): Anr? {
        return anrDatabase.arnDao().latestAnr()?.asAnr()
    }

    override fun removeMostRecentAnr(): Anr? {
        return anrDatabase.arnDao().latestAnr()?.let { entity ->
            anrDatabase.arnDao().deleteAnr(entity.hash)
            entity
        }?.asAnr()
    }
}

private fun AnrEntity.asAnr(): Anr {
    return Anr(
        message = message,
        name = name,
        file = file,
        lineNumber = lineNumber,
        stackTrace = stackTrace,
        timestamp = timestamp
    )
}

private fun List<AnrEntity>.asAnrs(): List<Anr> {
    val anrs = mutableListOf<Anr>()
    forEach { anrEntity ->
        anrs.add(anrEntity.asAnr())
    }

    return anrs
}
