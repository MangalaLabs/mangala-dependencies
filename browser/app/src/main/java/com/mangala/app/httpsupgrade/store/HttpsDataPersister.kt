/*
 * Copyright (c) DuckDuckGo, Inc.
 * Copyright (c) 2023-2025 Mangala Wallet
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
 * Modified from original source: https://github.com/duckduckgo/Android
 */



package com.mangala.app.httpsupgrade.store

import com.mangala.app.global.db.AppDatabase
import com.mangala.app.global.store.BinaryDataStore
import com.mangala.app.httpsupgrade.model.HttpsBloomFilterSpec
import com.mangala.app.httpsupgrade.model.HttpsFalsePositiveDomain
import timber.log.Timber
import java.io.IOException

class HttpsDataPersister (
    private val binaryDataStore: BinaryDataStore,
    private val httpsBloomSpecDao: HttpsBloomFilterSpecDao,
    private val httpsFalsePositivesDao: HttpsFalsePositivesDao,
    private val appDatabase: AppDatabase
) {

    fun persistBloomFilter(
        specification: HttpsBloomFilterSpec,
        bytes: ByteArray,
        falsePositives: List<HttpsFalsePositiveDomain>
    ) {
        appDatabase.runInTransaction {
            persistBloomFilter(specification, bytes)
            persistFalsePositives(falsePositives)
        }
    }

    fun persistBloomFilter(
        specification: HttpsBloomFilterSpec,
        bytes: ByteArray
    ) {
        if (!binaryDataStore.verifyCheckSum(bytes, specification.sha256)) {
            throw IOException("Https binary has incorrect sha, throwing away file")
        }

        Timber.d("Updating https bloom data store with new data")
        appDatabase.runInTransaction {
            httpsBloomSpecDao.insert(specification)
            binaryDataStore.saveData(HttpsBloomFilterSpec.HTTPS_BINARY_FILE, bytes)
        }
    }

    fun persistFalsePositives(falsePositives: List<HttpsFalsePositiveDomain>) {
        httpsFalsePositivesDao.updateAll(falsePositives)
    }

    fun isPersisted(specification: HttpsBloomFilterSpec): Boolean {
        return specification == httpsBloomSpecDao.get() && binaryDataStore.verifyCheckSum(
            HttpsBloomFilterSpec.HTTPS_BINARY_FILE,
            specification.sha256
        )
    }
}
