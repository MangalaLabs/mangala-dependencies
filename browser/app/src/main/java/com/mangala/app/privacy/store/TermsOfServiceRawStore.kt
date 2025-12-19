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

package com.mangala.app.privacy.store

import android.content.Context
import com.mangala.app.privacy.model.TermsOfService
import com.mangala.app.privacy.model.TermsOfServiceData
import com.schoolonair.wallet.browser.app.R
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
/**
 * This raw file store is temporary. Once we move to an api call to retrieve the json
 * we'll store the content in a db rather than a raw file.
 */

class TermsOfServiceRawStore (
    private val moshi: Moshi,
    private val context: Context
) : TermsOfServiceStore {

    private var data: List<TermsOfService> = ArrayList()
    private var initialized: Boolean = false

    override suspend fun loadData() {
        withContext(Dispatchers.IO) {
            val json = context.resources.openRawResource(R.raw.tosdr).bufferedReader().use { it.readText() }
//            val type = Types.newParameterizedType(List::class.java, TermsOfService::class.java)
            val adapter = moshi.adapter(TermsOfServiceData::class.java)
//            val adapter: JsonAdapter<List<TermsOfService>> = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(type)
            val result = adapter.fromJson(json)!!
            data = result.jsonToEntities()
            Timber.d("jsonjsonjson " + data.size)
            Timber.i("Initialised TermsOfService data")
            initialized = true
        }
    }

    override val terms: List<TermsOfService>
        get() = data
}
