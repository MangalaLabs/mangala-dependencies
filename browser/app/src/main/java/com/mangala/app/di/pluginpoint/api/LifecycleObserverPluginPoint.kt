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


package com.mangala.app.di.pluginpoint.api

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import com.mangala.app.browser.defaultbrowsing.DefaultBrowserObserver
import com.mangala.app.browser.downloader.FileDownloadBroadcastReceiver
import com.mangala.app.browser.httpauth.RealWebViewHttpAuthStore
import com.mangala.app.browser.serviceworker.ServiceWorkerLifecycleObserver
import com.mangala.app.browser.shortcut.ShortcutReceiver
import com.mangala.app.fire.DataClearerForegroundAppRestartPixel
import com.mangala.app.fire.FireAnimationLoader
import com.mangala.app.fire.LottieFireAnimationLoader
import com.mangala.app.global.initialization.AppDataLoader
import com.mangala.app.global.install.AppInstallSharedPreferences
import com.mangala.app.global.install.AppInstallStore
import com.mangala.app.global.migrations.MigrationLifecycleObserver
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.global.rating.AppEnjoymentAppCreationObserver
import com.mangala.app.global.shortcut.AppShortcutCreatorLifecycleObserver
import com.mangala.app.httpsupgrade.HttpsUpgraderImpl
import com.mangala.app.job.AndroidWorkScheduler
import com.mangala.app.job.AppConfigurationSyncer
import com.mangala.app.notification.NotificationRegistrar
import com.mangala.app.onboarding.store.AppUserStageStore
import com.mangala.app.onboarding.store.UserStageStore
import com.mangala.app.pixels.EnqueuedPixelWorker
import com.mangala.app.privacy.cleanup.TrackersDbCleanerScheduler
import com.mangala.app.statistics.AtbInitializer
import com.mangala.app.statistics.api.OfflinePixelScheduler
import com.mangala.app.statistics.api.PixelSender
import com.mangala.app.statistics.api.RxPixelSender
import com.mangala.app.surrogates.ResourceSurrogateLoader
import com.mangala.app.tabs.db.TabsDbSanitizer
import com.mangala.app.traces.AppStartUpTracer
import com.mangala.app.trackerdetection.TrackerDataLoader
import com.mangala.app.usage.app.AppDaysUsedRecorder
import com.mangala.app.waitlist.email.AppEmailWaitlistCodeFetcher
import com.mangala.app.widget.FavoritesObserver
import com.mangala.app.widget.WidgetAddedReceiver
import com.mangala.bandwidth.impl.BandwidthScheduler
import com.mangala.macos_impl.waitlist.ui.MacOsWaitlistCodeFetcher
import com.mangala.privacy.config.impl.observers.LocalPrivacyConfigObserver
import com.mangala.privacy.config.impl.workers.PrivacyConfigDownloadWorkerScheduler
import com.mangala.remote.messaging.impl.RemoteMessagingConfigDownloadScheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class LifecycleObserverPluginPoint (
): PluginPoint<LifecycleObserver>, KoinComponent {

    private val plugins: List<LifecycleObserver> by lazy {
        getKoin().getAll<LifecycleObserver>()
    }

    private val plugins2: List<LifecycleEventObserver> by lazy {
        getKoin().getAll<LifecycleEventObserver>()
    }

    private val plugins3: List<DefaultLifecycleObserver> by lazy {
        getKoin().getAll<DefaultLifecycleObserver>()
    }

    private val appConfigurationSyncer: AppConfigurationSyncer by inject()
    private val appShortcutCreatorLifecycleObserver: AppShortcutCreatorLifecycleObserver by inject()
    private val appDaysUsedRecorder: AppDaysUsedRecorder by inject()
    private val bandwidthScheduler: BandwidthScheduler by inject()
    private val defaultBrowserObserver: DefaultBrowserObserver by inject()
    private val serviceWorkerLifecycleObserver: ServiceWorkerLifecycleObserver by inject()
    private val appEmailWaitlistCodeFetcher: AppEmailWaitlistCodeFetcher by inject()
    private val dataClearerForegroundAppRestartPixel: DataClearerForegroundAppRestartPixel by inject()
    private val appEnjoymentAppCreationObserver: AppEnjoymentAppCreationObserver by inject()
    private val rxPixelSender: PixelSender by inject()
    private val atbInitializer: AtbInitializer by inject()
    private val trackersDbCleanerScheduler: TrackersDbCleanerScheduler by inject()
    private val httpsUpgraderImpl: HttpsUpgraderImpl by inject()
    private val realWebViewHttpAuthStore: RealWebViewHttpAuthStore by inject()
    private val fileDownloadBroadcastReceiver: FileDownloadBroadcastReceiver by inject()
    private val appStartUpTracer: AppStartUpTracer by inject()
    private val offlinePixelScheduler: OfflinePixelScheduler by inject()
    private val remoteMessagingConfigDownloadScheduler: RemoteMessagingConfigDownloadScheduler by inject()
    private val privacyConfigDownloadWorkerScheduler: PrivacyConfigDownloadWorkerScheduler by inject()
    private val localPrivacyConfigObserver: LocalPrivacyConfigObserver by inject()
    private val widgetAddedReceiver: WidgetAddedReceiver by inject()
    private val macOsWaitlistCodeFetcher: MacOsWaitlistCodeFetcher by inject()
    private val trackerDataLoader: TrackerDataLoader by inject()
//    private val trackerDataDevReceiverRegister: TrackerDataDevReceiverRegister by inject()
    private val resourceSurrogateLoader: ResourceSurrogateLoader by inject()
    private val enqueuedPixelWorker: EnqueuedPixelWorker by inject()
    private val notificationRegistrar: NotificationRegistrar by inject()
    private val androidWorkScheduler: AndroidWorkScheduler by inject()
    private val migrationLifecycleObserver: MigrationLifecycleObserver by inject()
    private val appDataLoader: AppDataLoader by inject()
    private val shortcutReceiver: ShortcutReceiver by inject()
//    private val FlipperInitializer: FlipperInitializer by inject()
    private val appInstallSharedPreferences: AppInstallStore by inject()
    private val appUserStageStore: UserStageStore by inject()
    private val tabsDbSanitizer: TabsDbSanitizer by inject()
    private val favoritesObserver: FavoritesObserver by inject()
    private val lottieFireAnimationLoader: FireAnimationLoader by inject()
    override fun getPlugins(): Collection<LifecycleObserver> {
        val pluginTotal = mutableListOf<LifecycleObserver>()
        pluginTotal.add(appConfigurationSyncer)
        pluginTotal.add(appShortcutCreatorLifecycleObserver)
        pluginTotal.add(appDaysUsedRecorder)
        pluginTotal.add(bandwidthScheduler)
        pluginTotal.add(defaultBrowserObserver)
        pluginTotal.add(serviceWorkerLifecycleObserver)
        pluginTotal.add(appEmailWaitlistCodeFetcher)
        pluginTotal.add(dataClearerForegroundAppRestartPixel)
        pluginTotal.add(appEnjoymentAppCreationObserver)
        pluginTotal.add(rxPixelSender)
        pluginTotal.add(atbInitializer)
        pluginTotal.add(trackersDbCleanerScheduler)
        pluginTotal.add(httpsUpgraderImpl)
        pluginTotal.add(realWebViewHttpAuthStore)
        pluginTotal.add(fileDownloadBroadcastReceiver)
        pluginTotal.add(appStartUpTracer)
        pluginTotal.add(offlinePixelScheduler)
        pluginTotal.add(remoteMessagingConfigDownloadScheduler)
        pluginTotal.add(privacyConfigDownloadWorkerScheduler)
        pluginTotal.add(localPrivacyConfigObserver)
        pluginTotal.add(widgetAddedReceiver)
        pluginTotal.add(macOsWaitlistCodeFetcher)
        pluginTotal.add(trackerDataLoader)
        pluginTotal.add(resourceSurrogateLoader)
        pluginTotal.add(enqueuedPixelWorker)
        pluginTotal.add(notificationRegistrar)
        pluginTotal.add(androidWorkScheduler)
        pluginTotal.add(migrationLifecycleObserver)
        pluginTotal.add(appDataLoader)
        pluginTotal.add(shortcutReceiver)
        pluginTotal.add(appInstallSharedPreferences)
        pluginTotal.add(appUserStageStore)
        pluginTotal.add(tabsDbSanitizer)
        pluginTotal.add(favoritesObserver)
        pluginTotal.add(lottieFireAnimationLoader)

        Timber.d("1991 getPlugins LifecycleObserver size " + plugins.size + " plugins2 " + plugins2.size + " plugins3 " + plugins3.size + " pluginTotal " + pluginTotal.size)
        return pluginTotal
    }
}
