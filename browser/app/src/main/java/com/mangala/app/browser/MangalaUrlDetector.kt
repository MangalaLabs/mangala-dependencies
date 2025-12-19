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



package com.mangala.app.browser

import android.net.Uri
import com.mangala.app.global.AppUrl
import com.mangala.app.global.AppUrl.ParamKey

class MangalaUrlDetector {

    fun isMangalaEmailUrl(url: String): Boolean {
        val uri = url.toUri()
        val firstSegment = uri.pathSegments.firstOrNull()
        return isMangalaUrl(url) && firstSegment == AppUrl.Url.EMAIL_SEGMENT
    }

    fun isMangalaUrl(uri: String): Boolean {
        return AppUrl.Url.HOST == uri.toUri().host
    }

    fun isMangalaQueryUrl(uri: String): Boolean {
        return isMangalaUrl(uri) && hasQuery(uri)
    }

    fun isMangalaStaticUrl(uri: String): Boolean {
        return isMangalaUrl(uri) && matchesStaticPage(uri)
    }

    private fun matchesStaticPage(uri: String): Boolean {
        return when (uri.toUri().path) {
            AppUrl.StaticUrl.SETTINGS -> true
            AppUrl.StaticUrl.PARAMS -> true
            else -> false
        }
    }

    private fun hasQuery(uri: String): Boolean {
        return uri.toUri().queryParameterNames.contains(ParamKey.QUERY)
    }

    fun extractQuery(uriString: String): String? {
        val uri = uriString.toUri()
        return uri.getQueryParameter(ParamKey.QUERY)
    }

    fun isMangalaVerticalUrl(uri: String): Boolean {
        return isMangalaUrl(uri) && hasVertical(uri)
    }

    private fun hasVertical(uri: String): Boolean {
        return uri.toUri().queryParameterNames.contains(ParamKey.VERTICAL)
    }

    fun extractVertical(uriString: String): String? {
        val uri = uriString.toUri()
        return uri.getQueryParameter(ParamKey.VERTICAL)
    }

    private fun String.toUri(): Uri {
        return Uri.parse(this)
    }
}
