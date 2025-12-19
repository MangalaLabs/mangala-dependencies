/*
 * ORIGINAL COPYRIGHT:
 * Copyright (c) 2019-2023 AlphaWallet
 * Licensed under the MIT License (MIT).
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
 * ----------------------------------------------------------------
 * SOURCE:
 * Derived from: https://github.com/AlphaWallet/alpha-wallet-android
 *
 * ----------------------------------------------------------------
 * MODIFICATIONS:
 * Modified by Mangala Wallet for Kotlin Multiplatform compatibility.
 * ----------------------------------------------------------------
 */

package com.alphawallet.app.web3;//package com.alphawallet.app.web3;
//
//import android.webkit.JavascriptInterface;
//import android.webkit.WebView;
//
//import androidx.annotation.NonNull;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by JB on 1/05/2020.
// */
//public class ValueCallbackJSInterface
//{
//    private final WebView webView;
//    @NonNull
//    private final OnSetValuesListener onSetValuesListener;
//
//    public ValueCallbackJSInterface(
//            WebView webView,
//            @NonNull OnSetValuesListener onSetValuesListener)
//    {
//        this.webView = webView;
//        this.onSetValuesListener = onSetValuesListener;
//    }
//
//    @JavascriptInterface
//    public void setValues(String jsonValuesFromTokenView) {
//        Map<String, String> updates;
//        try
//        {
//            updates = new Gson().fromJson(jsonValuesFromTokenView, new TypeToken<HashMap<String, String>>() {}.getType());
//        }
//        catch (Exception e)
//        {
//            updates = new HashMap<>();
//        }
//
//        onSetValuesListener.setValues(updates);
//    }
//}
