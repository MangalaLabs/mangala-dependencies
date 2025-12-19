/*
 * Copyright 2023-2024 Mangala Wallet
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
 * This file uses patterns and conventions from eos-jvm
 * (https://github.com/memtrip/eos-jvm) by memtrip LTD.
 */

package com.mangala.app.di.pluginpoint.privacyfeature

import androidx.lifecycle.LifecycleObserver
import com.mangala.app.browser.downloader.FileDownloadBroadcastReceiver
import com.mangala.app.browser.httpauth.RealWebViewHttpAuthStore
import com.mangala.app.browser.shortcut.ShortcutReceiver
import com.mangala.app.di.pluginpoint.api.LifecycleObserverPluginPoint
import com.mangala.app.di.pluginpoint.privacyfeature.PrivacyFeaturePluginPluginPoint
import com.mangala.app.global.initialization.AppDataLoader
import com.mangala.app.global.migrations.MigrationLifecycleObserver
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.httpsupgrade.HttpsUpgraderImpl
import com.mangala.app.job.AndroidWorkScheduler
import com.mangala.app.notification.NotificationRegistrar
import com.mangala.app.pixels.EnqueuedPixelWorker
import com.mangala.app.statistics.api.OfflinePixelScheduler
import com.mangala.app.surrogates.ResourceSurrogateLoader
import com.mangala.app.traces.AppStartUpTracer
import com.mangala.app.trackerdetection.TrackerDataLoader
import com.mangala.app.widget.WidgetAddedReceiver
import com.mangala.di.DaggerSet
import com.mangala.macos_impl.waitlist.ui.MacOsWaitlistCodeFetcher
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
import com.mangala.privacy.config.impl.observers.LocalPrivacyConfigObserver
import com.mangala.privacy.config.impl.workers.PrivacyConfigDownloadWorkerScheduler
import com.mangala.remote.messaging.impl.RemoteMessagingConfigDownloadScheduler
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//interface PrivacyFeaturePluginPluginPointModule {
//    @Multibinds
//    fun pixelInterceptorPlugin(): Set<PrivacyFeaturePlugin>
//
//    @Binds
//    @IntoSet
//    fun bindAmpLinksPlugin(ampLinksPlugin: AmpLinksPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindAutofillPlugin(autofillPlugin: AutofillPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindContentBlockingPlugin(contentBlockingPlugin: ContentBlockingPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindDrmPlugin(drmPlugin: DrmPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindGpcPlugin(gpcPlugin: GpcPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindHttpsPlugin(httpsPlugin: HttpsPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindTrackerAllowlistPlugin(trackerAllowlistPlugin: TrackerAllowlistPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindTrackingParametersPlugin(trackingParametersPlugin: TrackingParametersPlugin): PrivacyFeaturePlugin
//
//    @Binds
//    @IntoSet
//    fun bindUserAgentPlugin(userAgentPlugin: UserAgentPlugin): PrivacyFeaturePlugin

//    @Binds
//    fun bindPrivacyFeaturePluginPluginPoint(privacyFeaturePluginPluginPoint: PrivacyFeaturePluginPluginPoint): PluginPoint<PrivacyFeaturePlugin>
//}

val privacyFeaturePluginPluginPointModule = module {
    // Define the PrivacyFeaturePluginPluginPointModule
    single<PrivacyFeaturePlugin> { AmpLinksPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { AutofillPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { ContentBlockingPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { DrmPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { GpcPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { HttpsPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { TrackerAllowlistPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { TrackingParametersPlugin(get(), get()) }
    single<PrivacyFeaturePlugin> { UserAgentPlugin(get(), get()) }



    factory<PluginPoint<PrivacyFeaturePlugin>> {
        PrivacyFeaturePluginPluginPoint()
    }
}
