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



package com.mangala.app.email

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.mangala.app.browser.MangalaUrlDetector
import com.mangala.app.global.DispatcherProvider
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.privacy.config.api.Autofill
import com.mangala.privacy.config.api.PrivacyFeatureName
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class EmailJavascriptInterface(
    private val emailManager: EmailManager,
    private val webView: WebView,
    private val urlDetector: MangalaUrlDetector,
    private val dispatcherProvider: DispatcherProvider,
    private val featureToggle: FeatureToggle,
    private val autofill: Autofill,
    private val showNativeTooltip: () -> Unit
) {

    private fun getUrl(): String? {
        return runBlocking(dispatcherProvider.main()) {
            webView.url
        }
    }

    private fun isUrlFromMangalaEmail(): Boolean {
        val url = getUrl()
        return (url != null && urlDetector.isMangalaEmailUrl(url))
    }

    private fun isFeatureEnabled() = featureToggle.isFeatureEnabled(PrivacyFeatureName.AutofillFeatureName, defaultValue = true)

    @JavascriptInterface
    fun isSignedIn(): String {
        return if (isUrlFromMangalaEmail()) {
            emailManager.isSignedIn().toString()
        } else {
            ""
        }
    }

    @JavascriptInterface
    fun getUserData(): String {
        return if (isUrlFromMangalaEmail()) {
            emailManager.getUserData()
        } else {
            ""
        }
    }

    @JavascriptInterface
    fun getDeviceCapabilities(): String {
        return if (isUrlFromMangalaEmail()) {
            JSONObject().apply {
                put("addUserData", true)
                put("getUserData", true)
                put("removeUserData", true)
            }.toString()
        } else {
            ""
        }
    }

    @JavascriptInterface
    fun storeCredentials(
        token: String,
        username: String,
        cohort: String
    ) {
        if (isUrlFromMangalaEmail()) {
            emailManager.storeCredentials(token, username, cohort)
        }
    }

    @JavascriptInterface
    fun removeCredentials() {
        if (isUrlFromMangalaEmail()) {
            emailManager.signOut()
        }
    }

    @JavascriptInterface
    fun showTooltip() {
        getUrl()?.let {
            if (isFeatureEnabled() && !autofill.isAnException(it)) {
                showNativeTooltip()
            }
        }
    }

    companion object {
        const val JAVASCRIPT_INTERFACE_NAME = "EmailInterface"
    }
}
