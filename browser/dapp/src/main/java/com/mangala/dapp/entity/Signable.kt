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

package com.mangala.dapp.entity

/**
 * Interface for Singable data, for stuff like TBSData (to-be-signed-data), with the view that
 * EthereumMessage, EthereumTypedMessage, EthereumTransaction, X.509 message (attestations)
 * etc eventually use from this
 */
interface Signable {
    val message: String
    val callbackId: Long
    val prehash: ByteArray
    val origin: String
    val userMessage: CharSequence
    val messageType: SignMessageType
}
