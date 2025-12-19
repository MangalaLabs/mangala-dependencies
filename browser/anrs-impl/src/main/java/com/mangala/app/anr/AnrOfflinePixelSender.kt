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

import android.util.Base64
import com.mangala.anrs.api.AnrRepository
import com.mangala.app.statistics.api.OfflinePixel
import com.mangala.app.statistics.api.PixelSender
import com.mangala.app.statistics.pixels.Pixel
import io.reactivex.Completable
import io.reactivex.Completable.complete
import io.reactivex.Completable.defer

class AnrOfflinePixelSender(
    private val anrRepository: AnrRepository,
    private val pixelSender: PixelSender,
) : OfflinePixel {
    override fun send(): Completable {
        return defer {
            val anr = anrRepository.peekMostRecentAnr()
            anr?.let {
                val ss = Base64.encodeToString(it.stackTrace.joinToString("\n").toByteArray(), Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE)
                return@defer pixelSender.sendPixel(
                    AnrPixelName.ANR_PIXEL.pixelName,
                    mapOf("stackTrace" to ss),
                    mapOf()
                ).doOnComplete {
                    anrRepository.removeMostRecentAnr()
                }
            }
            return@defer complete()
        }
    }
}

enum class AnrPixelName(override val pixelName: String) : Pixel.PixelName {
    ANR_PIXEL("m_anr_exception")
}
