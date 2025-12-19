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

package com.mangala.app.bookmarks.di

import android.content.Context
import com.mangala.app.bookmarks.db.BookmarkFoldersDao
import com.mangala.app.bookmarks.db.BookmarksDao
import com.mangala.app.bookmarks.db.FavoritesDao
import com.mangala.app.bookmarks.model.BookmarksDataRepository
import com.mangala.app.bookmarks.model.BookmarksRepository
import com.mangala.app.bookmarks.model.FavoritesDataRepository
import com.mangala.app.bookmarks.model.FavoritesRepository
import com.mangala.app.bookmarks.service.RealSavedSitesExporter
import com.mangala.app.bookmarks.service.RealSavedSitesImporter
import com.mangala.app.bookmarks.service.RealSavedSitesManager
import com.mangala.app.bookmarks.service.RealSavedSitesParser
import com.mangala.app.bookmarks.service.SavedSitesExporter
import com.mangala.app.bookmarks.service.SavedSitesImporter
import com.mangala.app.bookmarks.service.SavedSitesManager
import com.mangala.app.bookmarks.service.SavedSitesParser
import com.mangala.app.bookmarks.ui.BookmarksViewModel
import com.mangala.app.bookmarks.ui.bookmarkfolders.BookmarkFoldersViewModel
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.global.db.AppDatabase
import com.mangala.app.statistics.pixels.Pixel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//class BookmarksModule {
//
//    @Provides
//    @Singleton
//    fun savedSitesImporter(
//        @ApplicationContext context: Context,
//        bookmarksDao: BookmarksDao,
//        favoritesRepository: FavoritesRepository,
//        bookmarksRepository: BookmarksRepository,
//        savedSitesParser: SavedSitesParser,
//    ): SavedSitesImporter {
//        return RealSavedSitesImporter(context.contentResolver, bookmarksDao, favoritesRepository, bookmarksRepository, savedSitesParser)
//    }
//
//    @Provides
//    @Singleton
//    fun savedSitesParser(): SavedSitesParser {
//        return RealSavedSitesParser()
//    }
//
//    @Provides
//    @Singleton
//    fun savedSitesExporter(
//        @ApplicationContext context: Context,
//        savedSitesParser: SavedSitesParser,
//        favoritesRepository: FavoritesRepository,
//        bookmarksRepository: BookmarksRepository,
//        dispatcherProvider: DispatcherProvider
//    ): SavedSitesExporter {
//        return RealSavedSitesExporter(context.contentResolver, favoritesRepository, bookmarksRepository, savedSitesParser, dispatcherProvider)
//    }
//
//    @Provides
//    @Singleton
//    fun bookmarkManager(
//        savedSitesImporter: SavedSitesImporter,
//        savedSitesExporter: SavedSitesExporter,
//        pixel: Pixel
//    ): SavedSitesManager {
//        return RealSavedSitesManager(savedSitesImporter, savedSitesExporter, pixel)
//    }
//
//    @Provides
//    @Singleton
//    fun favoriteRepository(
//        favoritesDao: FavoritesDao,
//        faviconManager: Lazy<FaviconManager>
//    ): FavoritesRepository {
//        return FavoritesDataRepository(favoritesDao, faviconManager)
//    }
//
//    @Provides
//    @Singleton
//    fun bookmarkFoldersRepository(
//        bookmarkFoldersDao: BookmarkFoldersDao,
//        bookmarksDao: BookmarksDao,
//        appDatabase: AppDatabase
//    ): BookmarksRepository {
//        return BookmarksDataRepository(bookmarkFoldersDao, bookmarksDao, appDatabase)
//    }
//}


val bookmarksModule = module {
    single<SavedSitesParser> { RealSavedSitesParser()  }
    single<SavedSitesImporter> {
        RealSavedSitesImporter(androidContext().contentResolver, get(), get(), get(), get())
    }
    single<SavedSitesExporter> {
        RealSavedSitesExporter(androidContext().contentResolver, get(), get(), get(), get())
    }
    single<SavedSitesManager> { RealSavedSitesManager(get(), get(), get())}
    single<FavoritesRepository> { FavoritesDataRepository(get(), null) }
    single<BookmarksRepository> { BookmarksDataRepository(get(), get(), get()) }
    viewModel {BookmarkFoldersViewModel(get(), get())}
    viewModel { BookmarksViewModel(get(), get(), get(), get(), get(), get()) }
}
