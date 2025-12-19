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

import com.mangala.app.global.db.AppDatabase
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//object DaoModule {
//
//    @Provides
//    fun providesHttpsFalsePositivesDao(database: AppDatabase) = database.httpsFalsePositivesDao()
//
//    @Provides
//    fun provideHttpsBloomFilterSpecDao(database: AppDatabase) = database.httpsBloomFilterSpecDao()
//
//    @Provides
//    fun providesTdsTrackDao(database: AppDatabase) = database.tdsTrackerDao()
//
//    @Provides
//    fun providesTdsEntityDao(database: AppDatabase) = database.tdsEntityDao()
//
//    @Provides
//    fun providesTdsDomainEntityDao(database: AppDatabase) = database.tdsDomainEntityDao()
//
//    @Provides
//    fun providesUserWhitelist(database: AppDatabase) = database.userWhitelistDao()
//
//    @Provides
//    fun providesNetworkLeaderboardDao(database: AppDatabase) = database.networkLeaderboardDao()
//
//    @Provides
//    fun providesBookmarksDao(database: AppDatabase) = database.bookmarksDao()
//
//    @Provides
//    fun providesFavoritesDao(database: AppDatabase) = database.favoritesDao()
//
//    @Provides
//    fun providesBookmarkFoldersDao(database: AppDatabase) = database.bookmarkFoldersDao()
//
//    @Provides
//    fun providesTabsDao(database: AppDatabase) = database.tabsDao()
//
//    @Provides
//    fun surveyDao(database: AppDatabase) = database.surveyDao()
//
//    @Provides
//    fun dismissedCtaDao(database: AppDatabase) = database.dismissedCtaDao()
//
//    @Provides
//    fun searchCountDao(database: AppDatabase) = database.searchCountDao()
//
//    @Provides
//    fun appDaysUsedDao(database: AppDatabase) = database.appsDaysUsedDao()
//
//    @Provides
//    fun notification(database: AppDatabase) = database.notificationDao()
//
//    @Provides
//    fun privacyProtectionCounts(database: AppDatabase) = database.privacyProtectionCountsDao()
//
//    @Provides
//    fun uncaughtExceptionDao(database: AppDatabase) = database.uncaughtExceptionDao()
//
//    @Provides
//    fun tdsDao(database: AppDatabase) = database.tdsDao()
//
//    @Provides
//    fun userStageDao(database: AppDatabase) = database.userStageDao()
//
//    @Provides
//    fun fireproofWebsiteDao(database: AppDatabase) = database.fireproofWebsiteDao()
//
//    @Provides
//    fun userEventsDao(database: AppDatabase) = database.userEventsDao()
//
//    @Provides
//    fun locationPermissionsDao(database: AppDatabase) = database.locationPermissionsDao()
//
//    @Provides
//    fun webTrackersBlockedDao(database: AppDatabase) = database.webTrackersBlockedDao()
//
//    @Provides
//    fun allowedDomainsDao(database: AppDatabase) = database.authCookiesAllowedDomainsDao()
//}

val daoModule = module {
    single { get<AppDatabase>().httpsFalsePositivesDao() }
    single { get<AppDatabase>().httpsBloomFilterSpecDao() }
    single { get<AppDatabase>().tdsTrackerDao() }
    single { get<AppDatabase>().tdsEntityDao() }
    single { get<AppDatabase>().tdsDomainEntityDao() }
    single { get<AppDatabase>().userWhitelistDao() }
    single { get<AppDatabase>().networkLeaderboardDao() }
    single { get<AppDatabase>().bookmarksDao() }
    single { get<AppDatabase>().favoritesDao() }
    single { get<AppDatabase>().bookmarkFoldersDao() }
    single { get<AppDatabase>().tabsDao() }
    single { get<AppDatabase>().surveyDao() }
    single { get<AppDatabase>().dismissedCtaDao() }
    single { get<AppDatabase>().searchCountDao() }
    single { get<AppDatabase>().appsDaysUsedDao() }
    single { get<AppDatabase>().notificationDao() }
    single { get<AppDatabase>().privacyProtectionCountsDao() }
    single { get<AppDatabase>().uncaughtExceptionDao() }
    single { get<AppDatabase>().tdsDao() }
    single { get<AppDatabase>().userStageDao() }
    single { get<AppDatabase>().fireproofWebsiteDao() }
    single { get<AppDatabase>().userEventsDao() }
    single { get<AppDatabase>().locationPermissionsDao() }
    single { get<AppDatabase>().webTrackersBlockedDao() }
    single { get<AppDatabase>().authCookiesAllowedDomainsDao() }
    single { get<AppDatabase>().wcSignElementDao() }
    single { get<AppDatabase>().wc2SessionDao() }
    single { get<AppDatabase>().wc1SessionDao() }
}
