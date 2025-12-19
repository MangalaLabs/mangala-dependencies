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



package com.mangala.app.di

import androidx.lifecycle.LifecycleObserver
import androidx.webkit.ServiceWorkerClientCompat
import com.mangala.app.autocomplete.api.AutoComplete
import com.mangala.app.autocomplete.api.AutoCompleteApi
import com.mangala.app.autofill.FileBasedJavascriptInjector
import com.mangala.app.autofill.JavascriptInjector
import com.mangala.app.brokensite.BrokenSiteViewModel
import com.mangala.app.browser.BrowserTabViewModel
import com.mangala.app.browser.BrowserViewModel
import com.mangala.app.browser.DefaultWebViewVersionProvider
import com.mangala.app.browser.WebViewCompatWebViewVersionSource
import com.mangala.app.browser.WebViewVersionProvider
import com.mangala.app.browser.WebViewVersionSource
import com.mangala.app.browser.applinks.AppLinksHandler
import com.mangala.app.browser.applinks.MangalaAppLinksHandler
import com.mangala.app.browser.httpauth.RealWebViewHttpAuthStore
import com.mangala.app.browser.httpauth.WebViewHttpAuthStore
import com.mangala.app.browser.omnibar.OmnibarEntryConverter
import com.mangala.app.browser.omnibar.OmnibarScrolling
import com.mangala.app.browser.omnibar.QueryUrlConverter
import com.mangala.app.browser.print.PrintInjector
import com.mangala.app.browser.print.PrintInjectorJS
import com.mangala.app.browser.remotemessage.RemoteMessagingModel
import com.mangala.app.browser.serviceworker.BrowserServiceWorkerClient
import com.mangala.app.buildconfig.RealAppBuildConfig
import com.mangala.app.cta.ui.CtaViewModel
import com.mangala.app.downloads.DownloadsAdapter
import com.mangala.app.downloads.DownloadsViewModel
import com.mangala.app.email.ui.EmailProtectionSignInViewModel
import com.mangala.app.email.ui.EmailProtectionSignOutViewModel
import com.mangala.app.email.ui.EmailProtectionViewModel
import com.mangala.app.email.ui.EmailWebViewViewModel
import com.mangala.app.feedback.ui.common.FeedbackViewModel
import com.mangala.app.feedback.ui.initial.InitialFeedbackFragmentViewModel
import com.mangala.app.feedback.ui.negative.brokensite.BrokenSiteNegativeFeedbackViewModel
import com.mangala.app.feedback.ui.negative.openended.ShareOpenEndedNegativeFeedbackViewModel
import com.mangala.app.feedback.ui.positive.initial.PositiveFeedbackLandingViewModel
//import com.mangala.app.dev.settings.db.DevSettingsDataStore
//import com.mangala.app.dev.settings.db.DevSettingsSharedPreferences
import com.mangala.app.fire.AutomaticDataClearer
import com.mangala.app.fire.DataClearer
import com.mangala.app.fire.fireproofwebsite.ui.FireproofWebsitesViewModel
import com.mangala.app.global.migrations.MigrationSharedPreferences
import com.mangala.app.global.migrations.MigrationStore
import com.mangala.app.global.model.SiteFactory
import com.mangala.app.httpsupgrade.HttpsBloomFilterFactory
import com.mangala.app.httpsupgrade.HttpsBloomFilterFactoryImpl
import com.mangala.app.httpsupgrade.HttpsUpgrader
import com.mangala.app.httpsupgrade.HttpsUpgraderImpl
import com.mangala.app.httpsupgrade.store.HttpsDataPersister
import com.mangala.app.job.AndroidJobCleaner
import com.mangala.app.job.JobCleaner
import com.mangala.app.launch.LaunchViewModel
import com.mangala.app.location.ui.LocationPermissionsViewModel
import com.mangala.app.notification.NotificationRepository
import com.mangala.app.notification.RealTaskStackBuilderFactory
import com.mangala.app.notification.TaskStackBuilderFactory
import com.mangala.app.notification.db.RealNotificationRepository
import com.mangala.app.privacy.db.RealUserAllowListRepository
import com.mangala.app.privacy.db.UserAllowListRepository
import com.mangala.app.privacy.ui.PrivacyDashboardViewModel
import com.mangala.app.privacy.ui.PrivacyPracticesViewModel
import com.mangala.app.privacy.ui.ScorecardViewModel
import com.mangala.app.privacy.ui.TrackerNetworksViewModel
import com.mangala.app.privacy.ui.WhitelistViewModel
import com.mangala.app.referral.AppInstallationReferrerParser
import com.mangala.app.referral.AppReferenceSharePreferences
import com.mangala.app.referral.AppReferrerDataStore
import com.mangala.app.referral.QueryParamReferrerParser
import com.mangala.app.settings.SettingsViewModel
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.settings.db.SettingsSharedPreferences
import com.mangala.app.systemsearch.SystemSearchViewModel
import com.mangala.app.tabs.ui.TabSwitcherViewModel
import com.mangala.app.trackerdetection.TrackerDetector
import com.mangala.app.trackerdetection.TrackerDetectorImpl
import com.mangala.app.trackerdetection.api.WebTrackersBlockedAppRepository
import com.mangala.app.trackerdetection.api.WebTrackersBlockedRepository
import com.mangala.app.ui.GlobalPrivacyControlViewModel
import com.mangala.app.userwhitelist.UserWhiteListAppRepository
import com.mangala.app.userwhitelist.api.UserWhiteListRepository
import com.mangala.app.widget.AddWidgetCompatLauncher
import com.mangala.app.widget.AddWidgetLauncher
import com.mangala.app.widget.AppWidgetManagerAddWidgetLauncher
import com.mangala.app.widget.LegacyAddWidgetLauncher
import com.mangala.app.widget.ui.AddWidgetInstructionsViewModel
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.schoolonair.wallet.local.walletconnect.WC1SessionStorage
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//interface BindingModule {
//
//    @Binds
//    fun bindDefaultWebViewVersionProvider(defaultWebViewVersionProvider: DefaultWebViewVersionProvider): WebViewVersionProvider
//
//    @Binds
//    fun bindQueryUrlConverter(queryUrlConverter: QueryUrlConverter): OmnibarEntryConverter
//
//    @Binds
//    fun bindFileBasedJavascriptInjector(fileBasedJavascriptInjector: FileBasedJavascriptInjector): JavascriptInjector
//
//    @Binds
//    fun bindWebViewCompatWebViewVersionSource(webViewCompatWebViewVersionSource: WebViewCompatWebViewVersionSource): WebViewVersionSource
//
//    @Binds
//    fun bindAutoCompleteApi(autoCompleteApi: AutoCompleteApi): AutoComplete
//
//    @Binds
//    fun bindRealAppBuildConfig(realAppBuildConfig: RealAppBuildConfig): AppBuildConfig
    
