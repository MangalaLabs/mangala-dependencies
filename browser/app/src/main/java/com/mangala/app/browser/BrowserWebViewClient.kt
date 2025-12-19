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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.net.http.SslError.SSL_UNTRUSTED
import android.os.Build
import android.webkit.HttpAuthHandler
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.net.toUri
import com.alphawallet.app.web3.JsInjectorClient
import com.mangala.app.accessibility.AccessibilityManager
import com.mangala.app.browser.certificates.rootstore.CertificateValidationState
import com.mangala.app.browser.certificates.rootstore.TrustedCertificateStore
import com.mangala.app.browser.cookies.CookieManagerProvider
import com.mangala.app.browser.cookies.ThirdPartyCookieManager
import com.mangala.app.browser.httpauth.WebViewHttpAuthStore
import com.mangala.app.browser.logindetection.DOMLoginDetector
import com.mangala.app.browser.logindetection.WebNavigationEvent
import com.mangala.app.browser.model.BasicAuthenticationRequest
import com.mangala.app.browser.navigation.safeCopyBackForwardList
import com.mangala.app.browser.print.PrintInjector
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.global.exception.UncaughtExceptionRepository
import com.mangala.app.global.exception.UncaughtExceptionSource.ON_HTTP_AUTH_REQUEST
import com.mangala.app.global.exception.UncaughtExceptionSource.ON_PAGE_FINISHED
import com.mangala.app.global.exception.UncaughtExceptionSource.ON_PAGE_STARTED
import com.mangala.app.global.exception.UncaughtExceptionSource.SHOULD_INTERCEPT_REQUEST
import com.mangala.app.global.exception.UncaughtExceptionSource.SHOULD_OVERRIDE_REQUEST
import com.mangala.app.statistics.store.OfflinePixelCountDataStore
import com.mangala.autofill.BrowserAutofill
import com.mangala.autofill.InternalTestUserChecker
import com.mangala.privacy.config.api.AmpLinks
import com.mangala.privacy.config.api.Gpc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URI

