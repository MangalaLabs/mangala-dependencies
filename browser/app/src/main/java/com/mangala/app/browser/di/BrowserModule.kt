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

package com.mangala.app.browser.di

import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.lifecycle.LifecycleObserver
import com.alphawallet.app.C
import com.mangala.app.accessibility.AccessibilityManager
import com.mangala.app.browser.BrowserChromeClient
import com.mangala.app.browser.BrowserWebViewClient
import com.mangala.app.browser.DosDetector
import com.mangala.app.browser.MangalaRequestRewriter
import com.mangala.app.browser.MangalaUrlDetector
import com.mangala.app.browser.LongPressHandler
import com.mangala.app.browser.RequestInterceptor
import com.mangala.app.browser.RequestRewriter
import com.mangala.app.browser.SpecialUrlDetector
import com.mangala.app.browser.SpecialUrlDetectorImpl
import com.mangala.app.browser.WebDataManager
import com.mangala.app.browser.WebViewDataManager
import com.mangala.app.browser.WebViewLongPressHandler
import com.mangala.app.browser.WebViewRequestInterceptor
import com.mangala.app.browser.addtohome.AddToHomeCapabilityDetector
import com.mangala.app.browser.addtohome.AddToHomeSystemCapabilityDetector
import com.mangala.app.browser.certificates.rootstore.TrustedCertificateStore
import com.mangala.app.browser.cookies.AppThirdPartyCookieManager
import com.mangala.app.browser.cookies.CookieManagerProvider
import com.mangala.app.browser.cookies.DefaultCookieManagerProvider
import com.mangala.app.browser.cookies.ThirdPartyCookieManager
import com.mangala.app.browser.cookies.db.AuthCookiesAllowedDomainsRepository
import com.mangala.app.browser.defaultbrowsing.AndroidDefaultBrowserDetector
import com.mangala.app.browser.defaultbrowsing.DefaultBrowserDetector
import com.mangala.app.browser.defaultbrowsing.DefaultBrowserObserver
import com.mangala.app.browser.downloader.BlobConverterInjector
import com.mangala.app.browser.downloader.BlobConverterInjectorJs
import com.mangala.app.browser.favicon.FaviconPersister
import com.mangala.app.browser.favicon.FileBasedFaviconPersister
import com.mangala.app.browser.httpauth.WebViewHttpAuthStore
import com.mangala.app.browser.logindetection.BrowserTabFireproofDialogsEventHandler
import com.mangala.app.browser.logindetection.DOMLoginDetector
import com.mangala.app.browser.logindetection.FireproofDialogsEventHandler
import com.mangala.app.browser.logindetection.JsLoginDetector
import com.mangala.app.browser.logindetection.NavigationAwareLoginDetector
import com.mangala.app.browser.logindetection.NextPageLoginDetection
import com.mangala.app.browser.print.PrintInjector
import com.mangala.app.browser.serviceworker.ServiceWorkerLifecycleObserver
import com.mangala.app.browser.session.WebViewSessionInMemoryStorage
import com.mangala.app.browser.session.WebViewSessionStorage
import com.mangala.app.browser.tabpreview.FileBasedWebViewPreviewGenerator
import com.mangala.app.browser.tabpreview.FileBasedWebViewPreviewPersister
import com.mangala.app.browser.tabpreview.WebViewPreviewGenerator
import com.mangala.app.browser.tabpreview.WebViewPreviewPersister
import com.mangala.app.browser.urlextraction.DOMUrlExtractor
import com.mangala.app.browser.urlextraction.JsUrlExtractor
import com.mangala.app.browser.urlextraction.UrlExtractingWebViewClient
import com.mangala.app.browser.useragent.UserAgentInterceptor
import com.mangala.app.browser.useragent.UserAgentProvider
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.fire.AuthDatabaseLocator
import com.mangala.app.fire.CookieManagerRemover
import com.mangala.app.fire.CookieRemover
import com.mangala.app.fire.DatabaseCleaner
import com.mangala.app.fire.DatabaseCleanerHelper
import com.mangala.app.fire.DatabaseLocator
import com.mangala.app.fire.MangalaCookieManager
import com.mangala.app.fire.GetCookieHostsToPreserve
import com.mangala.app.fire.RemoveCookies
import com.mangala.app.fire.RemoveCookiesStrategy
import com.mangala.app.fire.SQLCookieRemover
import com.mangala.app.fire.WebViewCookieManager
import com.mangala.app.fire.WebViewDatabaseLocator
import com.mangala.app.fire.fireproofwebsite.data.FireproofWebsiteDao
import com.mangala.app.fire.fireproofwebsite.data.FireproofWebsiteRepository
import com.mangala.app.global.AppUrl
import com.mangala.app.global.DefaultDispatcherProvider
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.global.device.DeviceInfo
import com.mangala.app.global.events.db.UserEventsStore
import com.mangala.app.global.exception.UncaughtExceptionRepository
import com.mangala.app.global.file.FileDeleter
import com.mangala.app.global.install.AppInstallStore
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.httpsupgrade.HttpsUpgrader
import com.mangala.app.privacy.db.PrivacyProtectionCountDao
import com.mangala.app.privacy.db.UserAllowListRepository
import com.mangala.app.referral.AppReferrerDataStore
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.pixels.ExceptionPixel
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.statistics.store.OfflinePixelCountDataStore
import com.mangala.app.statistics.store.StatisticsDataStore
import com.mangala.app.surrogates.ResourceSurrogates
import com.mangala.app.tabs.ui.GridViewColumnCalculator
import com.mangala.app.trackerdetection.TrackerDetector
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.autofill.BrowserAutofill
import com.mangala.autofill.InternalTestUserChecker
import com.mangala.downloads.api.FileDownloader
import com.mangala.downloads.impl.*
import com.mangala.downloads.impl.AndroidFileDownloader
import com.mangala.downloads.impl.DataUriDownloader
import com.mangala.downloads.impl.DownloadFileService
import com.mangala.downloads.impl.NetworkFileDownloader
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.privacy.config.api.AmpLinks
import com.mangala.privacy.config.api.Gpc
import com.mangala.privacy.config.api.TrackingParameters
import com.mangala.privacy.config.api.UserAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

