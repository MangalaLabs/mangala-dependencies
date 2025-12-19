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



package com.mangala.privacy.config.impl.features.https

import com.mangala.feature.toggles.api.FeatureName
import com.mangala.privacy.config.api.PrivacyFeatureName
import com.mangala.privacy.config.impl.features.privacyFeatureValueOf
import com.mangala.privacy.config.api.PrivacyFeaturePlugin
import com.mangala.privacy.config.store.HttpsExceptionEntity
import com.mangala.privacy.config.store.PrivacyFeatureToggles
import com.mangala.privacy.config.store.PrivacyFeatureTogglesRepository
import com.mangala.privacy.config.store.features.https.HttpsRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class HttpsPlugin (
    private val httpsRepository: HttpsRepository,
    private val privacyFeatureTogglesRepository: PrivacyFeatureTogglesRepository
) : PrivacyFeaturePlugin {

    override fun store(
        name: FeatureName,
        jsonString: String
    ): Boolean {
        @Suppress("NAME_SHADOWING")
        val name = privacyFeatureValueOf(name.value)
        if (name == featureName) {
            val httpsExceptions = mutableListOf<HttpsExceptionEntity>()
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter: JsonAdapter<HttpsFeature> =
                moshi.adapter(HttpsFeature::class.java)

            val httpsFeature: HttpsFeature? = jsonAdapter.fromJson(jsonString)
            httpsFeature?.exceptions?.map {
                httpsExceptions.add(HttpsExceptionEntity(it.domain, it.reason))
            }
            httpsRepository.updateAll(httpsExceptions)
            val isEnabled = httpsFeature?.state == "enabled"
            privacyFeatureTogglesRepository.insert(PrivacyFeatureToggles(name, isEnabled, httpsFeature?.minSupportedVersion))
            return true
        }
        return false
    }

    override val featureName: PrivacyFeatureName = PrivacyFeatureName.HttpsFeatureName
}
