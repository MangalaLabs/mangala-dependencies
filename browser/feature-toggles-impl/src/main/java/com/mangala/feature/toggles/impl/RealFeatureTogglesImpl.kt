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


package com.mangala.feature.toggles.impl

import android.util.Log
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.feature.toggles.api.FeatureTogglesPlugin
import com.mangala.feature.toggles.api.FeatureName
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject

class RealFeatureToggleImpl(private val featureTogglesPluginPoint: PluginPoint<FeatureTogglesPlugin>) :
    FeatureToggle, KoinComponent {

    private val privacyFeatureTogglesPlugin: FeatureTogglesPlugin by inject()
    override fun isFeatureEnabled(
        featureName: FeatureName,
        defaultValue: Boolean
    ): Boolean {
        Log.d("1991", " size isFeatureEnabled " + featureTogglesPluginPoint.getPlugins().size + " name " + featureName)
//        featureTogglesPluginPoint.getPlugins().forEach { plugin ->
//            plugin.isEnabled(featureName, defaultValue)?.let { return it }
//        }
        privacyFeatureTogglesPlugin.isEnabled(featureName, defaultValue)?.let { return it }
        throw IllegalArgumentException("Unknown feature: ${featureName.value}")
    }
}
