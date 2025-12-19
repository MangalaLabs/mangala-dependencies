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



package com.mangala.app.di.pluginpoint.featuretoggles

import androidx.lifecycle.LifecycleObserver
import com.mangala.app.di.pluginpoint.featuretoggles.FeatureTogglesPluginPointPluginPoint
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.httpsupgrade.HttpsUpgraderImpl
import com.mangala.di.DaggerSet
import com.mangala.feature.toggles.api.FeatureTogglesPlugin
import com.mangala.privacy.config.impl.plugins.PrivacyFeatureTogglesPlugin
import org.koin.core.qualifier.named
import org.koin.dsl.module


//@Module
//@InstallIn(SingletonComponent::class)
//interface FeatureTogglesPluginPointPluginPointModule {
//    @Multibinds
//    fun featureTogglesPlugins(): DaggerSet<FeatureTogglesPlugin>
//
//    @Binds
//    @IntoSet
//    fun bindPrivacyFeatureTogglesPlugin(privacyFeatureTogglesPlugin: PrivacyFeatureTogglesPlugin): FeatureTogglesPlugin
//
//    @Binds
//    fun bindFeatureTogglesPluginPoint(featureTogglesPlugin: FeatureTogglesPluginPointPluginPoint): PluginPoint<FeatureTogglesPlugin>
//}

val featureTogglesPluginPointModule = module {
    // Define the FeatureTogglesPlugin instances
    single<FeatureTogglesPlugin> {
        PrivacyFeatureTogglesPlugin(get(), get())
    }

    // Define the FeatureTogglesPluginPoint
    factory<PluginPoint<FeatureTogglesPlugin>> {
        FeatureTogglesPluginPointPluginPoint()
    }
}

