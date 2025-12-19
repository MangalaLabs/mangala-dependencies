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
//import com.alphawallet.token.entity.EthereumMessage;
//import com.alphawallet.token.entity.SignMessageType;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by JB on 13/05/2020.
// */
//public class TokenScriptCallbackInterface {
//
//    private final WebView webView;
//    @NonNull
//    private final OnSignPersonalMessageListener onSignPersonalMessageListener;
//    @NonNull
//    private final OnSetValuesListener onSetValuesListener;
//
//    public TokenScriptCallbackInterface(
//            WebView webView,
//            @NonNull OnSignPersonalMessageListener onSignPersonalMessageListener,
//            @NonNull OnSetValuesListener onSetValuesListener) {
//        this.webView = webView;
//        this.onSignPersonalMessageListener = onSignPersonalMessageListener;
//        this.onSetValuesListener = onSetValuesListener;
//    }
//
//    @JavascriptInterface
//    public void signPersonalMessage(int callbackId, String data) {
//        webView.post(() -> onSignPersonalMessageListener.onSignPersonalMessage(new EthereumMessage(data, getUrl(), callbackId, SignMessageType.SIGN_PERSONAL_MESSAGE)));
//    }
//
//    private String getUrl() {
//        return webView == null ? "" : webView.getUrl();
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
