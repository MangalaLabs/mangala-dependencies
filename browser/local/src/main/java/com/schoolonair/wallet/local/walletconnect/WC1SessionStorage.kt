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

package com.schoolonair.wallet.local.walletconnect


class WC1SessionStorage(private val dao: WC1SessionDao) {

    fun getSessions(accountId: String): List<WalletConnectSession> {
        return dao.getByAccountId(accountId)
    }

    fun save(session: WalletConnectSession) {
        dao.insert(session)
    }

    fun deleteSessionsByPeerId(peerId: String) {
        dao.deleteByPeerId(peerId)
    }

    fun deleteSessionsExcept(accountIds: List<String> = listOf()) {
        dao.deleteAllExcept(accountIds)
    }

    fun getBySessionId(sessionId: String): WalletConnectSession?{
        return dao.getBySessionId(sessionId)
    }

    fun updateWalletConnectSession(session: WalletConnectSession){
        dao.updateWalletConnectSession(session)
    }
}
