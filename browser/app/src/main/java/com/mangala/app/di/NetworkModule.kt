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

package com.mangala.app.di

import android.content.Context
import com.mangala.app.autocomplete.api.AutoCompleteService
import com.mangala.app.brokensite.api.BrokenSiteSender
import com.mangala.app.brokensite.api.BrokenSiteSubmitter
import com.mangala.app.browser.useragent.UserAgentProvider
import com.mangala.app.di.NetworkModule.Companion.CACHE_SIZE
import com.mangala.app.email.api.EmailService
import com.mangala.app.feedback.api.FeedbackService
import com.mangala.app.feedback.api.FeedbackSubmitter
import com.mangala.app.feedback.api.FireAndForgetFeedbackSubmitter
import com.mangala.app.feedback.api.SubReasonApiMapper
import com.mangala.app.global.AppUrl.Url
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.global.api.*
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.global.plugins.pixel.PixelInterceptorPlugin
import com.mangala.app.httpsupgrade.api.HttpsUpgradeService
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.statistics.store.StatisticsDataStore
import com.mangala.app.surrogates.api.ResourceSurrogateListService
import com.mangala.app.survey.api.SurveyService
import com.mangala.app.trackerdetection.api.TrackerListService
import com.mangala.app.trackerdetection.db.TdsMetadataDao
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.app.global.api.ApiInterceptorPlugin
import com.mangala.app.global.api.ApiRequestInterceptor
import com.mangala.app.global.api.NetworkApiCache
import com.mangala.app.global.api.PixelEmailRemovalInterceptor
import com.mangala.app.global.api.PixelReQueryInterceptor
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.privacy.config.api.Gpc
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.io.File

