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



package com.mangala.app.dev.settings.api

import com.mangala.app.dev.settings.db.DevSettingsDataStore
import com.mangala.app.global.api.ApiInterceptorPlugin
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiDevTdsInterceptor @Inject constructor(
    private val devSettingsDataStore: DevSettingsDataStore
) : ApiInterceptorPlugin, Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        val url = chain.request().url
        if (url.toString().contains(TDS_URL)) {
            val tds = if (devSettingsDataStore.nextTdsEnabled) {
                NEXT_TDS
            } else {
                TDS
            }
            request.url("$TDS_URL$tds")
        }

        return chain.proceed(request.build())
    }

    override fun getInterceptor(): Interceptor {
        return this
    }

    companion object {
        private const val TDS_URL = "https://example.com/tds/"
        private const val NEXT_TDS = "tds-next.json"
        private const val TDS = "tds.json"
    }
}