//@Module
//@InstallIn(SingletonComponent::class)
//class BrowserModule {

//    @Provides
//    fun mangalaRequestRewriter(
//        urlDetector: MangalaUrlDetector,
//        statisticsStore: StatisticsDataStore,
//        variantManager: VariantManager,
//        appReferrerDataStore: AppReferrerDataStore
//    ): RequestRewriter {
//        return MangalaRequestRewriter(urlDetector, statisticsStore, variantManager, appReferrerDataStore)
//    }
//
//    @Provides
//    fun browserWebViewClient(
//        @ApplicationContext context: Context,
//        webViewHttpAuthStore: WebViewHttpAuthStore,
//        trustedCertificateStore: TrustedCertificateStore,
//        requestRewriter: RequestRewriter,
//        specialUrlDetector: SpecialUrlDetector,
//        requestInterceptor: RequestInterceptor,
//        offlinePixelCountDataStore: OfflinePixelCountDataStore,
//        uncaughtExceptionRepository: UncaughtExceptionRepository,
//        cookieManagerProvider: CookieManagerProvider,
//        loginDetector: DOMLoginDetector,
//        dosDetector: DosDetector,
//        gpc: Gpc,
//        thirdPartyCookieManager: ThirdPartyCookieManager,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider,
//        accessibilityManager: AccessibilityManager,
//        browserAutofill: BrowserAutofill,
//        ampLinks: AmpLinks,
//        printInjector: PrintInjector,
//        internalTestUserChecker: InternalTestUserChecker
//    ): BrowserWebViewClient {
//        return BrowserWebViewClient(
//            context,
//            webViewHttpAuthStore,
//            trustedCertificateStore,
//            requestRewriter,
//            specialUrlDetector,
//            requestInterceptor,
//            offlinePixelCountDataStore,
//            uncaughtExceptionRepository,
//            cookieManagerProvider,
//            loginDetector,
//            dosDetector,
//            gpc,
//            thirdPartyCookieManager,
//            appCoroutineScope,
//            dispatcherProvider,
//            browserAutofill,
//            accessibilityManager,
//            ampLinks,
//            printInjector,
//            internalTestUserChecker
//        )
//    }
//
//    @Provides
//    fun urlExtractingWebViewClient(
//        webViewHttpAuthStore: WebViewHttpAuthStore,
//        trustedCertificateStore: TrustedCertificateStore,
//        requestInterceptor: RequestInterceptor,
//        cookieManagerProvider: CookieManagerProvider,
//        gpc: Gpc,
//        thirdPartyCookieManager: ThirdPartyCookieManager,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider,
//        urlExtractor: DOMUrlExtractor,
//    ): UrlExtractingWebViewClient {
//        return UrlExtractingWebViewClient(
//            webViewHttpAuthStore,
//            trustedCertificateStore,
//            requestInterceptor,
//            cookieManagerProvider,
//            gpc,
//            thirdPartyCookieManager,
//            appCoroutineScope,
//            dispatcherProvider,
//            urlExtractor,
//        )
//    }
//
//    @Provides
//    fun webViewLongPressHandler(
//        @ApplicationContext context: Context,
//        pixel: Pixel
//    ): LongPressHandler {
//        return WebViewLongPressHandler(context, pixel)
//    }
//
//    @Provides
//    fun defaultWebBrowserCapability(
//        @ApplicationContext context: Context,
//        appBuildConfig: AppBuildConfig
//    ): DefaultBrowserDetector {
//        return AndroidDefaultBrowserDetector(context, appBuildConfig)
//    }
//
//    @Provides
//    @Singleton
//    @IntoSet
//    fun defaultBrowserObserver(
//        defaultBrowserDetector: DefaultBrowserDetector,
//        appInstallStore: AppInstallStore,
//        pixel: Pixel
//    ): LifecycleObserver {
//        return DefaultBrowserObserver(defaultBrowserDetector, appInstallStore, pixel)
//    }

