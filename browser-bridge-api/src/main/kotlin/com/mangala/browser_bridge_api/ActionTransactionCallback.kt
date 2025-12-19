/*
 * Copyright 2023-2024 Mangala Wallet
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
 * This file uses patterns and conventions from eos-jvm
 * (https://github.com/memtrip/eos-jvm) by memtrip LTD.
 */

package com.mangala.browser_bridge_api

//import com.mangala.wallet.model.token.Token

interface ActionTransactionCallback {

    fun dismissed(callbackId: Long, actionCompleted: Boolean)
    fun notifyConfirm(mode: String?)

    //    fun buttonClick(callbackId: Long, baseToken: Token?) //for message only actionsheet
    //for message only actionsheet
    fun notifyWalletConnectApproval(chainId: Long)    // used by WalletConnectRequest

    // used by WalletConnectRequest
    fun denyWalletConnect()
    fun openChainSelection()

    fun transactionSuccess(
        callbackId: Long,
        value: String,
        recipient: String,
        payload: String,
        nonce: Long,
        isLegacyTransaction: Boolean,
        hashData: String
    )

    fun transactionError(callbackId: Long, error: Throwable?)
    fun onSignMessageSuccessful(callbackId: Long, signHex: String)

    fun onSwitchChainSuccessful(callbackId: Long, newChainId: Long)
}
