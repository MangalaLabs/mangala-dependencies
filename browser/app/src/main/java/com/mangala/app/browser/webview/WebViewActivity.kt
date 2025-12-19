/*
 * Copyright 2023-2024 Mangala Wallet
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
 * This file uses patterns and conventions from eos-jvm
 * (https://github.com/memtrip/eos-jvm) by memtrip LTD.
 */

package com.mangala.app.browser.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebSettings
import androidx.appcompat.widget.Toolbar
import com.mangala.app.browser.BrowserWebViewClient
import com.mangala.app.browser.useragent.UserAgentProvider
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivityEmailWebviewBinding
import org.koin.android.ext.android.inject

class WebViewActivity : MangalaBrowserActivity() {


    val userAgentProvider: UserAgentProvider by inject()

    val webViewClient: BrowserWebViewClient by inject()

    private val binding: ActivityEmailWebviewBinding by viewBinding()

    private val toolbar
        get() = binding.includeToolbar.findViewById<Toolbar>(R.id.toolbar)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setupToolbar(toolbar)

        val url = intent.getStringExtra(URL_EXTRA)
        title = intent.getStringExtra(TITLE_EXTRA)

        binding.simpleWebview.let {
            it.webViewClient = webViewClient

            it.settings.apply {
                userAgentString = userAgentProvider.userAgent()
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = true
                displayZoomControls = false
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                setSupportMultipleWindows(true)
                databaseEnabled = false
                setSupportZoom(true)
            }
        }

        url?.let {
            binding.simpleWebview.loadUrl(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.simpleWebview.canGoBack()) {
            binding.simpleWebview.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val URL_EXTRA = "URL_EXTRA"
        const val TITLE_EXTRA = "TITLE_EXTRA"

        fun intent(
            context: Context,
            urlExtra: String,
            titleExtra: String
        ): Intent {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(URL_EXTRA, urlExtra)
            intent.putExtra(TITLE_EXTRA, titleExtra)
            return intent
        }
    }
}
