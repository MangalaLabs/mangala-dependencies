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


class WC2SessionStorage (private val dao: WC2SessionDao) {

    fun getAllSessions(): List<WalletConnectV2Session> {
        return dao.getAll()
    }

    fun getSessionsByAccountId(accountId: String): List<WalletConnectV2Session> {
        return dao.getByAccountId(accountId)
    }

    fun save(sessions: List<WalletConnectV2Session>) {
        dao.insert(sessions)
    }

    fun deleteSessionsExcept(accountIds: List<String> = listOf()) {
        dao.deleteAllExcept(accountIds)
    }

    fun deleteSessionsByTopics(topics: List<String> = listOf()) {
        dao.deleteByTopics(topics)
    }

}