//    @Binds
//    fun bindDevSettingsSharedPreferences(devSettingsSharedPreferences: DevSettingsSharedPreferences): DevSettingsDataStore
    
//    @Binds
//    fun bindRealTaskStackBuilderFactory(realTaskStackBuilderFactory: RealTaskStackBuilderFactory): TaskStackBuilderFactory
//
//    @Binds
//    fun bindRealUserAllowListRepository(realUserAllowListRepository: RealUserAllowListRepository): UserAllowListRepository
//
//    @Binds
//    fun bindWebTrackersBlockedAppRepository(webTrackersBlockedAppRepository: WebTrackersBlockedAppRepository): WebTrackersBlockedRepository
//
//    @Binds
//    @Singleton
//    fun bindTrackerDetectorImpl(trackerDetectorImpl: TrackerDetectorImpl): TrackerDetector
//
//    @Binds
//    @Singleton
//    fun bindRealNotificationRepository(realNotificationRepository: RealNotificationRepository): NotificationRepository
//
//    @Binds
//    fun bindUserWhiteListAppRepository(userWhiteListAppRepository: UserWhiteListAppRepository): UserWhiteListRepository
    
//    @Binds
//    fun bindPrintInjectorJS(printInjectorJs: PrintInjectorJS): PrintInjector
//
//    @Binds
//    fun bindMangalaAppLinksHandler(mangalaAppLinksHandler: MangalaAppLinksHandler): AppLinksHandler
//
//    @Binds
//    fun bindHttpsBloomFilterFactoryImpl(httpsBloomFilterFactoryImpl: HttpsBloomFilterFactoryImpl): HttpsBloomFilterFactory
//
//    @Binds
//    fun bindAddWidgetCompatLauncher(addWidgetCompatLauncher: AddWidgetCompatLauncher): AddWidgetLauncher
//
//    @Binds
//    @Singleton
//    fun bindBrowserServiceWorkerClient(browserServiceWorkerClient: BrowserServiceWorkerClient): ServiceWorkerClientCompat
//
//    @Binds
//    @Singleton
//    fun bindMigrationSharedPreferences(migrationSharedPreferences: MigrationSharedPreferences): MigrationStore
//
//    @Binds
//    @Singleton
//    fun bindAndroidJobCleaner(androidJobCleaner: AndroidJobCleaner): JobCleaner
    