class BrowserWebViewClient(
    private val context: Context,
    private val webViewHttpAuthStore: WebViewHttpAuthStore,
    private val trustedCertificateStore: TrustedCertificateStore,
    private val requestRewriter: RequestRewriter,
    private val specialUrlDetector: SpecialUrlDetector,
    private val requestInterceptor: RequestInterceptor,
    private val offlinePixelCountDataStore: OfflinePixelCountDataStore,
    private val uncaughtExceptionRepository: UncaughtExceptionRepository,
    private val cookieManagerProvider: CookieManagerProvider,
    private val loginDetector: DOMLoginDetector,
    private val dosDetector: DosDetector,
    private val gpc: Gpc,
    private val thirdPartyCookieManager: ThirdPartyCookieManager,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider,
    private val browserAutofill: BrowserAutofill,
    private val accessibilityManager: AccessibilityManager,
    private val ampLinks: AmpLinks,
    private val printInjector: PrintInjector,
    private val internalTestUserChecker: InternalTestUserChecker
) : WebViewClient() {

    var webViewClientListener: WebViewClientListener? = null
    private var lastPageStarted: String? = null

    // private val context: Context? = null

    /**
     * This is the new method of url overriding available from API 24 onwards
     */
    // @RequiresApi(Build.VERSION_CODES.N)
    // override fun shouldOverrideUrlLoading(
    //     view: WebView,
    //     request: WebResourceRequest
    // ): Boolean {
    //     val url = request.url
    //     return shouldOverride(view, url, request.isForMainFrame)
    // }
    //
    // /**
    //  * * This is the old, deprecated method of url overriding available until API 23
    //  */
    // @Suppress("OverridingDeprecatedMember")
    // override fun shouldOverrideUrlLoading(
    //     view: WebView,
    //     urlString: String
    // ): Boolean {
    //     val url = Uri.parse(urlString)
    //     return shouldOverride(view, url, isForMainFrame = true)
    // }

    // Handling of trusted apps
    private fun handleTrustedApps2(url: String): Boolean {
        // get list
        val strArray: Array<String> = context!!.getResources().getStringArray(com.alphawallet.app.R.array.TrustedApps)
        for (item in strArray) {
            val split = item.split(",").toTypedArray()
            if (url.startsWith(split[1])) {
                intentTryApp(split[0], url)
                return true
            }
        }
        return false
    }

    /**
     * API-agnostic implementation of deciding whether to override url or not
     */
    private fun shouldOverride(
        webView: WebView,
        url: Uri,
        isForMainFrame: Boolean
    ): Boolean {

        Timber.v("shouldOverride $url")
        try {
            val strArray: Array<String> = context!!.getResources().getStringArray(com.alphawallet.app.R.array.TrustedApps)
            for (item in strArray) {
                val split = item.split(",").toTypedArray()
                if (url.toString().startsWith(split[1])) {
                    intentTryApp(split[0], url.toString())
                    return true
                }
            }

            if (isForMainFrame && dosDetector.isUrlGeneratingDos(url)) {
                webView.loadUrl("about:blank")
                webViewClientListener?.dosAttackDetected()
                return false
            }

            return when (val urlType = specialUrlDetector.determineType(initiatingUrl = webView.originalUrl, uri = url)) {
                is SpecialUrlDetector.UrlType.Email -> {
                    webViewClientListener?.sendEmailRequested(urlType.emailAddress)
                    true
                }
                is SpecialUrlDetector.UrlType.Telephone -> {
                    webViewClientListener?.dialTelephoneNumberRequested(urlType.telephoneNumber)
                    true
                }
                is SpecialUrlDetector.UrlType.Sms -> {
                    webViewClientListener?.sendSmsRequested(urlType.telephoneNumber)
                    true
                }
                is SpecialUrlDetector.UrlType.AppLink -> {
                    Timber.i("Found app link for ${urlType.uriString}")
                    webViewClientListener?.let { listener ->
                        return listener.handleAppLink(urlType, isForMainFrame)
                    }
                    false
                }
                is SpecialUrlDetector.UrlType.NonHttpAppLink -> {
                    Timber.i("Found non-http app link for ${urlType.uriString}")
                    webViewClientListener?.let { listener ->
                        return listener.handleNonHttpAppLink(urlType)
                    }
                    true
                }
                is SpecialUrlDetector.UrlType.Unknown -> {
                    Timber.w("Unable to process link type for ${urlType.uriString}")
                    webView.originalUrl?.let {
                        webView.loadUrl(it)
                    }
                    false
                }
                is SpecialUrlDetector.UrlType.SearchQuery -> false
                is SpecialUrlDetector.UrlType.Web -> {
                    if (requestRewriter.shouldRewriteRequest(url)) {
                        val newUri = requestRewriter.rewriteRequestWithCustomQueryParams(url)
                        webView.loadUrl(newUri.toString())
                        return true
                    }
                    if (isForMainFrame) {
                        webViewClientListener?.willOverrideUrl(url.toString())
                    }
                    false
                }
                is SpecialUrlDetector.UrlType.ExtractedAmpLink -> {
                    if (isForMainFrame) {
                        webViewClientListener?.let { listener ->
                            listener.startProcessingTrackingLink()
                            Timber.d("AMP link detection: Loading extracted URL: ${urlType.extractedUrl}")
                            loadUrl(listener, webView, urlType.extractedUrl)
                            return true
                        }
                    }
                    false
                }
                is SpecialUrlDetector.UrlType.CloakedAmpLink -> {
                    val lastAmpLinkInfo = ampLinks.lastAmpLinkInfo
                    if (isForMainFrame && (lastAmpLinkInfo == null || lastPageStarted != lastAmpLinkInfo.destinationUrl)) {
                        webViewClientListener?.let { listener ->
                            listener.handleCloakedAmpLink(urlType.ampUrl)
                            return true
                        }
                    }
                    false
                }
                is SpecialUrlDetector.UrlType.TrackingParameterLink -> {
                    if (isForMainFrame) {
                        webViewClientListener?.let { listener ->
                            listener.startProcessingTrackingLink()
                            Timber.d("Loading parameter cleaned URL: ${urlType.cleanedUrl}")

                            return when (
                                val parameterStrippedType =
                                    specialUrlDetector.processUrl(initiatingUrl = webView.originalUrl, uriString = urlType.cleanedUrl)
                            ) {
                                is SpecialUrlDetector.UrlType.AppLink -> {
                                    loadUrl(listener, webView, urlType.cleanedUrl)
                                    listener.handleAppLink(parameterStrippedType, isForMainFrame)
                                }
                                is SpecialUrlDetector.UrlType.ExtractedAmpLink -> {
                                    Timber.d("AMP link detection: Loading extracted URL: ${parameterStrippedType.extractedUrl}")
                                    loadUrl(listener, webView, parameterStrippedType.extractedUrl)
                                    true
                                }
                                else -> {
                                    loadUrl(listener, webView, urlType.cleanedUrl)
                                    true
                                }
                            }
                        }
                    }
                    false
                }
            }
        } catch (e: Throwable) {
            appCoroutineScope.launch(dispatcherProvider.default()) {
                uncaughtExceptionRepository.recordUncaughtException(e, SHOULD_OVERRIDE_REQUEST)
                throw e
            }
            return false
        }
    }

    private fun loadUrl(
        listener: WebViewClientListener,
        webView: WebView,
        url: String
    ) {
        if (listener.linkOpenedInNewTab()) {
            webView.post {
                webView.loadUrl(url)
            }
        } else {
            webView.loadUrl(url)
        }
    }

    @UiThread
    override fun onPageStarted(
        webView: WebView,
        url: String?,
        favicon: Bitmap?
    ) {
        try {
            Timber.v("1991 onPageStarted webViewUrl: ${webView.url} URL: $url")
            url?.let {
                appCoroutineScope.launch(dispatcherProvider.default()) {
                    thirdPartyCookieManager.processUriForThirdPartyCookies(webView, url.toUri())
                }
            }
            val navigationList = webView.safeCopyBackForwardList() ?: return
            webViewClientListener?.navigationStateChanged(WebViewNavigationState(navigationList))
            if (url != null && url == lastPageStarted) {
                webViewClientListener?.pageRefreshed(url)
            }
            lastPageStarted = url
            browserAutofill.configureAutofillForCurrentPage(webView, url)
            injectGpcToDom(webView, url)
            loginDetector.onEvent(WebNavigationEvent.OnPageStarted(webView))
        } catch (e: Throwable) {
            appCoroutineScope.launch(dispatcherProvider.default()) {
                uncaughtExceptionRepository.recordUncaughtException(e, ON_PAGE_STARTED)
                throw e
            }
        }
    }

    @UiThread
    override fun onPageFinished(
        webView: WebView,
        url: String?
    ) {
        try {
            accessibilityManager.onPageFinished(webView, url)
            url?.let {
                // We call this for any url but it will only be processed for an internal tester verification url
                internalTestUserChecker.verifyVerificationCompleted(it)
            }
            Timber.v("onPageFinished webViewUrl: ${webView.url} URL: $url")
            val navigationList = webView.safeCopyBackForwardList() ?: return
            webViewClientListener?.run {
                navigationStateChanged(WebViewNavigationState(navigationList))
                url?.let { prefetchFavicon(url) }
            }
            flushCookies()
            printInjector.injectPrint(webView)
        } catch (e: Throwable) {
            appCoroutineScope.launch(dispatcherProvider.default()) {
                uncaughtExceptionRepository.recordUncaughtException(e, ON_PAGE_FINISHED)
                throw e
            }
        }
    }

    private fun injectGpcToDom(
        webView: WebView,
        url: String?
    ) {
        url?.let {
            if (gpc.canGpcBeUsedByUrl(url)) {
                webView.evaluateJavascript("javascript:${gpc.getGpcJs()}", null)
            }
        }
    }

    private fun flushCookies() {
        appCoroutineScope.launch(dispatcherProvider.io()) {
            cookieManagerProvider.get().flush()
        }
    }

    // @WorkerThread
    // override fun shouldInterceptRequest(
    //     webView: WebView,
    //     request: WebResourceRequest
    // ): WebResourceResponse? {
    //     return runBlocking {
    //         try {
    //             val documentUrl = withContext(Dispatchers.Main) { webView.url }
    //             withContext(Dispatchers.Main) {
    //                 loginDetector.onEvent(WebNavigationEvent.ShouldInterceptRequest(webView, request))
    //             }
    //             Timber.v("Intercepting resource ${request.url} type:${request.method} on page $documentUrl")
    //             requestInterceptor.shouldIntercept(request, webView, documentUrl, webViewClientListener)
    //         } catch (e: Throwable) {
    //             uncaughtExceptionRepository.recordUncaughtException(e, SHOULD_INTERCEPT_REQUEST)
    //             throw e
    //         }
    //     }
    // }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRenderProcessGone(
        view: WebView?,
        detail: RenderProcessGoneDetail?
    ): Boolean {
        Timber.w("onRenderProcessGone. Did it crash? ${detail?.didCrash()}")
        if (detail?.didCrash() == true) {
            offlinePixelCountDataStore.webRendererGoneCrashCount += 1
        } else {
            offlinePixelCountDataStore.webRendererGoneKilledCount += 1
        }

        webViewClientListener?.recoverFromRenderProcessGone()
        return true
    }

    @UiThread
    override fun onReceivedHttpAuthRequest(
        view: WebView?,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        try {
            Timber.v("onReceivedHttpAuthRequest ${view?.url} $realm, $host")
            if (handler != null) {
                Timber.v("onReceivedHttpAuthRequest - useHttpAuthUsernamePassword [${handler.useHttpAuthUsernamePassword()}]")
                if (handler.useHttpAuthUsernamePassword()) {
                    val credentials = view?.let {
                        webViewHttpAuthStore.getHttpAuthUsernamePassword(it, host.orEmpty(), realm.orEmpty())
                    }

                    if (credentials != null) {
                        handler.proceed(credentials.username, credentials.password)
                    } else {
                        requestAuthentication(view, handler, host, realm)
                    }
                } else {
                    requestAuthentication(view, handler, host, realm)
                }
            } else {
                super.onReceivedHttpAuthRequest(view, handler, host, realm)
            }
        } catch (e: Throwable) {
            appCoroutineScope.launch(dispatcherProvider.default()) {
                uncaughtExceptionRepository.recordUncaughtException(e, ON_HTTP_AUTH_REQUEST)
                throw e
            }
        }
    }

    override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler,
        error: SslError
    ) {
        var trusted: CertificateValidationState = CertificateValidationState.UntrustedChain
        when (error.primaryError) {
            SSL_UNTRUSTED -> {
                Timber.d("The certificate authority ${error.certificate.issuedBy.dName} is not trusted")
                trusted = trustedCertificateStore.validateSslCertificateChain(error.certificate)
            }
            else -> Timber.d("SSL error ${error.primaryError}")
        }

        Timber.d("The certificate authority validation result is $trusted")
        if (trusted is CertificateValidationState.TrustedChain) handler.proceed() else super.onReceivedSslError(view, handler, error)
    }

    private fun requestAuthentication(
        view: WebView?,
        handler: HttpAuthHandler,
        host: String?,
        realm: String?
    ) {
        webViewClientListener?.let {
            Timber.v("showAuthenticationDialog - $host, $realm")

            val siteURL = if (view?.url != null) "${URI(view.url).scheme}://$host" else host.orEmpty()

            val request = BasicAuthenticationRequest(
                handler = handler,
                host = host.orEmpty(),
                realm = realm.orEmpty(),
                site = siteURL
            )

            it.requiresAuthentication(request)
        }
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        view?.url?.let {
            // We call this for any url but it will only be processed for an internal tester verification url
            internalTestUserChecker.verifyVerificationErrorReceived(it)
        }
    }

    // DAPP
    private val jsInjectorClient: JsInjectorClient = JsInjectorClient(context)

    fun getJsInjectorClient(): JsInjectorClient {
        return jsInjectorClient
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest
    ): Boolean {
        val url = request.url
        return shouldOverride(view, url, request.isForMainFrame)
    }

    /**
     * * This is the old, deprecated method of url overriding available until API 23
     */
    @Suppress("OverridingDeprecatedMember")
    override fun shouldOverrideUrlLoading(
        view: WebView,
        urlString: String
    ): Boolean {
        val url = Uri.parse(urlString)
        return shouldOverride(view, url, isForMainFrame = true)
    }

    // override fun shouldOverrideUrlLoading(
    //     view: WebView,
    //     url: String
    // ): Boolean {
    //     return handleTrustedApps(url)
    // }

    // override fun shouldOverrideUrlLoading(
    //     view: WebView,
    //     request: WebResourceRequest
    // ): Boolean {
    //     if (request == null || view == null) {
    //         return false
    //     }
    //     val url = request.url.toString()
    //     return handleTrustedApps(url)
    // }

    @WorkerThread
    override fun shouldInterceptRequest(
        webView: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return runBlocking {
            try {
                val documentUrl = withContext(Dispatchers.Main) { webView.url }
                withContext(Dispatchers.Main) {
                    loginDetector.onEvent(WebNavigationEvent.ShouldInterceptRequest(webView, request))
                }
                Timber.v("Intercepting resource ${request.url} type:${request.method} on page $documentUrl")
                requestInterceptor.shouldIntercept(request, webView, documentUrl, webViewClientListener)
            } catch (e: Throwable) {
                uncaughtExceptionRepository.recordUncaughtException(e, SHOULD_INTERCEPT_REQUEST)
                throw e
            }
        }
    }

    // override fun shouldInterceptRequest(
    //     view: WebView,
    //     request: WebResourceRequest
    // ): WebResourceResponse? {
    //     return if (request == null) {
    //         null
    //     } else super.shouldInterceptRequest(view, request)
    // }

    fun getInitString(view: WebView): String {
        return jsInjectorClient.initJs(view.context)
    }

    fun getProviderString(view: WebView): String {
        return jsInjectorClient.providerJs(view.context)
    }

    // override fun onReceivedSslError(
    //     view: WebView,
    //     handler: SslErrorHandler,
    //     error: SslError
    // ) {
    //     val aDialog = AWalletAlertDialog(context!!)
    //     aDialog.setTitle(com.alphawallet.app.R.string.title_dialog_error)
    //     aDialog.setIcon(AWalletAlertDialog.ERROR)
    //     aDialog.setMessage(com.alphawallet.app.R.string.ssl_cert_invalid)
    //     aDialog.setButtonText(com.alphawallet.app.R.string.dialog_approve)
    //     aDialog.setButtonListener { v: View? ->
    //         handler.proceed()
    //         aDialog.dismiss()
    //     }
    //     aDialog.setSecondaryButtonText(com.alphawallet.app.R.string.action_cancel)
    //     aDialog.setButtonListener { v: View? ->
    //         handler.cancel()
    //         aDialog.dismiss()
    //     }
    //     aDialog.show()
    // }

    // Handling of trusted apps
    private fun handleTrustedApps(url: String): Boolean {
        // get list
        val strArray: Array<String> = context!!.getResources().getStringArray(com.alphawallet.app.R.array.TrustedApps)
        for (item in strArray) {
            val split = item.split(",").toTypedArray()
            if (url.startsWith(split[1])) {
                intentTryApp(split[0], url)
                return true
            }
        }
        return false
    }

    private fun intentTryApp(
        appId: String,
        msg: String
    ) {
        val isAppInstalled = isAppAvailable(appId)
        if (isAppInstalled) {
            val myIntent = Intent(Intent.ACTION_VIEW)
            myIntent.setPackage(appId)
            myIntent.data = Uri.parse(msg)
            myIntent.putExtra(Intent.EXTRA_TEXT, msg)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(myIntent)
        } else {
            Toast.makeText(context, "Required App not Installed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAppAvailable(appName: String): Boolean {
        val pm: PackageManager? = context?.getPackageManager()
        return try {
            pm?.getPackageInfo(appName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: NameNotFoundException) {
            false
        }
    }

    fun resetInject() {}
}
