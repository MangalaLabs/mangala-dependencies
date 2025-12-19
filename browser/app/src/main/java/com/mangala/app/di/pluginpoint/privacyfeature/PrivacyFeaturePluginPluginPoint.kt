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



package com.mangala.app.di.pluginpoint.privacyfeature

import com.mangala.app.global.plugins.PluginPoint
import com.mangala.privacy.config.api.PrivacyFeaturePlugin
import com.mangala.privacy.config.impl.features.amplinks.AmpLinksPlugin
import com.mangala.privacy.config.impl.features.autofill.AutofillPlugin
import com.mangala.privacy.config.impl.features.contentblocking.ContentBlockingPlugin
import com.mangala.privacy.config.impl.features.drm.DrmPlugin
import com.mangala.privacy.config.impl.features.gpc.GpcPlugin
import com.mangala.privacy.config.impl.features.https.HttpsPlugin
import com.mangala.privacy.config.impl.features.trackerallowlist.TrackerAllowlistPlugin
import com.mangala.privacy.config.impl.features.trackingparameters.TrackingParametersPlugin
import com.mangala.privacy.config.impl.features.useragent.UserAgentPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class PrivacyFeaturePluginPluginPoint (
): PluginPoint<PrivacyFeaturePlugin>, KoinComponent {

    private val ampLinksPlugin: AmpLinksPlugin by inject()
    private val autofillPlugin: AutofillPlugin by inject()
    private val contentBlockingPlugin: ContentBlockingPlugin by inject()
    private val drmPlugin: DrmPlugin by inject()
    private val gpcPlugin: GpcPlugin by inject()
    private val httpsPlugin: HttpsPlugin by inject()
    private val trackerAllowlistPlugin: TrackerAllowlistPlugin by inject()
    private val trackingParametersPlugin: TrackingParametersPlugin by inject()
    private val userAgentPlugin: UserAgentPlugin by inject()

    override fun getPlugins(): Collection<PrivacyFeaturePlugin> = listOf(
        ampLinksPlugin,
        autofillPlugin,
        contentBlockingPlugin,
        drmPlugin,
        gpcPlugin,
        httpsPlugin,
        trackerAllowlistPlugin,
        trackingParametersPlugin,
        userAgentPlugin
    )
}