//    @Singleton
//    @Provides
//    fun webViewSessionStorage(): WebViewSessionStorage = WebViewSessionInMemoryStorage()
//
//    @Singleton
//    @Provides
//    fun webDataManager(
//        @ApplicationContext context: Context,
//        webViewSessionStorage: WebViewSessionStorage,
//        cookieManager: MangalaCookieManager,
//        fileDeleter: FileDeleter,
//        webViewHttpAuthStore: WebViewHttpAuthStore
//    ): WebDataManager =
//        WebViewDataManager(context, webViewSessionStorage, cookieManager, fileDeleter, webViewHttpAuthStore)
//
//    @Provides
//    fun clipboardManager(@ApplicationContext context: Context): ClipboardManager {
//        return context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//    }
//
//    @Provides
//    fun addToHomeCapabilityDetector(@ApplicationContext context: Context): AddToHomeCapabilityDetector {
//        return AddToHomeSystemCapabilityDetector(context)
//    }
//
//    @Provides
//    fun specialUrlDetector(
//        packageManager: PackageManager,
//        ampLinks: AmpLinks,
//        trackingParameters: TrackingParameters,
//        appBuildConfig: AppBuildConfig,
//    ): SpecialUrlDetector = SpecialUrlDetectorImpl(packageManager, ampLinks, trackingParameters, appBuildConfig)
//
//    @Provides
//    @Singleton
//    fun userAgentProvider(
//        @Named("defaultUserAgent") defaultUserAgent: Provider<String>,
//        deviceInfo: DeviceInfo,
//        userAgentInterceptorPluginPoint: PluginPoint<UserAgentInterceptor>,
//        userAgent: UserAgent,
//        toggle: FeatureToggle,
//        userAllowListRepository: UserAllowListRepository,
//        dispatcher: DispatcherProvider,
//    ): UserAgentProvider {
//        return UserAgentProvider(
//            defaultUserAgent,
//            deviceInfo,
//            userAgentInterceptorPluginPoint,
//            userAgent,
//            toggle,
//            userAllowListRepository,
//            dispatcher
//        )
//    }

//    @Provides
//    fun webViewRequestInterceptor(
//        resourceSurrogates: ResourceSurrogates,
//        trackerDetector: TrackerDetector,
//        httpsUpgrader: HttpsUpgrader,
//        privacyProtectionCountDao: PrivacyProtectionCountDao,
//        gpc: Gpc,
//        userAgentProvider: UserAgentProvider
//    ): RequestInterceptor =
//        WebViewRequestInterceptor(resourceSurrogates, trackerDetector, httpsUpgrader, privacyProtectionCountDao, gpc, userAgentProvider)
//
//    @Provides
//    fun cookieManager(
//        cookieManagerProvider: CookieManagerProvider,
//        removeCookies: RemoveCookies,
//        dispatcherProvider: DispatcherProvider
//    ): MangalaCookieManager {
//        return WebViewCookieManager(cookieManagerProvider, AppUrl.Url.COOKIES, removeCookies, dispatcherProvider)
//    }
//
//    @Provides
//    fun removeCookiesStrategy(
//        cookieManagerRemover: CookieManagerRemover,
//        sqlCookieRemover: SQLCookieRemover
//    ): RemoveCookies {
//        return RemoveCookies(cookieManagerRemover, sqlCookieRemover)
//    }
//
//    @Provides
//    fun sqlCookieRemover(
//        @Named("webViewDbLocator") webViewDatabaseLocator: DatabaseLocator,
//        getCookieHostsToPreserve: GetCookieHostsToPreserve,
//        offlinePixelCountDataStore: OfflinePixelCountDataStore,
//        exceptionPixel: ExceptionPixel,
//        dispatcherProvider: DispatcherProvider
//    ): SQLCookieRemover {
//        return SQLCookieRemover(webViewDatabaseLocator, getCookieHostsToPreserve, offlinePixelCountDataStore, exceptionPixel, dispatcherProvider)
//    }
//
//    @Provides
//    @Named("webViewDbLocator")
//    fun webViewDatabaseLocator(@ApplicationContext context: Context): DatabaseLocator = WebViewDatabaseLocator(context)
//
//    @Provides
//    @Named("authDbLocator")
//    fun authDatabaseLocator(@ApplicationContext context: Context): DatabaseLocator = AuthDatabaseLocator(context)

