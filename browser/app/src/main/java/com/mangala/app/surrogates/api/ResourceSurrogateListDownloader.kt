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


package com.mangala.app.surrogates.api

import com.mangala.app.global.api.isCached
import com.mangala.app.surrogates.ResourceSurrogateLoader
import com.mangala.app.surrogates.store.ResourceSurrogateDataStore
import io.reactivex.Completable
import timber.log.Timber
import java.io.IOException

class ResourceSurrogateListDownloader (
    private val service: ResourceSurrogateListService,
    private val surrogatesDataStore: ResourceSurrogateDataStore,
    private val resourceSurrogateLoader: ResourceSurrogateLoader
) {

    fun downloadList(): Completable {

        return Completable.fromAction {

            Timber.d("Downloading Google Analytics Surrogates data")

            val call = service.surrogates()
            val response = call.execute()

            Timber.d("Response received, success=${response.isSuccessful}")

            if (response.isCached && surrogatesDataStore.hasData()) {
                Timber.d("Surrogates data already cached and stored")
                return@fromAction
            }

            if (response.isSuccessful) {
                val bodyBytes = response.body()!!.bytes()
                Timber.d("Updating surrogates data store with new data")
                persistData(bodyBytes)
                resourceSurrogateLoader.loadData()
            } else {
                throw IOException("Status: ${response.code()} - ${response.errorBody()?.string()}")
            }
        }
    }

    private fun persistData(bodyBytes: ByteArray) {
        surrogatesDataStore.saveData(bodyBytes)
    }
}
