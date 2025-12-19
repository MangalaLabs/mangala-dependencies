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

package com.alphawallet.app.walletconnect.entity

import com.alphawallet.app.web3.entity.NativeCurrency
import com.alphawallet.app.web3.entity.WalletAddEthereumChainObject
import com.trustwallet.walletconnect.models.WCPeerMeta

data class WCSessionRequest(
    val peerId: String,
    val peerMeta: WCPeerMeta,
    val chainId: String?
)

data class WCApproveSessionResponse(
    val approved: Boolean = true,
    val chainId: Long,
    val accounts: List<String>,
    val peerId: String?,
    val peerMeta: WCPeerMeta?
)

data class WCSessionUpdate(
    val approved: Boolean,
    val chainId: Long?,
    val accounts: List<String>?
)

data class WCEncryptionPayload(
    val data: String,
    val hmac: String,
    val iv: String
)

data class WCSocketMessage(
    val topic: String,
    val type: MessageType,
    val payload: String
)

//data class WCPeerMeta(
//    val name: String,
//    val url: String,
//    val description: String? = null,
//    val icons: List<String> = listOf("")
//)

data class WCSwitchEthChain(
    val chainId: String
)

data class WCAddEthChain(
    val chainId: String,
    val chainName: String,
    val nativeCurrency: NativeCurrency,
    val rpcUrls: List<String>,
    val blockExplorerUrls: List<String>? = null,
    val iconUrls: List<String>? = null
) {
    fun toWalletAddEthereumObject(): WalletAddEthereumChainObject {
        val chainObject: WalletAddEthereumChainObject = WalletAddEthereumChainObject()
        chainObject.nativeCurrency = this.nativeCurrency
        chainObject.chainName = this.chainName
        chainObject.chainId = this.chainId
        chainObject.blockExplorerUrls = this.blockExplorerUrls?.toTypedArray()
        chainObject.rpcUrls = this.rpcUrls.toTypedArray()
        return chainObject
    }
}
