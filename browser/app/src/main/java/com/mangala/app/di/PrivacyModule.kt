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
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import com.mangala.app.browser.WebDataManager
import com.mangala.app.browser.cookies.ThirdPartyCookieManager
import com.mangala.app.fire.*
import com.mangala.app.fire.fireproofwebsite.data.FireproofWebsiteRepository
import com.mangala.app.fire.AndroidAppCacheClearer
import com.mangala.app.fire.AppCacheClearer
import com.mangala.app.fire.BackgroundTimeKeeper
import com.mangala.app.fire.DataClearerForegroundAppRestartPixel
import com.mangala.app.fire.DataClearerTimeKeeper
import com.mangala.app.fire.MangalaCookieManager
import com.mangala.app.fire.UnsentForgetAllPixelStore
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.global.file.FileDeleter
import com.mangala.app.global.view.ClearDataAction
import com.mangala.app.global.view.ClearPersonalDataAction
import com.mangala.app.location.GeoLocationPermissions
import com.mangala.app.location.GeoLocationPermissionsManager
import com.mangala.app.location.data.LocationPermissionsRepository
import com.mangala.app.privacy.model.PrivacyPractices
import com.mangala.app.privacy.model.PrivacyPracticesImpl
import com.mangala.app.privacy.store.TermsOfServiceStore
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.tabs.model.TabRepository
import com.mangala.app.trackerdetection.EntityLookup
import com.mangala.app.trackerdetection.TdsEntityLookup
import com.mangala.app.trackerdetection.db.TdsDomainEntityDao
import com.mangala.app.trackerdetection.db.TdsEntityDao
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//
//
//@Module
//@InstallIn(SingletonComponent::class)
//object PrivacyModule {
//
//    @Provides
//    @Singleton
//    fun privacyPractices(
//        termsOfServiceStore: TermsOfServiceStore,
//        entityLookup: EntityLookup
//    ): PrivacyPractices =
//        PrivacyPracticesImpl(termsOfServiceStore, entityLookup)
//
//    @Provides
//    @Singleton
//    fun entityLookup(
//        entityDao: TdsEntityDao,
//        domainEntityDao: TdsDomainEntityDao
//    ): EntityLookup =
//        TdsEntityLookup(entityDao, domainEntityDao)
//
//    @Provides
//    fun clearDataAction(
//        @ApplicationContext context: Context,
//        dataManager: WebDataManager,
//        clearingStore: UnsentForgetAllPixelStore,
//        tabRepository: TabRepository,
//        settingsDataStore: SettingsDataStore,
//        cookieManager: MangalaCookieManager,
//        appCacheClearer: AppCacheClearer,
//        geoLocationPermissions: GeoLocationPermissions,
//        thirdPartyCookieManager: ThirdPartyCookieManager
//    ): ClearDataAction {
//        return ClearPersonalDataAction(
//            context,
//            dataManager,
//            clearingStore,
//            tabRepository,
//            settingsDataStore,
//            cookieManager,
//            appCacheClearer,
//            geoLocationPermissions,
//            thirdPartyCookieManager
//        )
//    }
//
//    @Provides
//    fun backgroundTimeKeeper(): BackgroundTimeKeeper {
//        return DataClearerTimeKeeper()
//    }
//
//    @Provides
//    @Singleton
//    @IntoSet
//    fun dataClearerForegroundAppRestartPixelObserver(
//        dataClearerForegroundAppRestartPixel: DataClearerForegroundAppRestartPixel
//    ): LifecycleObserver = dataClearerForegroundAppRestartPixel
//
//    @Provides
//    @Singleton
//    fun appCacheCleaner(
//        @ApplicationContext context: Context,
//        fileDeleter: FileDeleter
//    ): AppCacheClearer {
//        return AndroidAppCacheClearer(context, fileDeleter)
//    }
//
//    @Provides
//    @Singleton
//    fun geoLocationPermissions(
//        @ApplicationContext context: Context,
//        locationPermissionsRepository: LocationPermissionsRepository,
//        fireproofWebsiteRepository: FireproofWebsiteRepository,
//        dispatcherProvider: DispatcherProvider
//    ): GeoLocationPermissions {
//        return GeoLocationPermissionsManager(context, locationPermissionsRepository, fireproofWebsiteRepository, dispatcherProvider)
//    }
//}

val privacyModule = module {
    single {
        PrivacyPracticesImpl(
            get<TermsOfServiceStore>(),
            get<EntityLookup>()
        ) as PrivacyPractices
    }

    single {
        TdsEntityLookup(
            get<TdsEntityDao>(),
            get<TdsDomainEntityDao>()
        ) as EntityLookup
    }

    factory {
        ClearPersonalDataAction(
            androidContext(),
            get<WebDataManager>(),
            get<UnsentForgetAllPixelStore>(),
            get<TabRepository>(),
            get<SettingsDataStore>(),
            get<MangalaCookieManager>(),
            get<AppCacheClearer>(),
            get<GeoLocationPermissions>(),
            get<ThirdPartyCookieManager>()
        ) as ClearDataAction
    }

    single {
        DataClearerTimeKeeper() as BackgroundTimeKeeper
    }

    single {
        DataClearerForegroundAppRestartPixel(androidContext(), get())
    }

    single {
        AndroidAppCacheClearer(
            androidContext(),
            get<FileDeleter>()
        ) as AppCacheClearer
    }

    single {
        GeoLocationPermissionsManager(
            androidContext(),
            get<LocationPermissionsRepository>(),
            get<FireproofWebsiteRepository>(),
            get<DispatcherProvider>()
        ) as GeoLocationPermissions
    }
}
