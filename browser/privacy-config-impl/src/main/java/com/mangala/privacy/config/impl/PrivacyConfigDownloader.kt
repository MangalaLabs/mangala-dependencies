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


package com.mangala.privacy.config.impl

import androidx.annotation.WorkerThread
import com.mangala.privacy.config.impl.network.PrivacyConfigService
import timber.log.Timber
//import javax.inject.Inject

interface PrivacyConfigDownloader {
    suspend fun download(): Boolean
}

@WorkerThread
class RealPrivacyConfigDownloader (
    private val privacyConfigService: PrivacyConfigService,
    private val privacyConfigPersister: PrivacyConfigPersister
) : PrivacyConfigDownloader {

    override suspend fun download(): Boolean {
        Timber.d("Downloading privacy config")
        val response = runCatching {
            privacyConfigService.privacyConfig()
        }.onSuccess {
            privacyConfigPersister.persistPrivacyConfig(it)
        }.onFailure {
            Timber.w(it.localizedMessage)
        }
        return response.isSuccess
    }
}
