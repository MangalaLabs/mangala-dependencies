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



package com.mangala.privacy.config.impl.features.trackingparameters

import android.net.Uri
import androidx.core.net.toUri
import com.mangala.app.global.UriString
import com.mangala.app.global.domain
import com.mangala.app.global.replaceQueryParameters
import com.mangala.app.userwhitelist.api.UserWhiteListRepository
import com.mangala.di.scopes.FragmentScope
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.privacy.config.api.PrivacyFeatureName
import com.mangala.privacy.config.api.TrackingParameters
import com.mangala.privacy.config.impl.features.unprotectedtemporary.UnprotectedTemporary
import com.mangala.privacy.config.store.features.trackingparameters.TrackingParametersRepository
//import dagger.WrongScope
import timber.log.Timber
import java.lang.UnsupportedOperationException


//@WrongScope("This should be one instance per BrowserTabFragment", FragmentScope::class)
//@Singleton
class RealTrackingParameters(
    private val trackingParametersRepository: TrackingParametersRepository,
    private val featureToggle: FeatureToggle,
    private val unprotectedTemporary: UnprotectedTemporary,
    val userWhiteListRepository: UserWhiteListRepository
) : TrackingParameters {

    override var lastCleanedUrl: String? = null

    override fun isAnException(initiatingUrl: String?, url: String): Boolean {
        return matches(initiatingUrl) || matches(url) ||
            unprotectedTemporary.isAnException(url) ||
            userWhiteListRepository.userWhiteList.contains(url.toUri().domain())
    }

    private fun matches(url: String?): Boolean {
        if (url == null) return false
        return trackingParametersRepository.exceptions.any { UriString.sameOrSubdomain(url, it.domain) }
    }

    override fun cleanTrackingParameters(initiatingUrl: String?, url: String): String? {
        if (!featureToggle.isFeatureEnabled(PrivacyFeatureName.TrackingParametersFeatureName)) return null
        if (isAnException(initiatingUrl, url)) return null

        val trackingParameters = trackingParametersRepository.parameters

        val uri = Uri.parse(url)

        try {
            val queryParameters = uri.queryParameterNames

            if (queryParameters.isEmpty()) {
                return null
            }
            val preservedParameters = getPreservedParameters(queryParameters, trackingParameters)
            if (preservedParameters.size == queryParameters.size) {
                return null
            }
            val cleanedUrl = uri.replaceQueryParameters(preservedParameters).toString()

            lastCleanedUrl = cleanedUrl

            return cleanedUrl
        } catch (exception: UnsupportedOperationException) {
            Timber.e("Tracking Parameter Removal: ${exception.message}")
            return null
        }
    }

    private fun getPreservedParameters(
        queryParameters: MutableSet<String>,
        trackingParameters: List<Regex>
    ) =
        queryParameters.filter { parameter ->
            var match = false
            for (trackingParameter in trackingParameters) {
                match = parameter.matches(trackingParameter)
                if (match) break
            }
            !match
        }
}
