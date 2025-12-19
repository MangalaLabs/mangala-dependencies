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



package com.mangala.remote.messaging.impl.matchers

import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.browser.api.AppProperties
import com.mangala.remote.messaging.impl.models.IntMatchingAttribute
import com.mangala.remote.messaging.impl.models.MATCHING_ATTR_INT_DEFAULT_VALUE
import com.mangala.remote.messaging.impl.models.MATCHING_ATTR_STRING_DEFAULT_VALUE
import com.mangala.remote.messaging.impl.models.MatchingAttribute
import com.mangala.remote.messaging.impl.models.RangeIntMatchingAttribute
import com.mangala.remote.messaging.impl.models.RangeStringMatchingAttribute
import com.mangala.remote.messaging.impl.models.StringMatchingAttribute
import com.mangala.remote.messaging.impl.models.asJsonFormat
import com.mangala.remote.messaging.impl.models.matches

class DeviceAttributeMatcher(
    private val appBuildConfig: AppBuildConfig,
    private val appProperties: AppProperties
) : AttributeMatcher {
    override suspend fun evaluate(matchingAttribute: MatchingAttribute): EvaluationResult? {
        when (matchingAttribute) {
            is MatchingAttribute.Api -> {
                if (matchingAttribute == MatchingAttribute.Api()) return EvaluationResult.Fail

                if (matchingAttribute.value != MATCHING_ATTR_INT_DEFAULT_VALUE) {
                    return (matchingAttribute as IntMatchingAttribute).matches(appBuildConfig.sdkInt)
                }
                return (matchingAttribute as RangeIntMatchingAttribute).matches(appBuildConfig.sdkInt)
            }
            is MatchingAttribute.Locale -> {
                if (matchingAttribute == MatchingAttribute.Locale()) return EvaluationResult.Fail
                return matchingAttribute.matches(appBuildConfig.deviceLocale.asJsonFormat())
            }
            is MatchingAttribute.WebView -> {
                if (matchingAttribute == MatchingAttribute.WebView()) return EvaluationResult.Fail
                if (matchingAttribute.value != MATCHING_ATTR_STRING_DEFAULT_VALUE) {
                    return (matchingAttribute as StringMatchingAttribute).matches(appProperties.webView())
                }
                return (matchingAttribute as RangeStringMatchingAttribute).matches(appProperties.webView())
            }
            else -> return null
        }
    }
}
