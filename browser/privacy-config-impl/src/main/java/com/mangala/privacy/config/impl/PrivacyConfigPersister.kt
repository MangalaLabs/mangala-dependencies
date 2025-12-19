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
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.feature.toggles.api.FeatureName
import com.mangala.privacy.config.impl.models.JsonPrivacyConfig
import com.mangala.privacy.config.api.PrivacyFeaturePlugin
import com.mangala.privacy.config.store.PrivacyConfig
import com.mangala.privacy.config.store.PrivacyConfigDatabase
import com.mangala.privacy.config.store.PrivacyConfigRepository
import com.mangala.privacy.config.store.PrivacyFeatureTogglesRepository
import com.mangala.privacy.config.store.features.unprotectedtemporary.UnprotectedTemporaryRepository
import timber.log.Timber

//import javax.inject.Inject
//import javax.inject.Singleton

interface PrivacyConfigPersister {
    suspend fun persistPrivacyConfig(jsonPrivacyConfig: JsonPrivacyConfig)
}

@WorkerThread
//@Singleton
class RealPrivacyConfigPersister (
    private val privacyFeaturePluginPoint: PluginPoint<PrivacyFeaturePlugin>,
    private val privacyFeatureTogglesRepository: PrivacyFeatureTogglesRepository,
    private val unprotectedTemporaryRepository: UnprotectedTemporaryRepository,
    private val privacyConfigRepository: PrivacyConfigRepository,
    private val database: PrivacyConfigDatabase
) : PrivacyConfigPersister {

    override suspend fun persistPrivacyConfig(jsonPrivacyConfig: JsonPrivacyConfig) {
        val privacyConfig = privacyConfigRepository.get()
        val newVersion = jsonPrivacyConfig.version
        val previousVersion = privacyConfig?.version ?: 0

        if (newVersion > previousVersion) {
            database.runInTransaction {
                privacyFeatureTogglesRepository.deleteAll()
                privacyConfigRepository.insert(PrivacyConfig(version = jsonPrivacyConfig.version, readme = jsonPrivacyConfig.readme))
                unprotectedTemporaryRepository.updateAll(jsonPrivacyConfig.unprotectedTemporary)
                jsonPrivacyConfig.features.forEach { feature ->
                    feature.value?.let { jsonObject ->
                        Timber.d("1991 privacyFeaturePluginPoint " + privacyFeaturePluginPoint.getPlugins().size)
                        privacyFeaturePluginPoint.getPlugins().firstOrNull { feature.key == it.featureName.value }?.let { featurePlugin ->
                            featurePlugin.store(FeatureName { feature.key }, jsonObject.toString())
                        }
                    }
                }
            }
        }
    }
}
