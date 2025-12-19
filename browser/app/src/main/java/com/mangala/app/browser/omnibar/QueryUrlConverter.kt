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



package com.mangala.app.browser.omnibar

import android.net.Uri
import android.webkit.URLUtil
import com.mangala.app.browser.RequestRewriter
import com.mangala.app.global.AppUrl
import com.mangala.app.global.AppUrl.Url
import com.mangala.app.global.UriString
import com.mangala.app.global.UrlScheme.Companion.https
import com.mangala.app.global.withScheme
import timber.log.Timber

class QueryUrlConverter(private val requestRewriter: RequestRewriter) :
    OmnibarEntryConverter {

    override fun convertQueryToUrl(
        searchQuery: String,
        vertical: String?,
        queryOrigin: QueryOrigin
    ): String {
        val isUrl = when (queryOrigin) {
            is QueryOrigin.FromAutocomplete -> queryOrigin.isNav
            is QueryOrigin.FromUser -> UriString.isWebUrl(searchQuery)
        }

        if (isUrl == true) {
            val result =  convertUri(searchQuery)
            return result.replace("http://", "https://")
        }
        if (URLUtil.isDataUrl(searchQuery)) {
            return searchQuery
        }

        val uriBuilder = Uri.Builder()
            .scheme(https)
            .appendQueryParameter(AppUrl.ParamKey.QUERY, searchQuery)
            .authority(Url.HOST)

        if (vertical != null && majorVerticals.contains(vertical)) {
            uriBuilder.appendQueryParameter(AppUrl.ParamKey.VERTICAL_REWRITE, vertical)
        }

        requestRewriter.addCustomQueryParams(uriBuilder)
        return uriBuilder.build().toString()
    }

    private fun convertUri(input: String): String {
        val uri = Uri.parse(input).withScheme()

        if (uri.host == Url.HOST) {
            return requestRewriter.rewriteRequestWithCustomQueryParams(uri).toString()
        }
        return uri.toString()
    }

    companion object {
        val majorVerticals = listOf("images", "videos", "news", "shopping")
    }
}
