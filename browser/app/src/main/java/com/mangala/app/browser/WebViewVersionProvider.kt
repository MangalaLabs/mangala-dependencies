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

import com.mangala.app.browser.WebViewVersionProvider.Companion.WEBVIEW_UNKNOWN_VERSION


interface WebViewVersionProvider {
    companion object {
        const val WEBVIEW_UNKNOWN_VERSION = "unknown"
    }

    fun getFullVersion(): String
    fun getMajorVersion(): String
}

class DefaultWebViewVersionProvider (
    private val webViewVersionSource: WebViewVersionSource
) : WebViewVersionProvider {
    companion object {
        const val WEBVIEW_VERSION_DELIMITER = "."
    }

    override fun getFullVersion(): String = webViewVersionSource.get().mapEmptyToUnknown()

    override fun getMajorVersion(): String =
        webViewVersionSource.get().captureMajorVersion().mapNonIntegerToUnknown()

    private fun String.captureMajorVersion() = this.split(WEBVIEW_VERSION_DELIMITER)[0]

    private fun String.mapNonIntegerToUnknown() =
        if (isNotBlank() && all(Char::isDigit)) this else WEBVIEW_UNKNOWN_VERSION

    private fun String.mapEmptyToUnknown() = if (isBlank()) WEBVIEW_UNKNOWN_VERSION else this
}
