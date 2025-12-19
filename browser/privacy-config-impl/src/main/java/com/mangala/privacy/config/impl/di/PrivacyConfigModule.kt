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


package com.mangala.privacy.config.impl.di

import android.content.Context
import androidx.room.Room
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.AppUrl
import com.mangala.app.global.DispatcherProvider
import com.mangala.di.scopes.FragmentScope
import com.mangala.privacy.config.api.*
import com.mangala.privacy.config.api.AmpLinks
import com.mangala.privacy.config.api.Autofill
import com.mangala.privacy.config.api.ContentBlocking
import com.mangala.privacy.config.api.Drm
import com.mangala.privacy.config.api.Gpc
import com.mangala.privacy.config.api.Https
import com.mangala.privacy.config.api.TrackerAllowlist
import com.mangala.privacy.config.api.TrackingParameters
import com.mangala.privacy.config.api.UserAgent
import com.mangala.privacy.config.impl.PrivacyConfigDownloader
import com.mangala.privacy.config.impl.PrivacyConfigPersister
import com.mangala.privacy.config.impl.RealPrivacyConfigDownloader
import com.mangala.privacy.config.impl.RealPrivacyConfigPersister
import com.mangala.privacy.config.impl.features.amplinks.RealAmpLinks
import com.mangala.privacy.config.impl.features.autofill.RealAutofill
import com.mangala.privacy.config.impl.features.contentblocking.RealContentBlocking
import com.mangala.privacy.config.impl.features.drm.RealDrm
import com.mangala.privacy.config.impl.features.gpc.RealGpc
import com.mangala.privacy.config.impl.features.https.RealHttps
import com.mangala.privacy.config.impl.features.trackerallowlist.RealTrackerAllowlist
import com.mangala.privacy.config.impl.features.trackingparameters.RealTrackingParameters
import com.mangala.privacy.config.impl.features.unprotectedtemporary.RealUnprotectedTemporary
import com.mangala.privacy.config.impl.features.unprotectedtemporary.UnprotectedTemporary
import com.mangala.privacy.config.impl.features.useragent.RealUserAgent
import com.mangala.privacy.config.impl.network.JSONObjectAdapter
import com.mangala.privacy.config.impl.network.PrivacyConfigService
import com.mangala.privacy.config.store.ALL_MIGRATIONS
import com.mangala.privacy.config.store.PrivacyConfigDatabase
import com.mangala.privacy.config.store.PrivacyConfigRepository
import com.mangala.privacy.config.store.PrivacyFeatureTogglesDataStore
import com.mangala.privacy.config.store.PrivacyFeatureTogglesRepository
import com.mangala.privacy.config.store.PrivacyFeatureTogglesSharedPreferences
import com.mangala.privacy.config.store.RealPrivacyConfigRepository
import com.mangala.privacy.config.store.RealPrivacyFeatureTogglesRepository
import com.mangala.privacy.config.store.features.autofill.AutofillRepository
import com.mangala.privacy.config.store.features.autofill.RealAutofillRepository
import com.mangala.privacy.config.store.features.contentblocking.ContentBlockingRepository
import com.mangala.privacy.config.store.features.contentblocking.RealContentBlockingRepository
import com.mangala.privacy.config.store.features.drm.DrmRepository
import com.mangala.privacy.config.store.features.drm.RealDrmRepository
import com.mangala.privacy.config.store.features.gpc.GpcDataStore
import com.mangala.privacy.config.store.features.gpc.GpcRepository
import com.mangala.privacy.config.store.features.gpc.GpcSharedPreferences
import com.mangala.privacy.config.store.features.gpc.RealGpcRepository
import com.mangala.privacy.config.store.features.https.HttpsRepository
import com.mangala.privacy.config.store.features.https.RealHttpsRepository
import com.mangala.privacy.config.store.features.trackerallowlist.RealTrackerAllowlistRepository
import com.mangala.privacy.config.store.features.trackerallowlist.TrackerAllowlistRepository
import com.mangala.privacy.config.store.features.amplinks.RealAmpLinksRepository
import com.mangala.privacy.config.store.features.amplinks.AmpLinksRepository
import com.mangala.privacy.config.store.features.trackingparameters.RealTrackingParametersRepository
import com.mangala.privacy.config.store.features.trackingparameters.TrackingParametersRepository
import com.mangala.privacy.config.store.features.unprotectedtemporary.RealUnprotectedTemporaryRepository
import com.mangala.privacy.config.store.features.unprotectedtemporary.UnprotectedTemporaryRepository
import com.mangala.privacy.config.store.features.useragent.RealUserAgentRepository
import com.mangala.privacy.config.store.features.useragent.UserAgentRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import dagger.Binds
//import dagger.WrongScope
//import dagger.*
import kotlinx.coroutines.CoroutineScope
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
//import javax.inject.Named
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
//import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//
//    @Provides
//    @Singleton
//    fun apiRetrofit(@Named("api") okHttpClient: OkHttpClient): PrivacyConfigService {
//        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).add(JSONObjectAdapter()).build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(AppUrl.Url.API)
//            .client(okHttpClient)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//        return retrofit.create(PrivacyConfigService::class.java)
//    }
//}