//@Module
//@InstallIn(SingletonComponent::class)
//class NetworkModule {
//
//    @Provides
//    @Singleton
//    @Named("api")
//    fun apiOkHttpClient(
//        @ApplicationContext context: Context,
//        apiRequestInterceptor: ApiRequestInterceptor,
//        apiInterceptorPlugins: PluginPoint<ApiInterceptorPlugin>
//    ): OkHttpClient {
//        val cacheLocation = File(context.cacheDir, NetworkApiCache.FILE_NAME)
//        val cache = Cache(cacheLocation, CACHE_SIZE)
//        return OkHttpClient.Builder()
//            .addInterceptor(apiRequestInterceptor)
//            .cache(cache).apply {
//                apiInterceptorPlugins.getPlugins().forEach {
//                    addInterceptor(it.getInterceptor())
//                }
//            }
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    @Named("nonCaching")
//    fun pixelOkHttpClient(
//        apiRequestInterceptor: ApiRequestInterceptor,
//        pixelReQueryInterceptor: PixelReQueryInterceptor,
//        pixelEmailRemovalInterceptor: PixelEmailRemovalInterceptor,
//        pixelInterceptorPlugins: PluginPoint<PixelInterceptorPlugin>,
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(apiRequestInterceptor)
//            .addInterceptor(pixelReQueryInterceptor)
//            .addInterceptor(pixelEmailRemovalInterceptor)
//            .apply {
//                pixelInterceptorPlugins.getPlugins().forEach { addInterceptor(it.getInterceptor()) }
//            }
//            // shall be the last one as it is logging the pixel request url that goes out
//            .addInterceptor { chain: Interceptor.Chain ->
//                Timber.v("Pixel url request: ${chain.request().url}")
//                return@addInterceptor chain.proceed(chain.request())
//            }
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    @Named("api")
//    fun apiRetrofit(
//        @Named("api") okHttpClient: OkHttpClient,
//        moshi: Moshi
//    ): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(Url.API)
//            .client(okHttpClient)
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    @Named("nonCaching")
//    fun nonCachingRetrofit(
//        @Named("nonCaching") okHttpClient: OkHttpClient,
//        moshi: Moshi
//    ): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(Url.API)
//            .client(okHttpClient)
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//    }
//
//    @Provides
//    fun apiRequestInterceptor(
//        @ApplicationContext context: Context,
//        userAgentProvider: UserAgentProvider,
//        appBuildConfig: AppBuildConfig
//    ): ApiRequestInterceptor {
//        return ApiRequestInterceptor(context, userAgentProvider, appBuildConfig)
//    }
//
//    @Provides
//    fun pixelReQueryInterceptor(): PixelReQueryInterceptor {
//        return PixelReQueryInterceptor()
//    }
//
//    @Provides
//    fun pixelEmailRemovalInterceptor(): PixelEmailRemovalInterceptor {
//        return PixelEmailRemovalInterceptor()
//    }
//
//    @Provides
//    fun trackerListService(@Named("api") retrofit: Retrofit): TrackerListService =
//        retrofit.create(TrackerListService::class.java)
//
//    @Provides
//    fun httpsUpgradeService(@Named("api") retrofit: Retrofit): HttpsUpgradeService =
//        retrofit.create(HttpsUpgradeService::class.java)
//
//    @Provides
//    fun autoCompleteService(@Named("nonCaching") retrofit: Retrofit): AutoCompleteService =
//        retrofit.create(AutoCompleteService::class.java)
//
//    @Provides
//    fun emailService(@Named("nonCaching") retrofit: Retrofit): EmailService =
//        retrofit.create(EmailService::class.java)
//
//    @Provides
//    fun surrogatesService(@Named("api") retrofit: Retrofit): ResourceSurrogateListService =
//        retrofit.create(ResourceSurrogateListService::class.java)
//
//    @Provides
//    fun brokenSiteSender(
//        statisticsStore: StatisticsDataStore,
//        variantManager: VariantManager,
//        tdsMetadataDao: TdsMetadataDao,
//        pixel: Pixel,
//        gpc: Gpc,
//        featureToggle: FeatureToggle,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope,
//        appBuildConfig: AppBuildConfig,
//        dispatcherProvider: DispatcherProvider
//    ): BrokenSiteSender =
//        BrokenSiteSubmitter(
//            statisticsStore, variantManager, tdsMetadataDao, gpc, featureToggle,
//            pixel, appCoroutineScope, appBuildConfig, dispatcherProvider
//        )
//
//    @Provides
//    fun surveyService(@Named("api") retrofit: Retrofit): SurveyService =
//        retrofit.create(SurveyService::class.java)
//
//    @Provides
//    fun feedbackSubmitter(
//        feedbackService: FeedbackService,
//        variantManager: VariantManager,
//        apiKeyMapper: SubReasonApiMapper,
//        statisticsStore: StatisticsDataStore,
//        pixel: Pixel,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope,
//        appBuildConfig: AppBuildConfig
//    ): FeedbackSubmitter =
//        FireAndForgetFeedbackSubmitter(feedbackService, variantManager, apiKeyMapper, statisticsStore, pixel, appCoroutineScope, appBuildConfig)
//
//    @Provides
//    fun feedbackService(@Named("api") retrofit: Retrofit): FeedbackService =
//        retrofit.create(FeedbackService::class.java)
//
//
//}

class NetworkModule{
    companion object {
        const val CACHE_SIZE: Long = 10 * 1024 * 1024 // 10MB
    }
}

