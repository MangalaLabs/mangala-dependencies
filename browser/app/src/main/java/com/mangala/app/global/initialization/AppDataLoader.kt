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


package com.mangala.app.global.initialization

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.privacy.model.PrivacyPractices
import com.mangala.app.privacy.store.TermsOfServiceStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber


class AppDataLoader(
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope,
    private val termsOfServiceStore: TermsOfServiceStore,
    private val privacyPractices: PrivacyPractices
) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        appCoroutineScope.launch {
            Timber.i("Started to load app data")
            termsOfServiceStore.loadData()
            privacyPractices.loadData()
            Timber.i("Finished loading app data")
        }
    }
}