//    @Provides
//    fun databaseCleanerHelper(): DatabaseCleaner = DatabaseCleanerHelper()
//
//    @Provides
//    fun getCookieHostsToPreserve(fireproofWebsiteDao: FireproofWebsiteDao): GetCookieHostsToPreserve = GetCookieHostsToPreserve(fireproofWebsiteDao)
//
//    @Provides
//    fun cookieManagerRemover(
//        cookieManagerProvider: CookieManagerProvider
//    ): CookieManagerRemover {
//        return CookieManagerRemover(cookieManagerProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun webViewCookieManagerProvider(): CookieManagerProvider {
//        return DefaultCookieManagerProvider()
//    }
//
//    @Singleton
//    @Provides
//    fun gridViewColumnCalculator(@ApplicationContext context: Context): GridViewColumnCalculator {
//        return GridViewColumnCalculator(context)
//    }
//
//    @Singleton
//    @Provides
//    fun webViewPreviewPersister(
//        @ApplicationContext context: Context,
//        fileDeleter: FileDeleter
//    ): WebViewPreviewPersister {
//        return FileBasedWebViewPreviewPersister(context, fileDeleter)
//    }
//
//    @Singleton
//    @Provides
//    fun faviconPersister(
//        @ApplicationContext context: Context,
//        fileDeleter: FileDeleter,
//        dispatcherProvider: DispatcherProvider
//    ): FaviconPersister {
//        return FileBasedFaviconPersister(context, fileDeleter, dispatcherProvider)
//    }
//
//    @Provides
//    fun webViewPreviewGenerator(): WebViewPreviewGenerator {
//        return FileBasedWebViewPreviewGenerator()
//    }

//    @Provides
//    fun domLoginDetector(settingsDataStore: SettingsDataStore): DOMLoginDetector {
//        return JsLoginDetector(settingsDataStore)
//    }
//
//    @Provides
//    fun domUrlExtractor(): DOMUrlExtractor {
//        return JsUrlExtractor()
//    }
//
//    @Provides
//    fun blobConverterInjector(): BlobConverterInjector {
//        return BlobConverterInjectorJs()
//    }
//
//    @Provides
//    fun navigationAwareLoginDetector(
//        settingsDataStore: SettingsDataStore,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope
//    ): NavigationAwareLoginDetector {
//        return NextPageLoginDetection(settingsDataStore, appCoroutineScope)
//    }
//
//    @Provides
//    fun downloadFileService(@Named("api") retrofit: Retrofit): DownloadFileService = retrofit.create(
//        DownloadFileService::class.java
//    )
//
//    @Provides
//    fun fileDownloader(
//        dataUriDownloader: DataUriDownloader,
//        networkFileDownloader: NetworkFileDownloader
//    ): FileDownloader {
//        return AndroidFileDownloader(dataUriDownloader, networkFileDownloader)
//    }
//
//    @Provides
//    fun fireproofLoginDialogEventHandler(
//        userEventsStore: UserEventsStore,
//        pixel: Pixel,
//        fireproofWebsiteRepository: FireproofWebsiteRepository,
//        appSettingsPreferencesStore: SettingsDataStore,
//        dispatchers: DispatcherProvider
//    ): FireproofDialogsEventHandler {
//        return BrowserTabFireproofDialogsEventHandler(
//            userEventsStore,
//            pixel,
//            fireproofWebsiteRepository,
//            appSettingsPreferencesStore,
//            dispatchers
//        )
//    }
//
//    @Singleton
//    @Provides
//    fun thirdPartyCookieManager(
//        cookieManagerProvider: CookieManagerProvider,
//        authCookiesAllowedDomainsRepository: AuthCookiesAllowedDomainsRepository
//    ): ThirdPartyCookieManager {
//        return AppThirdPartyCookieManager(cookieManagerProvider, authCookiesAllowedDomainsRepository)
//    }
//
//    @Provides
//    @Singleton
//    @IntoSet
//    fun serviceWorkerLifecycleObserver(serviceWorkerLifecycleObserver: ServiceWorkerLifecycleObserver): LifecycleObserver =
//        serviceWorkerLifecycleObserver
//}


