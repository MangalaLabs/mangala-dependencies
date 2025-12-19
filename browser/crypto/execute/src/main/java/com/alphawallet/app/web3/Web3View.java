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
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.webkit.ValueCallback;
//import android.webkit.WebChromeClient;
//import android.webkit.WebResourceError;
//import android.webkit.WebResourceRequest;
//import android.webkit.WebSettings;
//import android.webkit.WebView;
//import android.webkit.WebViewClient;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//
//import com.alphawallet.app.BuildConfig;
//import com.alphawallet.app.entity.URLLoadInterface;
//import com.alphawallet.app.web3.entity.Address;
//import com.alphawallet.app.web3.entity.WalletAddEthereumChainObject;
//import com.alphawallet.app.web3.entity.Web3Call;
//import com.alphawallet.app.web3.entity.Web3Transaction;
//import com.alphawallet.token.entity.EthereumMessage;
//import com.alphawallet.token.entity.EthereumTypedMessage;
//import com.alphawallet.token.entity.Signable;
//
//import org.jetbrains.annotations.Contract;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import timber.log.Timber;
//
//public class Web3View extends WebView {
//    private static final String JS_PROTOCOL_CANCELLED = "cancelled";
//    private static final String JS_PROTOCOL_ON_SUCCESSFUL = "AlphaWallet.executeCallback(%1$s, null, \"%2$s\")";
//    private static final String JS_PROTOCOL_EXPR_ON_SUCCESSFUL = "AlphaWallet.executeCallback(%1$s, null, %2$s)";
//    private static final String JS_PROTOCOL_ON_FAILURE = "AlphaWallet.executeCallback(%1$s, \"%2$s\", null)";
//    private final Web3ViewClient webViewClient;
//    @Nullable
//    private OnSignTransactionListener onSignTransactionListener;
//    private final OnSignTransactionListener innerOnSignTransactionListener = new OnSignTransactionListener() {
//        @Override
//        public void onSignTransaction(Web3Transaction transaction, String url)
//        {
//            Timber.d("1991 onSignTransaction " + transaction.payload + " url " + url);
//            if (onSignTransactionListener != null)
//            {
//                onSignTransactionListener.onSignTransaction(transaction, url);
//            }
//        }
//    };
//    @Nullable
//    private OnSignMessageListener onSignMessageListener;
//    private final OnSignMessageListener innerOnSignMessageListener = new OnSignMessageListener() {
//        @Override
//        public void onSignMessage(EthereumMessage message)
//        {
//            Timber.d("1991 onSignMessage msg " + message.getMessage());
//            if (onSignMessageListener != null)
//            {
//                onSignMessageListener.onSignMessage(message);
//            }
//        }
//    };
//    @Nullable
//    private OnSignPersonalMessageListener onSignPersonalMessageListener;
//    private final OnSignPersonalMessageListener innerOnSignPersonalMessageListener = new OnSignPersonalMessageListener() {
//        @Override
//        public void onSignPersonalMessage(EthereumMessage message)
//        {
//            Timber.d("1991 onSignPersonalMessage " + message.getMessage());
//            onSignPersonalMessageListener.onSignPersonalMessage(message);
//        }
//    };
//    @Nullable
//    private OnSignTypedMessageListener onSignTypedMessageListener;
//    private final OnSignTypedMessageListener innerOnSignTypedMessageListener = new OnSignTypedMessageListener() {
//        @Override
//        public void onSignTypedMessage(EthereumTypedMessage message)
//        {
//            Timber.d("1991 onSignTypedMessage " + message.getMessage());
//            onSignTypedMessageListener.onSignTypedMessage(message);
//        }
//    };
//    @Nullable
//    private OnEthCallListener onEthCallListener;
//    private final OnEthCallListener innerOnEthCallListener = new OnEthCallListener() {
//        @Override
//        public void onEthCall(Web3Call txData)
//        {
//            Timber.d("1991 onEthCall " + txData.payload);
//            onEthCallListener.onEthCall(txData);
//
//        }
//    };
//    @Nullable
//    private OnWalletAddEthereumChainObjectListener onWalletAddEthereumChainObjectListener;
//    private final OnWalletAddEthereumChainObjectListener innerAddChainListener = new OnWalletAddEthereumChainObjectListener() {
//        @Override
//        public void onWalletAddEthereumChainObject(long callbackId, WalletAddEthereumChainObject chainObject)
//        {
//            Timber.d("1991 onWalletAddEthereumChainObject " + chainObject.chainId);
//            onWalletAddEthereumChainObjectListener.onWalletAddEthereumChainObject(callbackId, chainObject);
//        }
//    };
//    @Nullable
//    private OnWalletActionListener onWalletActionListener;
//    private final OnWalletActionListener innerOnWalletActionListener = new OnWalletActionListener() {
//        @Override
//        public void onRequestAccounts(long callbackId)
//        {
//            Timber.d("1991 onRequestAccounts " + callbackId);
//            onWalletActionListener.onRequestAccounts(callbackId);
//        }
//
//        @Override
//        public void onWalletSwitchEthereumChain(long callbackId, WalletAddEthereumChainObject chainObj)
//        {
//            Timber.d("1991 onWalletSwitchEthereumChain " + callbackId);
//            onWalletActionListener.onWalletSwitchEthereumChain(callbackId, chainObj);
//        }
//    };
//    private URLLoadInterface loadInterface;
//
//    public Web3View(@NonNull Context context, @Nullable AttributeSet attrs)
//    {
//        super(context, attrs);
//        webViewClient = new Web3ViewClient(getContext());
//        init();
//    }
//
//    public Web3View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
//    {
//        super(context, attrs, defStyleAttr);
//        webViewClient = new Web3ViewClient(getContext());
//        init();
//    }
//
//    @Override
//    public void setWebChromeClient(WebChromeClient client)
//    {
//        super.setWebChromeClient(client);
//    }
//
//    @Override
//    public void setWebViewClient(WebViewClient client)
//    {
//        super.setWebViewClient(new WrapWebViewClient(webViewClient, client));
//    }
//
//    @Override
//    public void loadUrl(@NonNull String url, @NonNull Map<String, String> additionalHttpHeaders)
//    {
//        super.loadUrl(url, additionalHttpHeaders);
//    }
//
//    @Override
//    public void loadUrl(@NonNull String url)
//    {
//        loadUrl(url, getWeb3Headers());
//    }
//
//    /* Required for CORS requests */
//    @NotNull
//    @Contract(" -> new")
//    private Map<String, String> getWeb3Headers()
//    {
//        //headers
//        return new HashMap<String, String>() {{
//            put("Connection", "close");
//            put("Content-Type", "text/plain");
//            put("Access-Control-Allow-Origin", "*");
//            put("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS");
//            put("Access-Control-Max-Age", "600");
//            put("Access-Control-Allow-Credentials", "true");
//            put("Access-Control-Allow-Headers", "accept, authorization, Content-Type");
//        }};
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    public void init()
//    {
//        getSettings().setJavaScriptEnabled(true);
//        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        getSettings().setBuiltInZoomControls(true);
//        getSettings().setDisplayZoomControls(false);
//        getSettings().setUseWideViewPort(true);
//        getSettings().setLoadWithOverviewMode(true);
//        getSettings().setDomStorageEnabled(true);
//        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        getSettings().setUserAgentString(getSettings().getUserAgentString()
//                + "AlphaWallet(Platform=Android&AppVersion=" + "1.0" + ")");
//        WebView.setWebContentsDebuggingEnabled(true); //so devs can debug their scripts/pages
//        addJavascriptInterface(new SignCallbackJSInterface(
//                this,
//                innerOnSignTransactionListener,
//                innerOnSignMessageListener,
//                innerOnSignPersonalMessageListener,
//                innerOnSignTypedMessageListener,
//                innerOnEthCallListener,
//                innerAddChainListener,
//                innerOnWalletActionListener), "alpha");
//
////        Removing this block for now.
////        TODO: Figure out if we should support dark mode for external websites
////        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK))
////        {
////            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
////            {
////                case Configuration.UI_MODE_NIGHT_YES:
////                    WebSettingsCompat.setForceDark(getSettings(), FORCE_DARK_ON);
////                    break;
////                case Configuration.UI_MODE_NIGHT_NO:
////                case Configuration.UI_MODE_NIGHT_UNDEFINED:
////                    WebSettingsCompat.setForceDark(getSettings(), FORCE_DARK_OFF);
////                    break;
////            }
////        }
//    }
//
//    @Nullable
//    public Address getWalletAddress()
//    {
//        return webViewClient.getJsInjectorClient().getWalletAddress();
//    }
//
//    public void setWalletAddress(@NonNull Address address)
//    {
//        webViewClient.getJsInjectorClient().setWalletAddress(address);
//    }
//
//    public long getChainId()
//    {
//        return webViewClient.getJsInjectorClient().getChainId();
//    }
//
//    public void setChainId(long chainId)
//    {
//        webViewClient.getJsInjectorClient().setChainId(chainId);
//    }
//
//    public void setWebLoadCallback(URLLoadInterface iFace)
//    {
//        loadInterface = iFace;
//    }
//
//    public void setRpcUrl(@NonNull String rpcUrl)
//    {
//        webViewClient.getJsInjectorClient().setRpcUrl(rpcUrl);
//    }
//
//    public void setOnSignTransactionListener(@Nullable OnSignTransactionListener onSignTransactionListener)
//    {
//        this.onSignTransactionListener = onSignTransactionListener;
//    }
//
//    public void setOnSignMessageListener(@Nullable OnSignMessageListener onSignMessageListener)
//    {
//        this.onSignMessageListener = onSignMessageListener;
//    }
//
//    public void setOnSignPersonalMessageListener(@Nullable OnSignPersonalMessageListener onSignPersonalMessageListener)
//    {
//        this.onSignPersonalMessageListener = onSignPersonalMessageListener;
//    }
//
//    public void setOnSignTypedMessageListener(@Nullable OnSignTypedMessageListener onSignTypedMessageListener)
//    {
//        this.onSignTypedMessageListener = onSignTypedMessageListener;
//    }
//
//    public void setOnEthCallListener(@Nullable OnEthCallListener onEthCallListener)
//    {
//        this.onEthCallListener = onEthCallListener;
//    }
//
//    public void setOnWalletAddEthereumChainObjectListener(@Nullable OnWalletAddEthereumChainObjectListener onWalletAddEthereumChainObjectListener)
//    {
//        this.onWalletAddEthereumChainObjectListener = onWalletAddEthereumChainObjectListener;
//    }
//
//    public void setOnWalletActionListener(@Nullable OnWalletActionListener onWalletActionListener)
//    {
//        this.onWalletActionListener = onWalletActionListener;
//    }
//
//    public void onSignTransactionSuccessful(Web3Transaction transaction, String signHex)
//    {
//        long callbackId = transaction.leafPosition;
//        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex);
//    }
//
//    public void onSignMessageSuccessful(Signable message, String signHex)
//    {
//        long callbackId = message.getCallbackId();
//        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex);
//    }
//
//    public void onCallFunctionSuccessful(long callbackId, String result)
//    {
//        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, result);
//    }
//
//    public void onCallFunctionError(long callbackId, String error)
//    {
//        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, error);
//    }
//
//    public void onSignCancel(long callbackId)
//    {
//        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, JS_PROTOCOL_CANCELLED);
//    }
//
//    private void callbackToJS(long callbackId, String function, String param)
//    {
//        String callback = String.format(function, callbackId, param);
//        post(() -> evaluateJavascript(callback, value ->Timber.tag("WEB_VIEW").d(value)));
//    }
//
//    public void onWalletActionSuccessful(long callbackId, String expression)
//    {
//        String callback = String.format(JS_PROTOCOL_EXPR_ON_SUCCESSFUL, callbackId, expression);
//        post(() -> evaluateJavascript(callback, Timber::d));
//    }
//
//    public void resetView()
//    {
//        webViewClient.resetInject();
//    }
//
//    private class WrapWebViewClient extends WebViewClient {
//        private final Web3ViewClient internalClient;
//        private final WebViewClient externalClient;
//        private boolean loadingError = false;
//        private boolean redirect = false;
//
//        public WrapWebViewClient(Web3ViewClient internalClient, WebViewClient externalClient)
//        {
//            this.internalClient = internalClient;
//            this.externalClient = externalClient;
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon)
//        {
//            super.onPageStarted(view, url, favicon);
//            clearCache(true);
//            if (!redirect)
//            {
//                Timber.d("1991 onPageStarted");
//                view.evaluateJavascript(internalClient.getProviderString(view), new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String s) {
//                        Timber.d("1991 onPageStarted getProviderString value " + s);
//                    }
//                });
//                view.evaluateJavascript(internalClient.getInitString(view), new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String s) {
//                        Timber.d("1991 onPageStarted getInitString value " + s);
//                    }
//                });
//                internalClient.resetInject();
//            }
//
//            redirect = false;
//        }
//
//        @Override
//        public void onPageFinished(WebView view, String url)
//        {
//            super.onPageFinished(view, url);
//
//            if (!redirect && !loadingError)
//            {
//                if (loadInterface != null)
//                {
//                    Timber.d("1991 onPageFinished onWebpageLoaded");
//                    loadInterface.onWebpageLoaded(url, view.getTitle());
//                }
//            }
//            else if (!loadingError && loadInterface != null)
//            {
//                Timber.d("1991 onPageFinished onWebpageLoadComplete");
//                loadInterface.onWebpageLoadComplete();
//            }
//
//            redirect = false;
//            loadingError = false;
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url)
//        {
//            redirect = true;
//            Timber.d("1991 shouldOverrideUrlLoading url " + url);
//            return externalClient.shouldOverrideUrlLoading(view, url)
//                    || internalClient.shouldOverrideUrlLoading(view, url);
//        }
//
//        @Override
//        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
//        {
//            Timber.d("1991 onReceivedError " + error.getDescription() );
//            loadingError = true;
//            if (externalClient != null)
//                externalClient.onReceivedError(view, request, error);
//        }
//
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
//        {
//            redirect = true;
//            Timber.d("1991 shouldOverrideUrlLoading request " + request.getUrl());
//            return externalClient.shouldOverrideUrlLoading(view, request)
//                    || internalClient.shouldOverrideUrlLoading(view, request);
//        }
//    }
//}
