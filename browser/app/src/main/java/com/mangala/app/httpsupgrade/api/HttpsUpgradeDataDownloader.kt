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



package com.mangala.app.httpsupgrade.api

import com.mangala.app.global.api.isCached
import com.mangala.app.httpsupgrade.HttpsUpgrader
import com.mangala.app.httpsupgrade.store.HttpsFalsePositivesDao
import com.mangala.app.httpsupgrade.model.HttpsBloomFilterSpec
import com.mangala.app.httpsupgrade.store.HttpsDataPersister
import io.reactivex.Completable
import io.reactivex.Completable.fromAction
import timber.log.Timber
import java.io.IOException

class HttpsUpgradeDataDownloader(
    private val service: HttpsUpgradeService,
    private val httpsUpgrader: HttpsUpgrader,
    private val dataPersister: HttpsDataPersister,
    private val bloomFalsePositivesDao: HttpsFalsePositivesDao
) {

    fun download(): Completable {

        val filter = service.httpsBloomFilterSpec()
            .flatMapCompletable {
                downloadBloomFilter(it)
            }
        val falsePositives = downloadFalsePositives()

        return Completable.mergeDelayError(listOf(filter, falsePositives))
            .doOnComplete {
                Timber.i("Https download task completed successfully")
            }
    }

    private fun downloadBloomFilter(specification: HttpsBloomFilterSpec): Completable {
        return fromAction {

            Timber.d("Downloading https bloom filter binary")

            if (dataPersister.isPersisted(specification)) {
                Timber.d("Https bloom data already stored for this spec")
                return@fromAction
            }

            val call = service.httpsBloomFilter()
            val response = call.execute()
            if (!response.isSuccessful) {
                throw IOException("Status: ${response.code()} - ${response.errorBody()?.string()}")
            }

            val bytes = response.body()!!.bytes()
            dataPersister.persistBloomFilter(specification, bytes)
            httpsUpgrader.reloadData()
        }
    }

    private fun downloadFalsePositives(): Completable {

        Timber.d("Downloading HTTPS false positives")
        return fromAction {

            val call = service.falsePositives()
            val response = call.execute()

            if (response.isCached && bloomFalsePositivesDao.count() > 0) {
                Timber.d("Https false positives already cached and stored")
                return@fromAction
            }

            if (response.isSuccessful) {
                val falsePositives = response.body()!!
                Timber.d("Updating https false positives with new data")
                dataPersister.persistFalsePositives(falsePositives)
            } else {
                throw IOException("Status: ${response.code()} - ${response.errorBody()?.string()}")
            }
        }
    }
}
