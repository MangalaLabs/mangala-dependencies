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

//package com.alphawallet.app.walletconnect
//
//import android.net.Uri
//
//data class WCSession(
//    val topic: String,
//    val version: String,
//    val bridge: String,
//    val key: String
//) {
//    companion object {
//        fun from(from: String): WCSession? {
//            if (!from.startsWith("wc:")) return null
//
//            val uriString = from.replace("wc:", "wc://")
//            val uri = Uri.parse(uriString)
//            val bridge = uri.getQueryParameter("bridge")
//            val key = uri.getQueryParameter("key")
//            val topic = uri.userInfo
//            val version = uri.host
//
//            if (bridge == null || key == null || topic == null || version == null) {
//                return null
//            }
//
//            return WCSession(topic, version, bridge, key)
//        }
//    }
//}
