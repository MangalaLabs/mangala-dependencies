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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.core.view.NestedScrollingChild
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import com.alphawallet.app.entity.URLLoadInterface
import com.alphawallet.app.web3.OnEthCallListener
import com.alphawallet.app.web3.OnSignMessageListener
import com.alphawallet.app.web3.OnSignPersonalMessageListener
import com.alphawallet.app.web3.OnSignTransactionListener
import com.alphawallet.app.web3.OnSignTypedMessageListener
import com.alphawallet.app.web3.OnWalletActionListener
import com.alphawallet.app.web3.OnWalletAddEthereumChainObjectListener
import com.alphawallet.app.web3.SignCallbackJSInterface
import com.alphawallet.app.web3.entity.Address
import com.alphawallet.app.web3.entity.WalletAddEthereumChainObject
import com.alphawallet.app.web3.entity.Web3Call
import com.alphawallet.app.web3.entity.Web3Transaction
import com.alphawallet.token.entity.EthereumMessage
import com.alphawallet.token.entity.EthereumTypedMessage
import com.alphawallet.token.entity.Signable
import org.jetbrains.annotations.Contract
import timber.log.Timber

/**
 * WebView subclass which allows the WebView to
 *   - hide the toolbar when placed in a CoordinatorLayout
 *   - add the flag so that users' typing isn't used for personalisation
 *
 * Originally based on https://github.com/takahirom/webview-in-coordinatorlayout for scrolling behaviour
 */
class MangalaWebView : WebView, NestedScrollingChild {
    private var lastClampedTopY: Boolean = true // when created we are always at the top
    private var contentAllowsSwipeToRefresh: Boolean = true
    private var enableSwipeRefreshCallback: ((Boolean) -> Unit)? = null
    private var hasGestureFinished = true
    private var canSwipeToRefresh = true