val privacyNetworkModule = module {
    single {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(JSONObjectAdapter())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(AppUrl.Url.API)
            .client(get<OkHttpClient>(named("api")))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        retrofit.create(PrivacyConfigService::class.java)
    }
}

//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    @Singleton
//    @Provides
//    fun providePrivacyConfigDatabase(@ApplicationContext context: Context): PrivacyConfigDatabase {
//        return Room.databaseBuilder(context, PrivacyConfigDatabase::class.java, "privacy_config.db")
//            .enableMultiInstanceInvalidation()
//            .fallbackToDestructiveMigration()
//            .addMigrations(*ALL_MIGRATIONS)
//            .build()
//    }

//    @Singleton
//    @Provides
//    fun providePrivacyConfigRepository(database: PrivacyConfigDatabase): PrivacyConfigRepository {
//        return RealPrivacyConfigRepository(database)
//    }

//    @Singleton
//    @Provides
//    fun providePTrackerAllowlistRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): TrackerAllowlistRepository {
//        return RealTrackerAllowlistRepository(database, coroutineScope, dispatcherProvider)
//    }

//    @Singleton
//    @Provides
//    fun provideContentBlockingRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): ContentBlockingRepository {
//        return RealContentBlockingRepository(database, coroutineScope, dispatcherProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun provideGpcDataStore(@ApplicationContext context: Context): GpcDataStore {
//        return GpcSharedPreferences(context)
//    }
//
//    @Singleton
//    @Provides
//    fun providePrivacyFeatureTogglesDataStore(@ApplicationContext context: Context): PrivacyFeatureTogglesDataStore {
//        return PrivacyFeatureTogglesSharedPreferences(context)
//    }

