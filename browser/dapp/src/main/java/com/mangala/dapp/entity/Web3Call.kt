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


import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import java.math.BigInteger

class Web3Call(
    to: Address,
    blockParam: DefaultBlockParameter,
    payload: String,
    value: String?,
    gasLimit: String?,
    leafPosition: Long
) {
    val to: Address
    val blockParam: DefaultBlockParameter
    val payload: String
    val value: BigInteger?
    val gasLimit: BigInteger?
    val leafPosition: Long

    init {
        this.to = to
        this.blockParam = blockParam
        this.payload = payload
        this.value = value?.toBigInteger()
        this.gasLimit = gasLimit?.toBigInteger()
        this.leafPosition = leafPosition
    }
}
