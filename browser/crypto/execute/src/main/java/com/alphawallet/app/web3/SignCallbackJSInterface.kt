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
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.alphawallet.app.entity.CryptoFunctions
import com.alphawallet.app.entity.tokenscript.TokenscriptFunction
import com.alphawallet.app.util.Hex
import com.alphawallet.app.util.Utils
import com.alphawallet.app.web3.entity.Address
import com.alphawallet.app.web3.entity.WalletAddEthereumChainObject
import com.alphawallet.app.web3.entity.Web3Call
import com.alphawallet.app.web3.entity.Web3Transaction
import com.alphawallet.token.entity.EthereumMessage
import com.alphawallet.token.entity.EthereumTypedMessage
import com.alphawallet.token.entity.SignMessageType
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import timber.log.Timber
import java.math.BigInteger

class SignCallbackJSInterface(
    private val webView: WebView?,
    private val onSignTransactionListener: OnSignTransactionListener,
    private val onSignMessageListener: OnSignMessageListener,
    private val onSignPersonalMessageListener: OnSignPersonalMessageListener,
    private val onSignTypedMessageListener: OnSignTypedMessageListener,
    private val onEthCallListener: OnEthCallListener,
    private val onWalletAddEthereumChainObjectListener: OnWalletAddEthereumChainObjectListener,
    private val onWalletActionListener: OnWalletActionListener
) {
    @JavascriptInterface
    fun signTransaction(
        callbackId: Int,
        recipient: String?,
        value: String?,
        nonce: String?,
        gasLimit: String?,
        gasPrice: String?,
        payload: String?
    ) {
        var value = value
        var gasPrice = gasPrice
        if (value == "undefined" || value == null) value = "0"
        if (gasPrice == null) gasPrice = "0"
        val transaction = Web3Transaction(
            if (TextUtils.isEmpty(recipient)) Address.EMPTY else Address(
                recipient!!
            ),
            null,
            Hex.hexToBigInteger(value),
            Hex.hexToBigInteger(gasPrice, BigInteger.ZERO),
            Hex.hexToBigInteger(gasLimit, BigInteger.ZERO),
            Hex.hexToLong(nonce, -1),
            payload,
            callbackId.toLong()
        )
        webView!!.post { onSignTransactionListener.onSignTransaction(transaction, url) }
    }

    @JavascriptInterface
    fun signMessage(callbackId: Int, data: String?) {
        webView!!.post {
            onSignMessageListener.onSignMessage(
                EthereumMessage(
                    data,
                    url,
                    callbackId.toLong(),
                    SignMessageType.SIGN_MESSAGE
                )
            )
        }
    }

    @JavascriptInterface
    fun signPersonalMessage(callbackId: Int, data: String?) {
        webView!!.post {
            onSignPersonalMessageListener.onSignPersonalMessage(
                EthereumMessage(
                    data,
                    url,
                    callbackId.toLong(),
                    SignMessageType.SIGN_PERSONAL_MESSAGE
                )
            )
        }
    }

    @JavascriptInterface
    fun requestAccounts(callbackId: Long) {
        webView!!.post { onWalletActionListener.onRequestAccounts(callbackId) }
    }

    @JavascriptInterface
    fun signTypedMessage(callbackId: Int, data: String?) {
        webView!!.post {
            try {
                val obj = JSONObject(data)
                val address = obj.getString("from")
                val messageData = obj.getString("data")
                val cryptoFunctions = CryptoFunctions()
                val message = EthereumTypedMessage(
                    messageData,
                    domainName,
                    callbackId.toLong(),
                    cryptoFunctions
                )
                onSignTypedMessageListener.onSignTypedMessage(message)
            } catch (e: Exception) {
                val message = EthereumTypedMessage(null, "", domainName, callbackId.toLong())
                onSignTypedMessageListener.onSignTypedMessage(message)
                Timber.e(e)
            }
        }
    }

    @JavascriptInterface
    fun ethCall(callbackId: Int, recipient: String?) {
        try {
            val json = JSONObject(recipient)
            val defaultBlockParameter: DefaultBlockParameter
            val to = if (json.has("to")) json.getString("to") else TokenscriptFunction.ZERO_ADDRESS
            val payload = if (json.has("data")) json.getString("data") else "0x"
            val value = if (json.has("value")) json.getString("value") else null
            val gasLimit = if (json.has("gas")) json.getString("gas") else null
            defaultBlockParameter =
                DefaultBlockParameterName.LATEST //TODO: Take block param from query if present
            val call = Web3Call(
                Address(to),
                defaultBlockParameter,
                payload,
                value,
                gasLimit,
                callbackId.toLong()
            )
            webView!!.post { onEthCallListener.onEthCall(call) }
        } catch (e: Exception) {
            //
        }
    }

    @JavascriptInterface
    fun walletAddEthereumChain(callbackId: Int, msgParams: String?) {
        //TODO: Implement custom chains from dapp browser: see OnWalletAddEthereumChainObject in class DappBrowserFragment
        //First draft: attempt to match this chain with known chains; switch to known chain if we match
        try {
            val chainObj = Gson().fromJson(msgParams, WalletAddEthereumChainObject::class.java)
            if (!TextUtils.isEmpty(chainObj.chainId)) {
                webView!!.post {
                    onWalletAddEthereumChainObjectListener.onWalletAddEthereumChainObject(
                        callbackId.toLong(),
                        chainObj
                    )
                }
            }
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
        }
    }

    @JavascriptInterface
    fun walletSwitchEthereumChain(callbackId: Int, msgParams: String?) {
        try { //{"chainId":"0x89","chainType":"ETH"}
            val chainObj = Gson().fromJson(msgParams, WalletAddEthereumChainObject::class.java)
            if (!TextUtils.isEmpty(chainObj.chainId)) {
                webView!!.post {
                    onWalletActionListener.onWalletSwitchEthereumChain(
                        callbackId.toLong(),
                        chainObj
                    )
                } // onWalletAddEthereumChainObjectListener.onWalletAddEthereumChainObject(chainObj));
            }
        } catch (e: JsonSyntaxException) {
            Timber.e(e)
        }
    }

    private val url: String
        private get() = if (webView == null) "" else webView.url!!
    private val domainName: String
        private get() = if (webView == null) "" else Utils.getDomainName(webView.url)
}
