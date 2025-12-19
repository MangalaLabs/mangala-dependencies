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

import android.content.Context
import android.text.TextUtils
import com.alphawallet.app.R
import com.alphawallet.app.repository.EthereumNetworkRepository
import com.alphawallet.app.util.Utils
import com.alphawallet.app.web3.entity.Address
import com.alphawallet.ethereum.EthereumNetworkBase
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.web3j.crypto.Keys
import timber.log.Timber
import java.math.BigInteger
import java.util.Locale
import java.util.regex.Pattern

class JsInjectorClient(private val context: Context) {
    private val httpClient: OkHttpClient
    var chainId = EthereumNetworkBase.BINANCE_MAIN_ID
    private var walletAddress: Address? = null

    //Note: this default RPC is overriden before injection
    var rpcUrl = EthereumNetworkRepository.getDefaultNodeURL(EthereumNetworkBase.BINANCE_MAIN_ID)

    init {
        httpClient = createHttpClient()
    }

    fun getWalletAddress(): Address? {
        return walletAddress
    }

    fun setWalletAddress(address: Address?) {
        Timber.d("1991 setWalletAddress")
        walletAddress = address
    }

    fun initJs(context: Context): String {
        return loadInitJs(context)
    }

    fun providerJs(context: Context?): String {
        Timber.d("1991 dapp providerJs")
        return Utils.loadFile(context, R.raw.alphawallet_min)
    }

    fun injectWeb3TokenInit(
        ctx: Context?,
        view: String,
        tokenContent: String?,
        tokenId: BigInteger
    ): String {
        Timber.d("dapp injectWeb3TokenInit")
        var initSrc = Utils.loadFile(ctx, R.raw.init_token)
        //put the view in here
        val tokenIdWrapperName = "token-card-" + tokenId.toString(10)
        initSrc = String.format(
            initSrc,
            tokenContent,
            walletAddress,
            EthereumNetworkRepository.getDefaultNodeURL(chainId),
            chainId,
            tokenIdWrapperName
        )
        //now insert this source into the view
        Timber.d("1991 initSrc $initSrc")
        Timber.d("1991 walletAddress " + walletAddress.toString() + " chainId " + chainId + " url " + rpcUrl)
        // note that the <div> is not closed because it is closed in injectStyleAndWrap().
        val wrapper = "<div id=\"token-card-" + tokenId.toString(10) + "\" class=\"token-card\">"
        initSrc = "<script>\n$initSrc</script>\n$wrapper"
        return injectJS(view, initSrc)
    }

    fun injectJSAtEnd(view: String, newCode: String): String {
        val position = getEndInjectionPosition(view)
        if (position >= 0) {
            val beforeTag = view.substring(0, position)
            val afterTab = view.substring(position)
            return beforeTag + newCode + afterTab
        }
        return view
    }

    fun injectJS(html: String, js: String): String {
        if (TextUtils.isEmpty(html)) {
            return html
        }
        val position = getInjectionPosition(html)
        if (position >= 0) {
            val beforeTag = html.substring(0, position)
            val afterTab = html.substring(position)
            return beforeTag + js + afterTab
        }
        return html
    }

    private fun getInjectionPosition(body: String): Int {
        var body = body
        body = body.lowercase(Locale.getDefault())
        val ieDetectTagIndex = body.indexOf("<!--[if")
        val scriptTagIndex = body.indexOf("<script")
        var index: Int
        index = if (ieDetectTagIndex < 0) {
            scriptTagIndex
        } else {
            Math.min(scriptTagIndex, ieDetectTagIndex)
        }
        if (index < 0) {
            index = body.indexOf("</head")
        }
        if (index < 0) {
            index = 0 //just wrap whole view
        }
        return index
    }

    private fun getEndInjectionPosition(body: String): Int {
        var body = body
        body = body.lowercase(Locale.getDefault())
        val firstIndex = body.indexOf("<script")
        val nextIndex = body.indexOf("web3", firstIndex)
        return body.indexOf("</script", nextIndex)
    }

    private fun buildRequest(url: String, headers: Map<String, String>): Request? {
        val httpUrl = url.toHttpUrlOrNull() ?: return null
        val requestBuilder: Request.Builder = Request.Builder()
            .get()
            .url(httpUrl)
        val keys = headers.keys
        for (key in keys) {
            requestBuilder.addHeader(key, headers[key] ?: "")
        }
        return requestBuilder.build()
    }

    private fun loadInitJs(context: Context): String {
//        Timber.d("1991 dapp loadInitJs walletAddress " + walletAddress.toString());
        val initSrc = Utils.loadFile(context, R.raw.init)
        val address =
            if (walletAddress == null) Address.EMPTY.toString() else Keys.toChecksumAddress(
                walletAddress.toString()
            )
        //        Timber.d("1991 loadInitJs address " + address);
        Timber.d("1991 loadInitJs address $address chainId $chainId rpcUrl $rpcUrl")
        return String.format(initSrc, address, rpcUrl, chainId)
    }

    fun injectStyleAndWrap(view: String, style: String?): String {
        var style = style
        if (style == null) style = ""
        //String injectHeader = "<head><meta name=\"viewport\" content=\"width=device-width, user-scalable=false\" /></head>";
        val injectHeader =
            "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, shrink-to-fit=no\" />" //iOS uses these header settings
        style = """
               <style type="text/css">
               $style.token-card {
               padding: 0pt;
               margin: 0pt;
               }</style></head>
               """.trimIndent() + "<body>\n"
        // the opening of the following </div> is in injectWeb3TokenInit();
        return "$injectHeader$style$view</div></body>"
    }

    private fun getMimeType(contentType: String): String {
        val regexResult = Pattern.compile("^.*(?=;)").matcher(contentType)
        return if (regexResult.find()) {
            regexResult.group()
        } else DEFAULT_MIME_TYPE
    }

    private fun getCharset(contentType: String): String {
        val regexResult = Pattern.compile("charset=([a-zA-Z0-9-]+)").matcher(contentType)
        if (regexResult.find()) {
            if (regexResult.groupCount() >= 2) {
                return regexResult.group(1)
            }
        }
        return DEFAULT_CHARSET
    }

    private fun getContentTypeHeader(response: Response): String? {
        val headers = response.headers
        var contentType: String?
        contentType = if (TextUtils.isEmpty(headers["Content-Type"])) {
            if (TextUtils.isEmpty(headers["content-Type"])) {
                "text/data; charset=utf-8"
            } else {
                headers["content-Type"]
            }
        } else {
            headers["Content-Type"]
        }
        if (contentType != null) {
            contentType = contentType.trim { it <= ' ' }
        }
        return contentType
    }

    private fun createHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(WebViewCookieJar())
            .build()
    }

    companion object {
        private const val DEFAULT_CHARSET = "utf-8"
        private const val DEFAULT_MIME_TYPE = "text/html"
        private const val JS_TAG_TEMPLATE = "<script type=\"text/javascript\">%1\$s%2\$s</script>"
    }
}