//    @Singleton
//    @Provides
//    fun provideGpcRepository(
//        gpcDataStore: GpcDataStore,
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): GpcRepository {
//        return RealGpcRepository(gpcDataStore, database, coroutineScope, dispatcherProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun provideHttpsRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): HttpsRepository {
//        return RealHttpsRepository(database, coroutineScope, dispatcherProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun provideUnprotectedTemporaryRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): UnprotectedTemporaryRepository {
//        return RealUnprotectedTemporaryRepository(database, coroutineScope, dispatcherProvider)
//    }

//    @Singleton
//    @Provides
//    fun providePrivacyFeatureTogglesRepository(privacyFeatureTogglesDataStore: PrivacyFeatureTogglesDataStore): PrivacyFeatureTogglesRepository {
//        return RealPrivacyFeatureTogglesRepository(privacyFeatureTogglesDataStore)
//    }
//
//    @Singleton
//    @Provides
//    fun provideDrmRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): DrmRepository {
//        return RealDrmRepository(database, coroutineScope, dispatcherProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun provideAmpLinksRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): AmpLinksRepository {
//        return RealAmpLinksRepository(database, coroutineScope, dispatcherProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun provideTrackingParametersRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): TrackingParametersRepository {
//        return RealTrackingParametersRepository(database, coroutineScope, dispatcherProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun provideAutofillRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): AutofillRepository {
//        return RealAutofillRepository(database, coroutineScope, dispatcherProvider)
//    }
//
//    @Singleton
//    @Provides
//    fun provideUserAgentRepository(
//        database: PrivacyConfigDatabase,
//        @AppCoroutineScope coroutineScope: CoroutineScope,
//        dispatcherProvider: DispatcherProvider
//    ): UserAgentRepository {
//        return RealUserAgentRepository(database, coroutineScope, dispatcherProvider)
//    }
//}

val privacyDatabaseModule = module {
    single {
        Room.databaseBuilder(get(), PrivacyConfigDatabase::class.java, "privacy_config.db")
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }
    single<PrivacyConfigRepository> { RealPrivacyConfigRepository(get()) }
    single {
        RealTrackerAllowlistRepository(get(), get(named("AppCoroutineScope")), get()) as TrackerAllowlistRepository
    }
    single {
        RealContentBlockingRepository(get(), get(named("AppCoroutineScope")), get()) as ContentBlockingRepository
    }

    single { GpcSharedPreferences(androidContext()) as GpcDataStore }

    single { PrivacyFeatureTogglesSharedPreferences(androidContext()) as PrivacyFeatureTogglesDataStore }

    single {
        RealGpcRepository(get(), get(), get(named("AppCoroutineScope")), get()) as GpcRepository
    }

    single {
        RealHttpsRepository(get(), get(named("AppCoroutineScope")), get()) as HttpsRepository
    }

    single {
        RealUnprotectedTemporaryRepository(get(), get(named("AppCoroutineScope")), get()) as UnprotectedTemporaryRepository
    }

    single {
        RealPrivacyFeatureTogglesRepository(get()) as PrivacyFeatureTogglesRepository
    }

    single {
        RealDrmRepository(get(), get(named("AppCoroutineScope")), get()) as DrmRepository
    }

    single {
        RealAmpLinksRepository(get(), get(named("AppCoroutineScope")), get()) as AmpLinksRepository
    }

    single {
        RealTrackingParametersRepository(get(), get(named("AppCoroutineScope")), get()) as TrackingParametersRepository
    }

    single {
        RealAutofillRepository(get(), get(named("AppCoroutineScope")), get()) as AutofillRepository
    }

    single {
        RealUserAgentRepository(get(), get(named("AppCoroutineScope")), get()) as UserAgentRepository
    }
}

//@Module
//@InstallIn(SingletonComponent::class)
//interface Binding {
//    @Binds
//    fun bindRealPrivacyConfigDownloader(realPrivacyConfigDownloader: RealPrivacyConfigDownloader): PrivacyConfigDownloader
//
//    @Binds
//    @Singleton
//    fun bindRealHttps(realHttps: RealHttps): Https
//
//    @Binds
//    fun bindRealUserAgent(realUserAgent: RealUserAgent): UserAgent
//
//    @Binds
//    @Singleton
//    fun bindRealUnprotectedTemporary(realUnprotectedTemporary: RealUnprotectedTemporary): UnprotectedTemporary
//
//    @WrongScope("This should be one instance per BrowserTabFragment", FragmentScope::class)
//    @Binds
//    @Singleton
//    fun bindRealTrackingParameters(realTrackingParameters: RealTrackingParameters): TrackingParameters
//
//    @Binds
//    @Singleton
//    fun bindRealTrackerAllowlist(realTrackerAllowlist: RealTrackerAllowlist): TrackerAllowlist
//
//    @Binds
//    @Singleton
//    fun bindRealPrivacyConfigPersister(realPrivacyConfigPersister: RealPrivacyConfigPersister): PrivacyConfigPersister
//
//    @Binds
//    @Singleton
//    fun bindRealAmpLinks(realAmpLinks: RealAmpLinks): AmpLinks
//
//    @Binds
//    @Singleton
//    fun bindRealContentBlocking(realContentBlocking: RealContentBlocking): ContentBlocking
//
//    @Binds
//    @Singleton
//    fun bindRealGpc(realGpc: RealGpc): Gpc
//
//    @Binds
//    @Singleton
//    fun bindRealAutofill(realAutofill: RealAutofill): Autofill
//
//    @Binds
//    @Singleton
//    fun bindRealDrm(realDrm: RealDrm): Drm
//}

val privacyBindingModule = module {
    single<PrivacyConfigDownloader> { RealPrivacyConfigDownloader(get(), get()) }
    single<Https> { RealHttps(get(),get())}
    single<UserAgent> { RealUserAgent(get(), get()) }
    single<UnprotectedTemporary> { RealUnprotectedTemporary(get())}
    // For the 'RealTrackingParameters' definition, Koin doesn't natively support custom scopes.
    // You may need to handle this manually or check the latest version of Koin for possible new features.
    single<TrackingParameters> { RealTrackingParameters(get(), get(), get(), get()) }
    single<TrackerAllowlist> { RealTrackerAllowlist(get(), get()) }
    single<PrivacyConfigPersister> { RealPrivacyConfigPersister(get(), get(), get(), get(), get()) }
    single<AmpLinks> { RealAmpLinks(get(), get(), get()) }
    single<ContentBlocking> { RealContentBlocking(get(), get(), get()) }
    single<Gpc> { RealGpc(androidContext(), get(), get(), get()) }
    single<Autofill> { RealAutofill(get(), get()) }
    single<Drm> { RealDrm(get(), get()) }
}
