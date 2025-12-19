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

package com.mangala.app.httpsupgrade

import androidx.annotation.WorkerThread
import com.mangala.app.global.store.BinaryDataStore
import com.mangala.app.httpsupgrade.model.HttpsBloomFilterSpec.Companion.HTTPS_BINARY_FILE
import com.mangala.app.httpsupgrade.store.HttpsBloomFilterSpecDao
import com.mangala.app.httpsupgrade.store.HttpsEmbeddedDataPersister
import com.mangala.app.httpsupgrade.store.HttpsDataPersister
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.statistics.pixels.Pixel
import timber.log.Timber


interface HttpsBloomFilterFactory {
    fun create(): BloomFilter?
}

class HttpsBloomFilterFactoryImpl(
    private val dao: HttpsBloomFilterSpecDao,
    private val binaryDataStore: BinaryDataStore,
    private val httpsEmbeddedDataPersister: HttpsEmbeddedDataPersister,
    private val httpsDataPersister: HttpsDataPersister,
    private val pixel: Pixel,
) : HttpsBloomFilterFactory {

    @WorkerThread
    override fun create(): BloomFilter? {

        if (httpsEmbeddedDataPersister.shouldPersistEmbeddedData()) {
            Timber.d("Https update data not found, loading embedded data")
            httpsEmbeddedDataPersister.persistEmbeddedData()
        }

        val specification = dao.get()
        val dataPath = binaryDataStore.dataFilePath(HTTPS_BINARY_FILE)
        if (dataPath == null || specification == null || !httpsDataPersister.isPersisted(specification)) {
            Timber.d("Https update data not available")
            return null
        }

        val initialTimestamp = System.currentTimeMillis()
        Timber.d("Found https data at $dataPath, building filter")
        val bloomFilter = try {
            BloomFilter(dataPath, specification.bitCount, specification.totalEntries)
        } catch (t: Throwable) {
            Timber.e(t, "Error creating the bloom filter")
            pixel.fire(AppPixelName.CREATE_BLOOM_FILTER_ERROR)
            null
        }
        Timber.v("Loading took ${System.currentTimeMillis() - initialTimestamp}ms")

        return bloomFilter
    }
}
