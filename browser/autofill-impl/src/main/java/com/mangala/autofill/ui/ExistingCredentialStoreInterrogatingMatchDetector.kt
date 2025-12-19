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


package com.mangala.autofill.ui

import com.mangala.app.global.DefaultDispatcherProvider
import com.mangala.app.global.DispatcherProvider
import com.mangala.autofill.store.AutofillStore
import kotlinx.coroutines.withContext

class ExistingCredentialStoreInterrogatingMatchDetector (
    private val autofillStore: AutofillStore,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) :
    ExistingCredentialMatchDetector {

    override suspend fun determine(currentUrl: String, username: String, password: String): AutofillStore.ContainsCredentialsResult {
        return withContext(dispatcherProvider.io()) {
            autofillStore.containsCredentials(currentUrl, username, password)
        }
    }
}
