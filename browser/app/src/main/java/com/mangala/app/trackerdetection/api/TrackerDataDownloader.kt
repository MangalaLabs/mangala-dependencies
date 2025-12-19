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



package com.mangala.app.trackerdetection.api

import com.mangala.app.global.db.AppDatabase
import com.mangala.app.global.extensions.extractETag
import com.mangala.app.global.store.BinaryDataStore
import com.mangala.app.trackerdetection.Client.ClientName.*
import com.mangala.app.trackerdetection.TrackerDataLoader
import com.mangala.app.trackerdetection.db.TdsMetadataDao
import io.reactivex.Completable
import okhttp3.Headers
import timber.log.Timber
import java.io.IOException

class TrackerDataDownloader(
    private val trackerListService: TrackerListService,
    private val binaryDataStore: BinaryDataStore,
    private val trackerDataLoader: TrackerDataLoader,
    private val appDatabase: AppDatabase,
    private val metadataDao: TdsMetadataDao
) {

    fun downloadTds(): Completable {

        return Completable.fromAction {

            Timber.d("Downloading tds.json")

            val call = trackerListService.tds()
            val response = call.execute()

            if (!response.isSuccessful) {
                throw IOException("Status: ${response.code()} - ${response.errorBody()?.string()}")
            }

            val body = response.body()!!
            val eTag = response.headers().extractETag()
            val oldEtag = metadataDao.eTag()
            if (eTag != oldEtag) {
                Timber.d("Updating tds data from server")
                appDatabase.runInTransaction {
                    trackerDataLoader.persistTds(eTag, body)
                    trackerDataLoader.loadTrackers()
                }
            }
        }
    }

    fun clearLegacyLists(): Completable {
        return Completable.fromAction {
            listOf(EASYLIST, EASYPRIVACY, TRACKERSWHITELIST).forEach {
                if (binaryDataStore.hasData(it.name)) {
                    binaryDataStore.clearData(it.name)
                }
            }
            return@fromAction
        }
    }
}

fun Headers.extractETag(): String {
    return this["eTag"]?.removePrefix("W/")?.removeSurrounding("\"", "\"").orEmpty() // removes weak eTag validator
}
