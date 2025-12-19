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


package com.mangala.remote.messaging.impl.di

import android.content.Context
import androidx.room.Room
import com.mangala.app.global.AppUrl
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.app.global.DispatcherProvider
import com.mangala.browser.api.AppProperties
import com.mangala.browser.api.UserBrowserProperties
import com.mangala.di.DaggerSet
import com.mangala.remote.messaging.impl.RealRemoteMessagingConfigDownloader
import com.mangala.remote.messaging.impl.RemoteMessagingConfigDownloader
import com.mangala.remote.messaging.api.RemoteMessagingRepository
import com.mangala.remote.messaging.impl.*
import com.mangala.remote.messaging.impl.mappers.RemoteMessagingConfigJsonMapper
import com.mangala.remote.messaging.impl.matchers.AndroidAppAttributeMatcher
import com.mangala.remote.messaging.impl.matchers.AttributeMatcher
import com.mangala.remote.messaging.impl.matchers.DeviceAttributeMatcher
import com.mangala.remote.messaging.impl.matchers.UserAttributeMatcher
import com.mangala.remote.messaging.impl.network.RemoteMessagingService
import com.mangala.remote.messaging.impl.AppRemoteMessagingRepository
import com.mangala.remote.messaging.impl.RealRemoteMessagingConfigProcessor
import com.mangala.remote.messaging.impl.RemoteMessagingConfigMatcher
import com.mangala.remote.messaging.impl.RemoteMessagingConfigProcessor
import com.mangala.remote.messaging.store.ALL_MIGRATIONS
import com.mangala.remote.messaging.store.LocalRemoteMessagingConfigRepository
import com.mangala.remote.messaging.store.RemoteMessagesDao
import com.mangala.remote.messaging.store.RemoteMessagingConfigRepository
import com.mangala.remote.messaging.store.RemoteMessagingDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import dagger.multibindings.IntoSet
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


//@Module
//@InstallIn(SingletonComponent::class)
//object DomainModule {
//
//    @Provides
//    fun providesRemoteMessagingConfigDownloader(
//        remoteConfig: RemoteMessagingService,
//        remoteMessagingConfigProcessor: RemoteMessagingConfigProcessor
//    ): RemoteMessagingConfigDownloader {
//        return RealRemoteMessagingConfigDownloader(remoteConfig, remoteMessagingConfigProcessor)
//    }
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//
//    @Provides
//    @Singleton
//    fun apiRetrofit(@Named("api") okHttpClient: OkHttpClient): RemoteMessagingService {
//        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(AppUrl.Url.API)
//            .client(okHttpClient)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//        return retrofit.create(RemoteMessagingService::class.java)
//    }
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//object DataSourceModule {
//
//    @Provides
//    @Singleton
//    fun providesRemoteMessagingConfigProcessor(
//        remoteMessagingConfigJsonMapper: RemoteMessagingConfigJsonMapper,
//        remoteMessagingConfigRepository: RemoteMessagingConfigRepository,
//        remoteMessagingRepository: RemoteMessagingRepository,
//        remoteMessagingConfigMatcher: RemoteMessagingConfigMatcher
//    ): RemoteMessagingConfigProcessor {
//        return RealRemoteMessagingConfigProcessor(
//            remoteMessagingConfigJsonMapper,
//            remoteMessagingConfigRepository,
//            remoteMessagingRepository,
//            remoteMessagingConfigMatcher
//        )
//    }
//
//    @Provides
//    fun providesRemoteMessagingRepository(
//        remoteMessagingConfigRepository: RemoteMessagingConfigRepository,
//        remoteMessagesDao: RemoteMessagesDao,
//        dispatchers: DispatcherProvider
//    ): RemoteMessagingRepository {
//        return AppRemoteMessagingRepository(remoteMessagingConfigRepository, remoteMessagesDao, dispatchers)
//    }
//
//    @Provides
//    @Singleton
//    fun providesRemoteMessagesDao(
//        remoteMessagingDatabase: RemoteMessagingDatabase
//    ): RemoteMessagesDao {
//        return remoteMessagingDatabase.remoteMessagesDao()
//    }
//
//    @Provides
//    @Singleton
//    fun providesRemoteMessagingConfigJsonMapper(
//        appBuildConfig: AppBuildConfig
//    ): RemoteMessagingConfigJsonMapper {
//        return RemoteMessagingConfigJsonMapper(appBuildConfig)
//    }
//
//    @Provides
//    @Singleton
//    fun providesRemoteMessagingConfigMatcher(
//        matchers: DaggerSet<AttributeMatcher>,
//        remoteMessagingRepository: RemoteMessagingRepository,
//    ): RemoteMessagingConfigMatcher {
//        return RemoteMessagingConfigMatcher(matchers, remoteMessagingRepository)
//    }
//
//    @Provides
//    @IntoSet
//    fun providesAndroidAppAttributeMatcher(
//        appProperties: AppProperties,
//        appBuildConfig: AppBuildConfig
//    ): AttributeMatcher {
//        return AndroidAppAttributeMatcher(appProperties, appBuildConfig)
//    }
//
//    @Provides
//    @IntoSet
//    fun providesDeviceAttributeMatcher(
//        appBuildConfig: AppBuildConfig,
//        appProperties: AppProperties
//    ): AttributeMatcher {
//        return DeviceAttributeMatcher(appBuildConfig, appProperties)
//    }
//
//    @Provides
//    @IntoSet
//    fun providesUserAttributeMatcher(
//        userBrowserProperties: UserBrowserProperties
//    ): AttributeMatcher {
//        return UserAttributeMatcher(userBrowserProperties)
//    }
//
//    @Provides
//    fun providesRemoteMessagingConfigRepository(database: RemoteMessagingDatabase): RemoteMessagingConfigRepository {
//        return LocalRemoteMessagingConfigRepository(database)
//    }
//
//    @Provides
//    @Singleton
//    fun providesRemoteMessagingDatabase(@ApplicationContext context: Context): RemoteMessagingDatabase {
//        return Room.databaseBuilder(context, RemoteMessagingDatabase::class.java, "remote_messaging.db")
//            .enableMultiInstanceInvalidation()
//            .fallbackToDestructiveMigration()
//            .addMigrations(*ALL_MIGRATIONS)
//            .build()
//    }
//}