val browserModule = module {
    single {
        MangalaRequestRewriter(get(), get(), get(), get()) as RequestRewriter
    }
    single {DosDetector()}
    single {
        BrowserWebViewClient(
            androidContext(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(named("AppCoroutineScope")),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }

    single {
        UrlExtractingWebViewClient(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(named("AppCoroutineScope")),
            get(),
            get()
        ) as UrlExtractingWebViewClient
    }
    single {
        WebViewLongPressHandler(androidContext(), get()) as LongPressHandler
    }
    single {
        AndroidDefaultBrowserDetector(androidContext(), get()) as DefaultBrowserDetector
    }
    single {
        DefaultBrowserObserver(get(), get(), get())
    }

    single { WebViewSessionInMemoryStorage() as WebViewSessionStorage }
    single {
        WebViewDataManager(
            androidContext(),
            get(),
            get(),
            get(),
            get()
        ) as WebDataManager
    }
    single {
        androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    single { AddToHomeSystemCapabilityDetector(androidContext()) as AddToHomeCapabilityDetector }
    single {
        SpecialUrlDetectorImpl(
            get(),
            get(),
            get(),
            get()
        ) as SpecialUrlDetector
    }
    single {
        UserAgentProvider(
            get(named("defaultUserAgent")),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as UserAgentProvider
    }

    single {
        WebViewRequestInterceptor(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        ) as RequestInterceptor
    }
    single<MangalaCookieManager> {
        WebViewCookieManager(
            get(),
            AppUrl.Url.COOKIES,
            get(),
            get()
        )
    }
    single<RemoveCookiesStrategy> { RemoveCookies(get(named("cookieManagerRemover")), get(named("sqlCookieRemover"))) }
    single { ExceptionPixel(get(), get())}
    single(named("sqlCookieRemover")) {
        SQLCookieRemover(
            get(named("webViewDbLocator")),
            get(),
            get(),
            get(),
            get()
        ) as CookieRemover
    }
    single(named("cookieManagerRemover")){ CookieManagerRemover(get()) as CookieRemover }

    single(named("webViewDbLocator")) { WebViewDatabaseLocator(androidContext()) as DatabaseLocator }
    single(named("authDbLocator")) { AuthDatabaseLocator(androidContext()) as DatabaseLocator }

    single<DatabaseCleaner> { DatabaseCleanerHelper()}
    single { GetCookieHostsToPreserve(get()) }

    single<CookieManagerProvider> { DefaultCookieManagerProvider() }
    single { GridViewColumnCalculator(androidContext()) as GridViewColumnCalculator }
    single {
        FileBasedWebViewPreviewPersister(
            androidContext(),
            get()
        ) as WebViewPreviewPersister
    }
    single {
        FileBasedFaviconPersister(
            androidContext(),
            get(),
            get()
        ) as FaviconPersister
    }
    single { FileBasedWebViewPreviewGenerator() as WebViewPreviewGenerator }

    single { JsLoginDetector(get()) as DOMLoginDetector }
    single { JsUrlExtractor() as DOMUrlExtractor }
    single { BlobConverterInjectorJs() as BlobConverterInjector }
    single { NextPageLoginDetection(get(), get(named("AppCoroutineScope")), get()) as NavigationAwareLoginDetector }
    single { get<Retrofit>(named("api")).create(DownloadFileService::class.java) }
    single { AndroidFileDownloader(get(), get()) as FileDownloader }
    single { DataUriDownloader(get()) }
    single { DataUriParser() }
    single { NetworkFileDownloader(androidContext(), get(), get()) }
    single { FilenameExtractor(get()) }
    single {
        BrowserTabFireproofDialogsEventHandler(
            get(),
            get(),
            get(),
            get(),
            get()
        ) as FireproofDialogsEventHandler
    }
    single { AppThirdPartyCookieManager(get(), get()) as ThirdPartyCookieManager }
    single { ServiceWorkerLifecycleObserver(get(), get()) }
    single(named("AppCoroutineScope")) { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    single {AuthCookiesAllowedDomainsRepository(get(), get())}
    single { BrowserChromeClient(get(), get(), get(), get(named("AppCoroutineScope")), get()) }
}