//    @Binds
//    fun bindQueryParamReferrerParser(queryParamReferrerParser: QueryParamReferrerParser): AppInstallationReferrerParser
//
//    @Binds
//    @Singleton
//    fun bindAppReferenceSharePreferences(appReferenceSharePreferences: AppReferenceSharePreferences): AppReferrerDataStore
//
//    @Binds
//    fun bindSettingsSharedPreferences(settingsSharedPreferences: SettingsSharedPreferences): SettingsDataStore
//
//    @Binds
//    @Named("legacyAddWidgetLauncher")
//    fun bindLegacyAddWidgetLauncher(legacyAddWidgetLauncher: LegacyAddWidgetLauncher): AddWidgetLauncher
//
//    @Binds
//    @Named("appWidgetManagerAddWidgetLauncher")
//    fun bindAppWidgetManagerAddWidgetLauncher(appWidgetManagerAddWidgetLauncher: AppWidgetManagerAddWidgetLauncher): AddWidgetLauncher
//
//    @Binds
//    @Singleton
//    fun bindHttpsUpgraderImpl(httpsUpgraderImpl: HttpsUpgraderImpl): HttpsUpgrader
//
//    @Binds
//    @Singleton
//    fun bindRealWebViewHttpAuthStore(realWebViewHttpAuthStore: RealWebViewHttpAuthStore): WebViewHttpAuthStore
//
//    @Binds
//    @Singleton
//    fun bindAutomaticDataClearer(automaticDataClearer: AutomaticDataClearer): DataClearer
//}


