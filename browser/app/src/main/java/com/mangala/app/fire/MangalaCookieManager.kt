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


package com.mangala.app.fire

import com.mangala.app.browser.cookies.CookieManagerProvider
import com.mangala.app.global.DispatcherProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface MangalaCookieManager {
    suspend fun removeExternalCookies()
    fun flush()
}

class WebViewCookieManager(
    private val cookieManager: CookieManagerProvider,
    private val host: String,
    private val removeCookies: RemoveCookiesStrategy,
    private val dispatcher: DispatcherProvider
) : MangalaCookieManager {

    override suspend fun removeExternalCookies() {
        withContext(dispatcher.io()) {
            flush()
        }
        // The Fire Button does not delete the user's search settings, which are saved as cookies.
        // Removing these cookies would reset them and have undesired consequences, i.e. changing the theme, default language, etc.
        // These cookies are not stored in a personally identifiable way. For example, the large size setting is stored as 's=l.'
        // More info in https://mangala.com/privacy
        val ddgCookies = getMangalaCookies()
        if (cookieManager.get().hasCookies()) {
            removeCookies.removeCookies()
            storeMangalaCookies(ddgCookies)
        }
        withContext(dispatcher.io()) {
            flush()
        }
    }

    private suspend fun storeMangalaCookies(cookies: List<String>) {
        cookies.forEach {
            val cookie = it.trim()
            Timber.d("Restoring DDB cookie: $cookie")
            storeCookie(cookie)
        }
    }

    private suspend fun storeCookie(cookie: String) {
        suspendCoroutine<Unit> { continuation ->
            cookieManager.get().setCookie(host, cookie) { success ->
                Timber.v("Cookie $cookie stored successfully: $success")
                continuation.resume(Unit)
            }
        }
    }

    private fun getMangalaCookies(): List<String> {
        return cookieManager.get().getCookie(host)?.split(";").orEmpty()
    }

    override fun flush() {
        cookieManager.get().flush()
    }
}
