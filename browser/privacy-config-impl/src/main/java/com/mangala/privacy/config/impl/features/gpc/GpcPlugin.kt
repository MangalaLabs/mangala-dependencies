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



package com.mangala.privacy.config.impl.features.gpc

import com.mangala.feature.toggles.api.FeatureName
import com.mangala.privacy.config.api.PrivacyFeatureName
import com.mangala.privacy.config.impl.features.privacyFeatureValueOf
import com.mangala.privacy.config.api.PrivacyFeaturePlugin
import com.mangala.privacy.config.store.GpcExceptionEntity
import com.mangala.privacy.config.store.GpcHeaderEnabledSiteEntity
import com.mangala.privacy.config.store.PrivacyFeatureToggles
import com.mangala.privacy.config.store.PrivacyFeatureTogglesRepository
import com.mangala.privacy.config.store.features.gpc.GpcRepository

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class GpcPlugin (
    private val gpcRepository: GpcRepository,
    private val privacyFeatureTogglesRepository: PrivacyFeatureTogglesRepository
) : PrivacyFeaturePlugin {

    override fun store(
        name: FeatureName,
        jsonString: String
    ): Boolean {
        @Suppress("NAME_SHADOWING")
        val name = privacyFeatureValueOf(name.value)
        if (name == featureName) {
            val gpcExceptions = mutableListOf<GpcExceptionEntity>()
            val gpcHeaders = mutableListOf<GpcHeaderEnabledSiteEntity>()
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            val jsonAdapter: JsonAdapter<GpcFeature> =
                moshi.adapter(GpcFeature::class.java)

            val gpcFeature: GpcFeature? = jsonAdapter.fromJson(jsonString)
            gpcFeature?.exceptions?.map {
                gpcExceptions.add(GpcExceptionEntity(it.domain))
            }
            gpcFeature?.settings?.gpcHeaderEnabledSites?.map {
                gpcHeaders.add(GpcHeaderEnabledSiteEntity(it))
            }
            gpcRepository.updateAll(gpcExceptions, gpcHeaders)
            val isEnabled = gpcFeature?.state == "enabled"
            privacyFeatureTogglesRepository.insert(PrivacyFeatureToggles(name, isEnabled, gpcFeature?.minSupportedVersion))
            return true
        }
        return false
    }

    override val featureName: PrivacyFeatureName = PrivacyFeatureName.GpcFeatureName
}