val bindingModule = module {
    // Define the BindingModule
    single<WebViewVersionProvider> { DefaultWebViewVersionProvider(get()) }
    single<OmnibarEntryConverter> { QueryUrlConverter(get()) }
    single<JavascriptInjector> { FileBasedJavascriptInjector() }
    single<WebViewVersionSource> { WebViewCompatWebViewVersionSource(androidContext()) }
    single<AutoComplete> { AutoCompleteApi(get(), get(), get()) }
    single<AppBuildConfig> { RealAppBuildConfig() }
    single<TaskStackBuilderFactory> { RealTaskStackBuilderFactory(androidContext()) }
    single<UserAllowListRepository> { RealUserAllowListRepository(get()) }
    single<WebTrackersBlockedRepository> { WebTrackersBlockedAppRepository(get()) }
    single<TrackerDetector> { TrackerDetectorImpl(get(), get(), get(), get(), get()) }
    single<NotificationRepository> { RealNotificationRepository(get()) }
    single<UserWhiteListRepository> { UserWhiteListAppRepository(get(), get(named("AppCoroutineScope")), get()) }
    single<PrintInjector> { PrintInjectorJS() }
    single<AppLinksHandler> { MangalaAppLinksHandler(get()) }
    single { HttpsDataPersister(get(), get(), get(), get()) }
    single<HttpsBloomFilterFactory> { HttpsBloomFilterFactoryImpl(get(), get(), get(), get(), get()) }
    single<AddWidgetLauncher> { AddWidgetCompatLauncher(get(), get(), get()) }
    single<ServiceWorkerClientCompat> { BrowserServiceWorkerClient(get(), get()) }
    single<MigrationStore> { MigrationSharedPreferences(androidContext()) }
    single<JobCleaner> { AndroidJobCleaner(get()) }

    single<AppInstallationReferrerParser> { QueryParamReferrerParser() }
    single<AppReferrerDataStore> { AppReferenceSharePreferences(androidContext()) }
    single<SettingsDataStore> { SettingsSharedPreferences(androidContext(), get()) }
    single(named("legacyAddWidgetLauncher")) { LegacyAddWidgetLauncher() }
    single(named("appWidgetManagerAddWidgetLauncher")) { AppWidgetManagerAddWidgetLauncher() }
    factory <HttpsUpgrader> { HttpsUpgraderImpl(get(), get(), get(), get(), get()) }
    factory <LifecycleObserver> { HttpsUpgraderImpl(get(), get(), get(), get(), get()) }
    single<WebViewHttpAuthStore> { RealWebViewHttpAuthStore(get(), get(), get(named("authDbLocator")), get(), get(named("AppCoroutineScope")), get()) }

    single { DownloadsAdapter(get()) }
    single { OmnibarScrolling() }
    viewModel { BrokenSiteViewModel(get(), get(), get()) }
    viewModel { DownloadsViewModel(get(), get(), get(), androidContext()) }
    viewModel { EmailProtectionSignInViewModel(get(), get(), get(), get(), get()) }
    viewModel { BrowserViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { EmailProtectionSignOutViewModel(get()) }
    viewModel { EmailProtectionViewModel(get()) }
    viewModel { EmailWebViewViewModel(get()) }
    viewModel { InitialFeedbackFragmentViewModel() }
    viewModel { BrokenSiteNegativeFeedbackViewModel() }
    viewModel { ShareOpenEndedNegativeFeedbackViewModel() }
    viewModel { PositiveFeedbackLandingViewModel() }
    viewModel { AddWidgetInstructionsViewModel() }
    viewModel { LaunchViewModel(get(), get()) }
    viewModel { TabSwitcherViewModel(get(), get()) }
    viewModel { WhitelistViewModel(get(), get(named("AppCoroutineScope")), get()) }
    viewModel { GlobalPrivacyControlViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { SystemSearchViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { PrivacyPracticesViewModel(get()) }
    viewModel { TrackerNetworksViewModel(get()) }
    viewModel { LocationPermissionsViewModel(get(), get(), get(), get(), get()) }
    viewModel { PrivacyDashboardViewModel(get(), get(), get(), get(), get(named("AppCoroutineScope")), get()) }
    viewModel { FireproofWebsitesViewModel(get(), get(), get(), get(), get()) }
    viewModel { FeedbackViewModel(get(), get(), get(named("AppCoroutineScope")), get(), get()) }
    viewModel { ScorecardViewModel(get(), get(), get(), get()) }
    single { CtaViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single { RemoteMessagingModel(get(), get(), get()) }
    single { WC1SessionStorage(get()) }
    viewModel { BrowserTabViewModel(
        get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        get(), get(), get(), get(), get(), get(), get(), get(), get(), get(),
        get(), get(), get(named("AppCoroutineScope")), get(), get(), get(), get(), get(), get(), get(),
        get(), get(), get(), get()) }

    single { SiteFactory(get(), get()) }
}