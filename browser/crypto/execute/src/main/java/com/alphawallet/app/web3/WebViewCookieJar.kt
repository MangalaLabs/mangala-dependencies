/*
 * Copyright (c) 2019-2023 AlphaWallet
 * Copyright (c) 2023-2025 Mangala Wallet
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Modified from original source: https://github.com/AlphaWallet/alpha-wallet-android
 */

package com.alphawallet.app.web3

import android.text.TextUtils
import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class WebViewCookieJar : CookieJar {
    private var webViewCookieManager: CookieManager? = null

    init {
        try {
            webViewCookieManager = CookieManager.getInstance()
        } catch (ex: Exception) {
            /* Caused by android.content.pm.PackageManager$NameNotFoundException com.google.android.webview */
        }
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (webViewCookieManager != null) {
            val urlString = url.toString()
            for (cookie in cookies) {
                webViewCookieManager?.setCookie(urlString, cookie.toString())
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        if (webViewCookieManager != null) {
            val urlString = url.toString()
            val cookiesString = webViewCookieManager?.getCookie(urlString)
            if (cookiesString != null && !TextUtils.isEmpty(cookiesString)) {
                val cookieHeaders =
                    cookiesString.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val cookies: MutableList<Cookie> = ArrayList()
                for (cookieHeader in cookieHeaders) {
                    val c = Cookie.parse(url, cookieHeader)
                    c?.let {
                        cookies.add(c)
                    }

                }
                return cookies
            }
        }
        return emptyList()
    }
}
