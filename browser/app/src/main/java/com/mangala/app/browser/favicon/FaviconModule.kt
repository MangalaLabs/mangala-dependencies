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

package com.mangala.app.browser.favicon

import android.content.Context
import com.mangala.app.bookmarks.db.BookmarksDao
import com.mangala.app.bookmarks.model.FavoritesRepository
import com.mangala.app.fire.fireproofwebsite.data.FireproofWebsiteRepository
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.location.data.LocationPermissionsRepository
import com.mangala.app.tabs.model.TabDataRepository
import com.mangala.app.tabs.model.TabRepository
import com.mangala.autofill.store.AutofillStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//class FaviconModule {
//
//    @Provides
//    @Singleton
//    fun faviconManager(
//        faviconPersister: FaviconPersister,
//        bookmarksDao: BookmarksDao,
//        fireproofWebsiteRepository: FireproofWebsiteRepository,
//        locationPermissionsRepository: LocationPermissionsRepository,
//        favoritesRepository: FavoritesRepository,
//        faviconDownloader: FaviconDownloader,
//        dispatcherProvider: DispatcherProvider,
//        autofillStore: AutofillStore
//    ): FaviconManager {
//        return MangalaFaviconManager(
//            faviconPersister,
//            bookmarksDao,
//            fireproofWebsiteRepository,
//            locationPermissionsRepository,
//            favoritesRepository,
//            faviconDownloader,
//            dispatcherProvider,
//            autofillStore
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun faviconDownloader(
//        @ApplicationContext context: Context,
//        dispatcherProvider: DispatcherProvider
//    ): FaviconDownloader {
//        return GlideFaviconDownloader(context, dispatcherProvider)
//    }
//}

val faviconModule = module {
    single {
        val location = LocationPermissionsRepository(get(), null, get())
//        val favManager = get<FaviconManager>()
//        if(favManager != null){
//            location.setFaviconManager(favManager)
//        }
        location
    }

    single {
        val fireproofWebsiteRepository = FireproofWebsiteRepository(get(), get(), null)
//        val favManager = get<FaviconManager>()
//        if(favManager != null){
//            fireproofWebsiteRepository.setFaviconManager(favManager)
//        }
        fireproofWebsiteRepository
    }
    single<TabRepository> {
        val tabDataRepository = TabDataRepository(get(), get(), get(), get(), get(named("AppCoroutineScope")))
//        val favManager = get<FaviconManager>()
//        if(favManager != null){
//            tabDataRepository.setFaviconManager(favManager)
//        }
        tabDataRepository
    }
    single<FaviconManager> {
        val favManager = MangalaFaviconManager(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
//        val fireproofWebsiteRepository = get<FireproofWebsiteRepository>()
//        fireproofWebsiteRepository?.setFaviconManager(favManager)
//        val tabDataRepository = get<TabRepository>()
//        (tabDataRepository as? TabDataRepository)?.setFaviconManager(favManager)
//        val locationPermissionsRepository = get<LocationPermissionsRepository>()
//        locationPermissionsRepository?.setFaviconManager(favManager)
        favManager
    }
    single<FaviconDownloader> { GlideFaviconDownloader(get(), get()) }

}