val networkModule = module {
    single(named("api")) {
        val context: Context = androidContext()
        val apiRequestInterceptor: ApiRequestInterceptor = get()
        val apiInterceptorPlugins: PluginPoint<ApiInterceptorPlugin> = get()
        val cacheLocation = File(context.cacheDir, NetworkApiCache.FILE_NAME)
        val cache = Cache(cacheLocation, CACHE_SIZE)
        OkHttpClient.Builder()
            .addInterceptor(apiRequestInterceptor)
            .cache(cache).apply {
                apiInterceptorPlugins.getPlugins().forEach {
                    addInterceptor(it.getInterceptor())
                }
            }
            .build()
    }

    single(named("nonCaching")) {
        val apiRequestInterceptor: ApiRequestInterceptor = get()
        val pixelReQueryInterceptor: PixelReQueryInterceptor = get()
        val pixelEmailRemovalInterceptor: PixelEmailRemovalInterceptor = get()
        val pixelInterceptorPlugins: PluginPoint<PixelInterceptorPlugin> = get()
        OkHttpClient.Builder()
            .addInterceptor(apiRequestInterceptor)
            .addInterceptor(pixelReQueryInterceptor)
            .addInterceptor(pixelEmailRemovalInterceptor)
            .apply {
                pixelInterceptorPlugins.getPlugins().forEach { addInterceptor(it.getInterceptor()) }
            }
            .addInterceptor { chain: Interceptor.Chain ->
                Timber.v("Pixel url request: ${chain.request().url}")
                return@addInterceptor chain.proceed(chain.request())
            }
            .build()
    }

    single(named("api")) {
        val okHttpClient: OkHttpClient = get(named("api"))
        val moshi: Moshi = get()
        Retrofit.Builder()
            .baseUrl(Url.API)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    single(named("nonCaching")) {
        val okHttpClient: OkHttpClient = get(named("nonCaching"))
        val moshi: Moshi = get()
        Retrofit.Builder()
            .baseUrl(Url.API)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    factory {
        val context: Context = androidContext()
        val userAgentProvider: UserAgentProvider = get()
        val appBuildConfig: AppBuildConfig = get()
        ApiRequestInterceptor(context, userAgentProvider, appBuildConfig)
    }

    factory { PixelReQueryInterceptor() }

    factory { PixelEmailRemovalInterceptor() }

    factory<TrackerListService> { (get(named("api")) as Retrofit).create(TrackerListService::class.java) }

    factory<HttpsUpgradeService> { (get(named("api")) as Retrofit).create(HttpsUpgradeService::class.java) }

    factory<AutoCompleteService> { (get(named("nonCaching"))as Retrofit).create(AutoCompleteService::class.java) }

    factory<EmailService> { (get(named("nonCaching")) as Retrofit).create(EmailService::class.java) }

    factory<ResourceSurrogateListService> { (get(named("api")) as Retrofit).create(ResourceSurrogateListService::class.java) }

    factory<BrokenSiteSender> {
        val statisticsStore: StatisticsDataStore = get()
        val variantManager: VariantManager = get()
        val tdsMetadataDao: TdsMetadataDao = get()
        val pixel: Pixel = get()
        val gpc: Gpc = get()
        val featureToggle: FeatureToggle = get()
        val appCoroutineScope: CoroutineScope = get(named("AppCoroutineScope"))
        val appBuildConfig: AppBuildConfig = get()
        val dispatcherProvider: DispatcherProvider = get()

        BrokenSiteSubmitter(
            statisticsStore, variantManager, tdsMetadataDao, gpc, featureToggle,
            pixel, appCoroutineScope, appBuildConfig, dispatcherProvider
        )
    }

    factory<SurveyService> { (get(named("api")) as Retrofit).create(SurveyService::class.java) }

    factory<FeedbackSubmitter> {
        val feedbackService: FeedbackService = get()
        val variantManager: VariantManager = get()
        val apiKeyMapper: SubReasonApiMapper = get()
        val statisticsStore: StatisticsDataStore = get()
        val pixel: Pixel = get()
        val appCoroutineScope: CoroutineScope = get(named("AppCoroutineScope"))
        val appBuildConfig: AppBuildConfig = get()

        FireAndForgetFeedbackSubmitter(
            feedbackService, variantManager, apiKeyMapper, statisticsStore, pixel,
            appCoroutineScope, appBuildConfig
        )
    }

    factory<FeedbackService> { (get(named("api")) as Retrofit).create(FeedbackService::class.java) }
}
