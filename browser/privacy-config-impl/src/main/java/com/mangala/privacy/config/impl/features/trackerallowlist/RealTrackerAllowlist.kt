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



package com.mangala.privacy.config.impl.features.trackerallowlist

import androidx.core.net.toUri
import com.mangala.app.global.UriString
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.privacy.config.api.PrivacyFeatureName
import com.mangala.privacy.config.api.TrackerAllowlist
import com.mangala.privacy.config.store.TrackerAllowlistEntity
import com.mangala.privacy.config.store.features.trackerallowlist.TrackerAllowlistRepository
import java.net.URI


//@Singleton
class RealTrackerAllowlist (
    private val trackerAllowlistRepository: TrackerAllowlistRepository,
    private val featureToggle: FeatureToggle
) : TrackerAllowlist {

    override fun isAnException(
        documentURL: String,
        url: String
    ): Boolean {
        return if (featureToggle.isFeatureEnabled(PrivacyFeatureName.TrackerAllowlistFeatureName, true)) {
            trackerAllowlistRepository.exceptions
                .filter { UriString.sameOrSubdomain(url, it.domain) }
                .map { matches(url, documentURL, it) }
                .firstOrNull() ?: false
        } else {
            false
        }
    }

    private fun matches(
        url: String,
        documentUrl: String,
        trackerAllowlist: TrackerAllowlistEntity
    ): Boolean {
        val cleanedUrl = removePortFromUrl(url)
        return trackerAllowlist.rules.any {
            val regex = ".*${it.rule}.*".toRegex()
            cleanedUrl.matches(regex) && (it.domains.contains("<all>") || it.domains.any { domain -> UriString.sameOrSubdomain(documentUrl, domain) })
        }
    }

    private fun removePortFromUrl(url: String): String {
        return try {
            val uri = url.toUri()
            URI(uri.scheme, uri.host, uri.path, uri.fragment).toString()
        } catch (e: Exception) {
            url
        }
    }
}
