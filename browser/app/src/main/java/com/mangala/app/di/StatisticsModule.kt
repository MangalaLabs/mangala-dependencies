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

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import com.mangala.app.global.db.AppDatabase
import com.mangala.app.global.device.ContextDeviceInfo
import com.mangala.app.global.device.DeviceInfo
import com.mangala.app.global.exception.UncaughtExceptionRepository
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.statistics.AtbInitializer
import com.mangala.app.statistics.AtbInitializerListener
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.api.*
import com.mangala.app.statistics.config.StatisticsLibraryConfig
import com.mangala.app.statistics.pixels.RxBasedPixel
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.statistics.store.OfflinePixelCountDataStore
import com.mangala.app.statistics.store.PendingPixelDao
import com.mangala.app.statistics.store.StatisticsDataStore
import com.mangala.di.DaggerSet
import com.mangala.app.statistics.api.OfflinePixel
import com.mangala.app.statistics.api.OfflinePixelSender
import com.mangala.app.statistics.api.PixelSender
import com.mangala.app.statistics.api.PixelService
import com.mangala.app.statistics.api.RefreshRetentionAtbPlugin
import com.mangala.app.statistics.api.RxPixelSender
import com.mangala.app.statistics.api.StatisticsRequester
import com.mangala.app.statistics.api.StatisticsService
import com.mangala.app.statistics.api.StatisticsUpdater
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

//
//@Module
//@InstallIn(SingletonComponent::class)
//object StatisticsModule {
//
//    @Provides
//    fun statisticsService(@Named("api") retrofit: Retrofit): StatisticsService = retrofit.create(
//        StatisticsService::class.java)
//
//    @Provides
//    fun statisticsUpdater(
//        statisticsDataStore: StatisticsDataStore,
//        statisticsService: StatisticsService,
//        variantManager: VariantManager,
//        plugins: PluginPoint<RefreshRetentionAtbPlugin>,
//    ): StatisticsUpdater {
//        return StatisticsRequester(
//            statisticsDataStore, statisticsService, variantManager, plugins
//        )
//    }
//
//    @Provides
//    fun pixelService(@Named("nonCaching") retrofit: Retrofit): PixelService {
//        return retrofit.create(PixelService::class.java)
//    }
//
//    @Provides
//    fun pixel(
//        pixelSender: PixelSender
//    ): Pixel =
//        RxBasedPixel(pixelSender)
//
//    @Provides
//    @Singleton
//    fun pixelSender(
//        pixelService: PixelService,
//        statisticsDataStore: StatisticsDataStore,
//        variantManager: VariantManager,
//        deviceInfo: DeviceInfo,
//        pendingPixelDao: PendingPixelDao,
//        statisticsLibraryConfig: StatisticsLibraryConfig
//    ): PixelSender {
//        return RxPixelSender(pixelService, pendingPixelDao, statisticsDataStore, variantManager, deviceInfo, statisticsLibraryConfig)
//    }
//
//    @Provides
//    @Singleton
//    @IntoSet
//    fun pixelSenderObserver(pixelSender: PixelSender): LifecycleObserver = pixelSender
//
//    @Provides
//    fun offlinePixelSender(
//        offlinePixelCountDataStore: OfflinePixelCountDataStore,
//        uncaughtExceptionRepository: UncaughtExceptionRepository,
//        pixelSender: PixelSender,
//        offlinePixels: DaggerSet<OfflinePixel>
//    ): OfflinePixelSender = OfflinePixelSender(offlinePixelCountDataStore, uncaughtExceptionRepository, pixelSender, offlinePixels)
//
//    @Provides
//    fun deviceInfo(@ApplicationContext context: Context): DeviceInfo = ContextDeviceInfo(context)
//
//    @Provides
//    @IntoSet
//    @Singleton
//    fun atbInitializer(
//        @AppCoroutineScope appCoroutineScope: CoroutineScope,
//        statisticsDataStore: StatisticsDataStore,
//        statisticsUpdater: StatisticsUpdater,
//        listeners: DaggerSet<AtbInitializerListener>
//    ): LifecycleObserver {
//        return AtbInitializer(appCoroutineScope, statisticsDataStore, statisticsUpdater, listeners)
//    }
//
//    @Singleton
//    @Provides
//    fun pixelDao(database: AppDatabase): PendingPixelDao {
//        return database.pixelDao()
//    }
//}

val statisticsAppModule = module {
    single {
        (get(named("api")) as Retrofit).create(StatisticsService::class.java)
    }

    factory {
        StatisticsRequester(
            get<StatisticsDataStore>(),
            get<StatisticsService>(),
            get<VariantManager>(),
            get<PluginPoint<RefreshRetentionAtbPlugin>>()
        ) as StatisticsUpdater
    }

    single {
        (get(named("nonCaching")) as Retrofit).create(PixelService::class.java)
    }

    single {
        RxBasedPixel(get()) as Pixel
    }

    single<PixelSender> {
        RxPixelSender(
            get<PixelService>(),
            get<PendingPixelDao>(),
            get<StatisticsDataStore>(),
            get<VariantManager>(),
            get<DeviceInfo>(),
            get<StatisticsLibraryConfig>()
        )
    }


    single {
        ContextDeviceInfo(androidContext()) as DeviceInfo
    }

    single {
        AtbInitializer(
            (get(named("AppCoroutineScope")) as CoroutineScope),
            get(),
            get(),
        )
    }

    single {
        get<AppDatabase>().pixelDao() as PendingPixelDao
    }
}