val remoteDomainModule = module {
    single<RemoteMessagingConfigDownloader> {
        val remoteConfig: RemoteMessagingService = get()
        val remoteMessagingConfigProcessor: RemoteMessagingConfigProcessor = get()
        RealRemoteMessagingConfigDownloader(remoteConfig, remoteMessagingConfigProcessor)
    }
}

val remoteNetworkModule = module {
    single<RemoteMessagingService> {
        val okHttpClient: OkHttpClient = get(named("api"))

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(AppUrl.Url.API)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        retrofit.create(RemoteMessagingService::class.java)
    }
}

val dataSourceModule = module {
    single<RemoteMessagingConfigProcessor> {
        val remoteMessagingConfigJsonMapper: RemoteMessagingConfigJsonMapper = get()
        val remoteMessagingConfigRepository: RemoteMessagingConfigRepository = get()
        val remoteMessagingRepository: RemoteMessagingRepository = get()
        val remoteMessagingConfigMatcher: RemoteMessagingConfigMatcher = get()

        RealRemoteMessagingConfigProcessor(
            remoteMessagingConfigJsonMapper,
            remoteMessagingConfigRepository,
            remoteMessagingRepository,
            remoteMessagingConfigMatcher
        )
    }

    single<RemoteMessagingRepository> {
        val remoteMessagingConfigRepository: RemoteMessagingConfigRepository = get()
        val remoteMessagesDao: RemoteMessagesDao = get()
        val dispatchers: DispatcherProvider = get()

        AppRemoteMessagingRepository(remoteMessagingConfigRepository, remoteMessagesDao, dispatchers)
    }

    single<RemoteMessagesDao> {
        get<RemoteMessagingDatabase>().remoteMessagesDao()
    }

    single<RemoteMessagingConfigJsonMapper> {
        val appBuildConfig: AppBuildConfig = get()
        RemoteMessagingConfigJsonMapper(appBuildConfig)
    }

    single<RemoteMessagingConfigMatcher> {
        val matchers: Set<AttributeMatcher> = get()
        val remoteMessagingRepository: RemoteMessagingRepository = get()

        RemoteMessagingConfigMatcher(matchers, remoteMessagingRepository)
    }

    single<AttributeMatcher>(named("AndroidAppAttributeMatcher")) {
        val appProperties: AppProperties = get()
        val appBuildConfig: AppBuildConfig = get()

        AndroidAppAttributeMatcher(appProperties, appBuildConfig)
    }

    single<AttributeMatcher>(named("DeviceAttributeMatcher")) {
        val appBuildConfig: AppBuildConfig = get()
        val appProperties: AppProperties = get()

        DeviceAttributeMatcher(appBuildConfig, appProperties)
    }

    single<AttributeMatcher>(named("UserAttributeMatcher")) {
        val userBrowserProperties: UserBrowserProperties = get()
        UserAttributeMatcher(userBrowserProperties)
    }

    single<RemoteMessagingConfigRepository> {
        val database: RemoteMessagingDatabase = get()
        LocalRemoteMessagingConfigRepository(database)
    }

    single<RemoteMessagingDatabase> {
        val context = androidContext()

        Room.databaseBuilder(context, RemoteMessagingDatabase::class.java, "remote_messaging.db")
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .addMigrations(*ALL_MIGRATIONS)
            .build()
    }
}
