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

import androidx.room.TypeConverter
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.schoolonair.wallet.core.app.CoreApp.Companion.encryptionManager
//import com.trustwallet.walletconnect.models.WCPeerMeta
//import com.trustwallet.walletconnect.models.session.WCSession
//import com.schoolonair.wallet.core.storage.SecretList
//import com.schoolonair.wallet.core.storage.SecretString
//import com.schoolonair.wallet.local.nft.NftAssetAttribute
import java.math.BigDecimal
import java.util.*

class DatabaseConverters {

//    private val gson by lazy { Gson() }

    // BigDecimal

    @TypeConverter
    fun fromString(value: String?): BigDecimal? = try {
        value?.let { BigDecimal(it) }
    } catch (e: Exception) {
        null
    }

    @TypeConverter
    fun toString(bigDecimal: BigDecimal?): String? {
        return bigDecimal?.toPlainString()
    }

    // SecretString
//
//    @TypeConverter
//    fun decryptSecretString(value: String?): SecretString? {
//        if (value == null) return null
//
//        return try {
//            SecretString(encryptionManager.decrypt(value))
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    @TypeConverter
//    fun encryptSecretString(secretString: SecretString?): String? {
//        return secretString?.value?.let { encryptionManager.encrypt(it) }
//    }
//
//    // SecretList
//
//    @TypeConverter
//    fun decryptSecretList(value: String?): SecretList? {
//        if (value == null) return null
//
//        return try {
//            SecretList(encryptionManager.decrypt(value).split(","))
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    @TypeConverter
//    fun encryptSecretList(secretList: SecretList?): String? {
//        return secretList?.list?.joinToString(separator = ",")?.let {
//            encryptionManager.encrypt(it)
//        }
//    }
//
//    @TypeConverter
//    fun fromWCPeerMeta(peerMeta: WCPeerMeta): String {
//        return gson.toJson(peerMeta)
//    }
//
//    @TypeConverter
//    fun toWCPeerMeta(json: String): WCPeerMeta {
//        return gson.fromJson(json, WCPeerMeta::class.java)
//    }
//
//    @TypeConverter
//    fun fromWCSession(session: WCSession): String {
//        return gson.toJson(session)
//    }
//
//    @TypeConverter
//    fun toWCSession(json: String): WCSession {
//        return gson.fromJson(json, WCSession::class.java)
//    }

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }
//
//    @TypeConverter
//    fun fromAttributes(attributes: List<NftAssetAttribute>): String {
//        return gson.toJson(attributes)
//    }
//
//    @TypeConverter
//    fun toAttributes(json: String): List<NftAssetAttribute> {
//        return gson.fromJson(json, object : TypeToken<List<NftAssetAttribute>>() {}.type)
//    }

}
