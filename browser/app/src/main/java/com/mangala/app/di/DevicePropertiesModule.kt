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
import com.mangala.app.bookmarks.model.BookmarksRepository
import com.mangala.app.bookmarks.model.FavoritesRepository
import com.mangala.app.email.EmailManager
import com.mangala.app.global.install.AppInstallStore
import com.mangala.app.global.store.AndroidAppProperties
import com.mangala.app.global.store.AndroidUserBrowserProperties
import com.mangala.app.playstore.PlayStoreUtils
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.store.StatisticsDataStore
import com.mangala.app.usage.app.AppDaysUsedRepository
import com.mangala.app.usage.search.SearchCountDao
import com.mangala.app.widget.ui.WidgetCapabilities
import com.mangala.browser.api.AppProperties
import com.mangala.browser.api.UserBrowserProperties
import com.mangala.mobile.android.ui.store.ThemingDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//object DevicePropertiesModule {
//
//    @Provides
//    @Singleton
//    fun providesAppProperties(
//        @ApplicationContext appContext: Context,
//        variantManager: VariantManager,
//        playStoreUtils: PlayStoreUtils,
//        statisticsStore: StatisticsDataStore
//    ): AppProperties {
//        return AndroidAppProperties(
//            appContext,
//            variantManager,
//            playStoreUtils,
//            statisticsStore
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun providesUserBrowserProperties(
//        themingDataStore: ThemingDataStore,
//        bookmarksRepository: BookmarksRepository,
//        favoritesRepository: FavoritesRepository,
//        appInstallStore: AppInstallStore,
//        widgetCapabilities: WidgetCapabilities,
//        emailManager: EmailManager,
//        searchCountDao: SearchCountDao,
//        appDaysUsedRepository: AppDaysUsedRepository
//    ): UserBrowserProperties {
//        return AndroidUserBrowserProperties(
//            themingDataStore,
//            bookmarksRepository,
//            favoritesRepository,
//            appInstallStore,
//            widgetCapabilities,
//            emailManager,
//            searchCountDao,
//            appDaysUsedRepository
//        )
//    }
//}

val devicePropertiesModule = module {
    single<AppProperties> {
        AndroidAppProperties(
            androidContext(),
            get(),
            get(),
            get()
        )
    }
    single<UserBrowserProperties> {
        AndroidUserBrowserProperties(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}