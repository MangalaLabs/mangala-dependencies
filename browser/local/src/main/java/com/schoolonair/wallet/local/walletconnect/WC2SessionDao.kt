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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WC2SessionDao {

    @Query("SELECT * FROM WalletConnectV2Session")
    fun getAll(): List<WalletConnectV2Session>

    @Query("SELECT * FROM WalletConnectV2Session WHERE accountId = :accountId")
    fun getByAccountId(accountId: String): List<WalletConnectV2Session>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sessions: List<WalletConnectV2Session>)

    @Query("DELETE FROM WalletConnectV2Session WHERE topic IN (:topics)")
    fun deleteByTopics(topics: List<String>)

    @Query("DELETE FROM WalletConnectV2Session WHERE accountId NOT IN (:accountIds)")
    fun deleteAllExcept(accountIds: List<String>)

}
