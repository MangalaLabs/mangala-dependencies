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

import androidx.lifecycle.LifecycleObserver
import com.mangala.app.browser.downloader.FileDownloadBroadcastReceiver
import com.mangala.app.browser.httpauth.RealWebViewHttpAuthStore
import com.mangala.app.browser.shortcut.ShortcutReceiver
//import com.mangala.app.dev.settings.privacy.TrackerDataDevReceiverRegister
//import com.mangala.app.flipper.FlipperInitializer
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
import com.mangala.privacy.config.impl.observers.LocalPrivacyConfigObserver
import com.mangala.privacy.config.impl.workers.PrivacyConfigDownloadWorkerScheduler
import com.mangala.remote.messaging.impl.RemoteMessagingConfigDownloadScheduler
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module


//@Module
//@InstallIn(SingletonComponent::class)
//interface LifecycleObserverPluginPointModule {
//
//    @Multibinds
//    fun browserLifecycleObserver(): DaggerSet<LifecycleObserver>
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindHttpsUpgraderImpl(httpsUpgraderImpl: HttpsUpgraderImpl): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindRealWebViewHttpAuthStore(realWebViewHttpAuthStore: RealWebViewHttpAuthStore): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindFileDownloadBroadcastReceiver(fileDownloadBroadcastReceiver: FileDownloadBroadcastReceiver): LifecycleObserver

//    @Binds
//    @IntoSet
//    fun bindAppStartUpTracer(appStartUpTracer: AppStartUpTracer): LifecycleObserver
//
//
//    @Binds
//    @IntoSet
//    fun bindOfflinePixelScheduler(offlinePixelScheduler: OfflinePixelScheduler): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    fun bindRemoteMessagingConfigDownloadScheduler(remoteMessagingConfigDownloadScheduler: RemoteMessagingConfigDownloadScheduler): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindPrivacyConfigDownloadWorkerScheduler(privacyConfigDownloadWorkerScheduler: PrivacyConfigDownloadWorkerScheduler): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindLocalPrivacyConfigObserver(localPrivacyConfigObserver: LocalPrivacyConfigObserver): LifecycleObserver
//
//    @Binds
//    @Singleton
//    @IntoSet
//    fun bindWidgetAddedReceiver(widgetAddedReceiver: WidgetAddedReceiver): LifecycleObserver

//    @Binds
//    @Singleton
//    @IntoSet
//    fun bindMacOsWaitlistCodeFetcher(macOsWaitlistCodeFetcher: MacOsWaitlistCodeFetcher): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    fun bindTrackerDataLoader(trackerDataLoader: TrackerDataLoader): LifecycleObserver

//    @Binds
//    @IntoSet
//    fun bindTrackerDataDevReceiverRegister(trackerDataDevReceiverRegister: TrackerDataDevReceiverRegister): LifecycleObserver

//    @Binds
//    @IntoSet
//    fun bindResourceSurrogateLoader(resourceSurrogateLoader: ResourceSurrogateLoader): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindEnqueuedPixelWorker(enqueuedPixelWorker: EnqueuedPixelWorker): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    fun bindNotificationRegistrar(notificationRegistrar: NotificationRegistrar): LifecycleObserver
//
//    @Binds
//    @Singleton
//    @IntoSet
//    fun bindAndroidWorkScheduler(androidWorkScheduler: AndroidWorkScheduler): LifecycleObserver
//
//    @Binds
//    @Singleton
//    @IntoSet
//    fun bindMigrationLifecycleObserver(migrationLifecycleObserver: MigrationLifecycleObserver): LifecycleObserver

//    @Binds
//    @IntoSet
//    fun bindAppDataLoader(appDataLoader: AppDataLoader): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindShortcutReceiver(shortcutReceiver: ShortcutReceiver): LifecycleObserver

//    @Binds
//    @IntoSet
//    fun bindFlipperInitializer(flipperInitializer: FlipperInitializer): LifecycleObserver

//    @Binds
//    fun bindLifecycleObserverPluginPoint(lifecycleObserverPluginPoint: LifecycleObserverPluginPoint): PluginPoint<LifecycleObserver>
//}

val lifecycleObserverPluginPointModule = module {

    // Define the singletons
    single { HttpsUpgraderImpl(get(), get(), get(), get(), get()) }
    single { RealWebViewHttpAuthStore(get(), get(), get(named("authDbLocator")), get(), get(named("AppCoroutineScope")), get()) }
    single { FileDownloadBroadcastReceiver(androidContext(), get(), get(), get(named("AppCoroutineScope"))) }
    single { AppStartUpTracer() }
    single { OfflinePixelScheduler(get()) }
    single { RemoteMessagingConfigDownloadScheduler(get()) }
    single { PrivacyConfigDownloadWorkerScheduler(get()) }
    single { LocalPrivacyConfigObserver(androidContext(), get(), get(named("AppCoroutineScope")), get()) }
    single { WidgetAddedReceiver(androidContext()) }
    single { MacOsWaitlistCodeFetcher(get(), get(), get(), get(), get(), get(named("AppCoroutineScope"))) }
    single { TrackerDataLoader(get(named("AppCoroutineScope")), get(), get(), get(), get(), get(), androidContext(), get(), get()) }

    single { ResourceSurrogateLoader(get(named("AppCoroutineScope")), get(), get()) }
    single { EnqueuedPixelWorker(get(), get(), get(), get()) }
    single { NotificationRegistrar(get(named("AppCoroutineScope")), androidContext(), get(), get(), get(), get(), get(), get()) }
    single { AndroidWorkScheduler(get(named("AppCoroutineScope")), get(), get()) }
    single { MigrationLifecycleObserver(get(), get()) }

    single { AppDataLoader(get(named("AppCoroutineScope")), get(), get()) }
    single { ShortcutReceiver(androidContext(), get(), get(), get(named("AppCoroutineScope"))) }

    single {
        LifecycleObserverPluginPoint()
    }

}
