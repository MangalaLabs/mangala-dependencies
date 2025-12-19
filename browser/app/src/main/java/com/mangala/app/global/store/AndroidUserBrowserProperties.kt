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



package com.mangala.app.global.store

import com.mangala.app.bookmarks.model.BookmarksRepository
import com.mangala.app.bookmarks.model.FavoritesRepository
import com.mangala.app.email.EmailManager
import com.mangala.app.global.install.AppInstallStore
import com.mangala.app.global.install.daysInstalled
import com.mangala.app.usage.app.AppDaysUsedRepository
import com.mangala.app.usage.search.SearchCountDao
import com.mangala.app.widget.ui.WidgetCapabilities
import com.mangala.browser.api.UserBrowserProperties
import com.mangala.mobile.android.ui.MangalaTheme
import com.mangala.mobile.android.ui.store.ThemingDataStore
import java.util.*

class AndroidUserBrowserProperties(
    private val themingDataStore: ThemingDataStore,
    private val bookmarksRepository: BookmarksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val appInstallStore: AppInstallStore,
    private val widgetCapabilities: WidgetCapabilities,
    private val emailManager: EmailManager,
    private val searchCountDao: SearchCountDao,
    private val appDaysUsedRepository: AppDaysUsedRepository
) : UserBrowserProperties {
    override fun appTheme(): MangalaTheme {
        return themingDataStore.theme
    }

    override suspend fun bookmarks(): Long {
        return bookmarksRepository.bookmarksCount()
    }

    override suspend fun favorites(): Long {
        return favoritesRepository.favoritesCount()
    }

    override fun daysSinceInstalled(): Long {
        return appInstallStore.daysInstalled()
    }

    override suspend fun daysUsedSince(since: Date): Long {
        return appDaysUsedRepository.getNumberOfDaysAppUsedSinceDate(since)
    }

    override fun defaultBrowser(): Boolean {
        return appInstallStore.defaultBrowser
    }

    override fun emailEnabled(): Boolean {
        return emailManager.isSignedIn()
    }

    override fun searchCount(): Long {
        return searchCountDao.getSearchesMade()
    }

    override fun widgetAdded(): Boolean {
        return widgetCapabilities.hasInstalledWidgets
    }
}