    private var lastY: Int = 0
    private var lastDeltaY: Int = 0
    private val scrollOffset = IntArray(2)
    private val scrollConsumed = IntArray(2)
    private var nestedOffsetY: Int = 0
    private var nestedScrollHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)

    private var webViewClient: BrowserWebViewClient? = null

    fun setBrowserWebViewClient(webViewClient: BrowserWebViewClient) {
        this.webViewClient = webViewClient
    }

    constructor(context: Context) : this(context, null) {
        // webViewClient = Web3ViewClient(context)
        // init()
    }
    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        isNestedScrollingEnabled = true
        // webViewClient = Web3ViewClient(context)
        // init()
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val inputConnection = super.onCreateInputConnection(outAttrs) ?: return null

        addNoPersonalisedFlag(outAttrs)

        return inputConnection
    }

    private fun addNoPersonalisedFlag(outAttrs: EditorInfo) {
        outAttrs.imeOptions = outAttrs.imeOptions or IME_FLAG_NO_PERSONALIZED_LEARNING
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        var returnValue = false

        val event = MotionEvent.obtain(ev)
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_DOWN) {
            nestedOffsetY = 0
        }
        val eventY = event.y.toInt()
        event.offsetLocation(0f, nestedOffsetY.toFloat())

        when (action) {
            MotionEvent.ACTION_UP -> {
                hasGestureFinished = true
                returnValue = super.onTouchEvent(event)
                stopNestedScroll()
            }
            MotionEvent.ACTION_MOVE -> {
                var deltaY = lastY - eventY

                lastClampedTopY = deltaY <= 0

                if (dispatchNestedPreScroll(0, deltaY, scrollConsumed, scrollOffset)) {
                    deltaY -= scrollConsumed[1]
                    lastY = eventY - scrollOffset[1]
                    event.offsetLocation(0f, (-scrollOffset[1]).toFloat())
                    nestedOffsetY += scrollOffset[1]
                }

                returnValue = super.onTouchEvent(event)

                if (dispatchNestedScroll(0, scrollOffset[1], 0, deltaY, scrollOffset)) {
                    event.offsetLocation(0f, scrollOffset[1].toFloat())
                    nestedOffsetY += scrollOffset[1]
                    lastY -= scrollOffset[1]
                }

                lastDeltaY = deltaY
            }

            MotionEvent.ACTION_DOWN -> {
                hasGestureFinished = false
                // disable swipeRefresh until we can be sure it should be enabled
                enableSwipeRefresh(false)

                returnValue = super.onTouchEvent(event)
                lastY = eventY
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }

            else -> {
                returnValue = super.onTouchEvent(event)
                stopNestedScroll()
            }
        }

        return returnValue
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        nestedScrollHelper.isNestedScrollingEnabled = enabled
    }

    override fun stopNestedScroll() {
        nestedScrollHelper.stopNestedScroll()
    }

    override fun isNestedScrollingEnabled(): Boolean = nestedScrollHelper.isNestedScrollingEnabled

    override fun startNestedScroll(axes: Int): Boolean = nestedScrollHelper.startNestedScroll(axes)

    override fun hasNestedScrollingParent(): Boolean = nestedScrollHelper.hasNestedScrollingParent()

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean =
        nestedScrollHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean =
        nestedScrollHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean =
        nestedScrollHelper.dispatchNestedFling(velocityX, velocityY, consumed)

    override fun dispatchNestedPreFling(
        velocityX: Float,
        velocityY: Float
    ): Boolean =
        nestedScrollHelper.dispatchNestedPreFling(velocityX, velocityY)

    override fun onOverScrolled(
        scrollX: Int,
        scrollY: Int,
        clampedX: Boolean,
        clampedY: Boolean
    ) {
        // taking into account lastDeltaY since we are only interested whether we clamped at the top
        lastClampedTopY = clampedY && lastDeltaY <= 0

        if (!lastClampedTopY) {
            canSwipeToRefresh = false // disable because user scrolled down so we need a new gesture
        }

        if (lastClampedTopY && hasGestureFinished) {
            canSwipeToRefresh = true // only enable if at the top and gestured finished
        }

        enableSwipeRefresh(canSwipeToRefresh && clampedY && scrollY == 0 && (lastDeltaY <= 0 || nestedOffsetY == 0))
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
    }

    fun setEnableSwipeRefreshCallback(callback: (Boolean) -> Unit) {
        enableSwipeRefreshCallback = callback
    }

    private fun enableSwipeRefresh(enable: Boolean) {
        enableSwipeRefreshCallback?.invoke(enable && contentAllowsSwipeToRefresh)
    }

    private fun setContentAllowsSwipeToRefresh(allowed: Boolean) {
        contentAllowsSwipeToRefresh = allowed
        if (!allowed) {
            enableSwipeRefresh(false)
        }
    }

    // DAPP
    private val JS_PROTOCOL_CANCELLED: String = "cancelled"
    private val JS_PROTOCOL_ON_SUCCESSFUL = "AlphaWallet.executeCallback(%1\$s, null, \"%2\$s\")"
    private val JS_PROTOCOL_EXPR_ON_SUCCESSFUL = "AlphaWallet.executeCallback(%1\$s, null, %2\$s)"
    private val JS_PROTOCOL_ON_FAILURE = "AlphaWallet.executeCallback(%1\$s, \"%2\$s\", null)"

    // fun setBrowserWebViewClient(webViewClient: BrowserWebViewClient?){
    //     this.webViewClient = webViewClient
    // }

    // @Inject
    // lateinit var webViewClient: BrowserWebViewClient

    private var onSignTransactionListener: OnSignTransactionListener? = null
    val innerOnSignTransactionListener = object: OnSignTransactionListener {
        override fun onSignTransaction(transaction: Web3Transaction?, url: String?) {
            onSignTransactionListener?.onSignTransaction(transaction, url)
        }
    }

    private var onSignMessageListener: OnSignMessageListener? = null
    val innerOnSignMessageListener = object: OnSignMessageListener {
        override fun onSignMessage(message: EthereumMessage?) {
            onSignMessageListener?.onSignMessage(message)
        }
    }

    private var onSignPersonalMessageListener: OnSignPersonalMessageListener? = null
    val innerOnSignPersonalMessageListener =
        object: OnSignPersonalMessageListener {
            override fun onSignPersonalMessage(message: EthereumMessage?) {
                onSignPersonalMessageListener?.onSignPersonalMessage(message)
            }
        }

    private var onSignTypedMessageListener: OnSignTypedMessageListener? = null
    val innerOnSignTypedMessageListener = object: OnSignTypedMessageListener {
        override fun onSignTypedMessage(message: EthereumTypedMessage?) {
            onSignTypedMessageListener?.onSignTypedMessage(message)
        }
    }

    private var onEthCallListener: OnEthCallListener? = null
    val innerOnEthCallListener = object: OnEthCallListener {
        override fun onEthCall(txdata: Web3Call?) {
            onEthCallListener?.onEthCall(txdata)
        }
    }

    private var onWalletAddEthereumChainObjectListener: OnWalletAddEthereumChainObjectListener? = null
    val innerAddChainListener = object: OnWalletAddEthereumChainObjectListener {
        override fun onWalletAddEthereumChainObject(
            callbackId: Long,
            chainObject: WalletAddEthereumChainObject?
        ) {
            onWalletAddEthereumChainObjectListener?.onWalletAddEthereumChainObject(
                callbackId,
                chainObject
            )
        }
    }
    private var onWalletActionListener: OnWalletActionListener? = null
    val innerOnWalletActionListener: OnWalletActionListener = object : OnWalletActionListener {
        override fun onRequestAccounts(callbackId: Long) {
            onWalletActionListener!!.onRequestAccounts(callbackId)
        }

        override fun onWalletSwitchEthereumChain(
            callbackId: Long,
            chainObj: WalletAddEthereumChainObject?
        ) {
            onWalletActionListener?.onWalletSwitchEthereumChain(callbackId, chainObj)
        }
    }
    internal var loadInterface: URLLoadInterface? = null

    override fun setWebChromeClient(client: WebChromeClient?) {
        super.setWebChromeClient(client)
    }

    override fun setWebViewClient(client: WebViewClient) {
        webViewClient?.let {
            Timber.d("1991 setWebViewClient ")
            super.setWebViewClient(WrapWebViewClient(webViewClient!!, client))
        }
    }

    override fun loadUrl(
        url: String,
        additionalHttpHeaders: Map<String, String>
    ) {
        super.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadUrl(url: String) {
        loadUrl(url, getWeb3Headers())
    }

    /* Required for CORS requests */
    @Contract(" -> new") private fun getWeb3Headers(): Map<String, String> {
        // headers
        return object : HashMap<String, String>() {
            init {
                put("Connection", "close")
                put("Content-Type", "text/plain")
                put("Access-Control-Allow-Origin", "*")
                put("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS")
                put("Access-Control-Max-Age", "600")
                put("Access-Control-Allow-Credentials", "true")
                put("Access-Control-Allow-Headers", "accept, authorization, Content-Type")
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled") fun init() {
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.domStorageEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.setUserAgentString(
            settings.userAgentString +
                "Mangala(Platform=Android&AppVersion=" + "1.0.0" + ")"
        )
        setWebContentsDebuggingEnabled(true) // so devs can debug their scripts/pages
        addJavascriptInterface(
            SignCallbackJSInterface(
                this,
                innerOnSignTransactionListener,
                innerOnSignMessageListener,
                innerOnSignPersonalMessageListener,
                innerOnSignTypedMessageListener,
                innerOnEthCallListener,
                innerAddChainListener,
                innerOnWalletActionListener
            ),
            "alpha"
        )

//        Removing this block for now.
//        TODO: Figure out if we should support dark mode for external websites
//        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK))
//        {
//            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
//            {
//                case Configuration.UI_MODE_NIGHT_YES:
//                    WebSettingsCompat.setForceDark(getSettings(), FORCE_DARK_ON);
//                    break;
//                case Configuration.UI_MODE_NIGHT_NO:
//                case Configuration.UI_MODE_NIGHT_UNDEFINED:
//                    WebSettingsCompat.setForceDark(getSettings(), FORCE_DARK_OFF);
//                    break;
//            }
//        }
    }

    fun getWalletAddress(): Address? {
        return webViewClient?.getJsInjectorClient()?.getWalletAddress()
    }

    fun setWalletAddress(address: Address) {
        Timber.d("1991 setWalletAddress1 " + address.toString())
        webViewClient?.getJsInjectorClient()?.setWalletAddress(address)
    }

    fun getChainId(): Long {
        return webViewClient?.getJsInjectorClient()?.chainId ?: 0L
    }

    fun setChainId(chainId: Long) {
        webViewClient?.getJsInjectorClient()?.chainId = chainId
    }

    fun setWebLoadCallback(iFace: URLLoadInterface) {
        loadInterface = iFace
    }

    fun setRpcUrl(rpcUrl: String) {
        webViewClient?.getJsInjectorClient()?.rpcUrl = rpcUrl
    }

    fun setOnSignTransactionListener(onSignTransactionListener: OnSignTransactionListener?) {
        this.onSignTransactionListener = onSignTransactionListener
    }

    fun setOnSignMessageListener(onSignMessageListener: OnSignMessageListener?) {
        this.onSignMessageListener = onSignMessageListener
    }

    fun setOnSignPersonalMessageListener(onSignPersonalMessageListener: OnSignPersonalMessageListener?) {
        this.onSignPersonalMessageListener = onSignPersonalMessageListener
    }

    fun setOnSignTypedMessageListener(onSignTypedMessageListener: OnSignTypedMessageListener?) {
        this.onSignTypedMessageListener = onSignTypedMessageListener
    }

    fun setOnEthCallListener(onEthCallListener: OnEthCallListener?) {
        this.onEthCallListener = onEthCallListener
    }

    fun setOnWalletAddEthereumChainObjectListener(onWalletAddEthereumChainObjectListener: OnWalletAddEthereumChainObjectListener?) {
        this.onWalletAddEthereumChainObjectListener = onWalletAddEthereumChainObjectListener
    }

    fun setOnWalletActionListener(onWalletActionListener: OnWalletActionListener?) {
        this.onWalletActionListener = onWalletActionListener
    }

    fun onSignTransactionSuccessful(
        callbackId: Long,
        signHex: String
    ) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex)
    }

    fun onSignMessageSuccessful(
        callbackId: Long,
        signHex: String
    ) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex)
    }

    fun onCallFunctionSuccessful(
        callbackId: Long,
        result: String
    ) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, result)
    }

    fun onCallFunctionError(
        callbackId: Long,
        error: String
    ) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, error)
    }

    fun onSignCancel(callbackId: Long) {
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, JS_PROTOCOL_CANCELLED)
    }

    private fun callbackToJS(
        callbackId: Long,
        function: String,
        param: String
    ) {
        val callback = String.format(function, callbackId, param)
        post {
            evaluateJavascript(
                callback
            ) { value: String? -> Timber.tag("WEB_VIEW").d(value) }
        }
    }

    fun onWalletActionSuccessful(
        callbackId: Long,
        expression: String?
    ) {
        Timber.d("1991 onWalletActionSuccessful $expression")
        val callback = String.format(JS_PROTOCOL_EXPR_ON_SUCCESSFUL, callbackId, expression)
        post {
            evaluateJavascript(
                callback
            ) { message: String? -> Timber.d(message) }
        }
    }

    fun resetView() {
        webViewClient!!.resetInject()
    }

    inner class WrapWebViewClient(
        private val internalClient: BrowserWebViewClient,
        private val externalClient: WebViewClient
    ) : WebViewClient() {
        private var loadingError = false
        private var redirect = false

        @UiThread
        override fun onPageStarted(
            view: WebView,
            url: String?,
            favicon: Bitmap?
        ) {
            super.onPageStarted(view, url, favicon)
            clearCache(true)
            if (!redirect) {
                view.evaluateJavascript(internalClient.getProviderString(view), null)
                view.evaluateJavascript(internalClient.getInitString(view), null)
                internalClient.resetInject()
            }
            redirect = false
        }

        @UiThread
        override fun onPageFinished(
            view: WebView,
            url: String?
        ) {
            super.onPageFinished(view, url)
            if (!redirect && !loadingError) {
                if (loadInterface != null) {
                    loadInterface?.onWebpageLoaded(url, view.title)
                }
            } else if (!loadingError && loadInterface != null) {
                loadInterface?.onWebpageLoadComplete()
            }
            redirect = false
            loadingError = false
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String
        ): Boolean {
            redirect = true
            return (
                externalClient?.shouldOverrideUrlLoading(view, url) ?: false ||
                    internalClient.shouldOverrideUrlLoading(view, url)
                )

            // return false
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            loadingError = true
            externalClient?.onReceivedError(view, request, error)
        }

        @RequiresApi(api = VERSION_CODES.N) override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            redirect = true
            return (
                externalClient?.shouldOverrideUrlLoading(view, request) ?: false ||
                    internalClient.shouldOverrideUrlLoading(view, request)
                )
            // return false
        }
    }

    companion object {

        /*
         * Taken from EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
         * We can't use that value directly as it was only added on Oreo, but we can apply the value anyway.
         */
        private const val IME_FLAG_NO_PERSONALIZED_LEARNING = 0x1000000
    }
}
