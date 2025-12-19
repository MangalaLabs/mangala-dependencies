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



package com.mangala.privacy.config.impl.observers

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider

import com.mangala.privacy.config.impl.R
import com.mangala.privacy.config.impl.PrivacyConfigPersister
import com.mangala.privacy.config.impl.models.JsonPrivacyConfig
import com.mangala.privacy.config.impl.network.JSONObjectAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@WorkerThread
//@Singleton
class LocalPrivacyConfigObserver(
    private val context: Context,
    private val privacyConfigPersister: PrivacyConfigPersister,
    @AppCoroutineScope val coroutineScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider
) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        coroutineScope.launch(dispatcherProvider.io()) { loadPrivacyConfig() }
    }

    private suspend fun loadPrivacyConfig() {
        val privacyConfigJson = getPrivacyConfigFromFile()
        privacyConfigJson?.let {
            privacyConfigPersister.persistPrivacyConfig(it)
        }
    }

    private fun getPrivacyConfigFromFile(): JsonPrivacyConfig? {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(JSONObjectAdapter()).build()
        val json = context.resources.openRawResource(R.raw.privacy_config).bufferedReader().use { it.readText() }
        val adapter = moshi.adapter(JsonPrivacyConfig::class.java)
        return adapter.fromJson(json)
    }
}
