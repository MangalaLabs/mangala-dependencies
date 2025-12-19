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

import androidx.lifecycle.LifecycleObserver
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.fire.UnsentForgetAllPixelStore
import com.mangala.app.fire.UnsentForgetAllPixelStoreSharedPreferences
import com.mangala.app.global.events.db.*
import com.mangala.app.global.install.AppInstallSharedPreferences
import com.mangala.app.global.install.AppInstallStore
import com.mangala.app.onboarding.store.AppUserStageStore
import com.mangala.app.onboarding.store.OnboardingSharedPreferences
import com.mangala.app.onboarding.store.OnboardingStore
import com.mangala.app.onboarding.store.UserStageStore
import com.mangala.app.privacy.store.TermsOfServiceRawStore
import com.mangala.app.privacy.store.TermsOfServiceStore
import com.mangala.app.statistics.store.OfflinePixelCountDataStore
import com.mangala.app.statistics.store.OfflinePixelCountSharedPreferences
import com.mangala.app.statistics.store.StatisticsDataStore
import com.mangala.app.statistics.store.StatisticsSharedPreferences
import com.mangala.app.tabs.db.TabsDbSanitizer
import com.mangala.app.tabs.model.TabDataRepository
import com.mangala.app.tabs.model.TabRepository
import com.mangala.mobile.android.ui.store.ThemingDataStore
import com.mangala.mobile.android.ui.store.ThemingSharedPreferences
import com.mangala.app.widget.FavoritesObserver
import com.mangala.app.global.events.db.AppUserEventsStore
import com.mangala.app.global.events.db.UserEventsStore
import com.mangala.widget.AppWidgetThemePreferences
import com.mangala.widget.WidgetPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

//
//@Module
//@InstallIn(SingletonComponent::class)
//abstract class StoreModule {
//
//    @Binds
//    abstract fun bindStatisticsStore(statisticsStore: StatisticsSharedPreferences): StatisticsDataStore
//
//    @Binds
//    abstract fun bindThemingStore(themeDataStore: ThemingSharedPreferences): ThemingDataStore
//
//    @Binds
//    abstract fun bindOnboardingStore(onboardingStore: OnboardingSharedPreferences): OnboardingStore
//
//    @Binds
//    abstract fun bindTermsOfServiceStore(termsOfServiceStore: TermsOfServiceRawStore): TermsOfServiceStore
//
//    @Binds
//    abstract fun bindTabRepository(tabRepository: TabDataRepository): TabRepository
//
//    @Binds
//    abstract fun bindAppInstallStore(store: AppInstallSharedPreferences): AppInstallStore
//
//    @Binds
//    @IntoSet
//    abstract fun bindAppInstallStoreObserver(appInstallStore: AppInstallStore): LifecycleObserver
//
//    @Binds
//    abstract fun bindDataClearingStore(store: UnsentForgetAllPixelStoreSharedPreferences): UnsentForgetAllPixelStore
//
//    @Binds
//    abstract fun bindOfflinePixelDataStore(store: OfflinePixelCountSharedPreferences): OfflinePixelCountDataStore
//
//    @Binds
//    abstract fun bindUserStageStore(userStageStore: AppUserStageStore): UserStageStore
//
//    @Binds
//    @IntoSet
//    abstract fun bindUserStageStoreObserver(userStageStore: UserStageStore): LifecycleObserver
//
//    @Binds
//    abstract fun bindUserEventsStore(userEventsStore: AppUserEventsStore): UserEventsStore
//
//    @Binds
//    @IntoSet
//    abstract fun bindTabsDbSanitizerObserver(tabsDbSanitizer: TabsDbSanitizer): LifecycleObserver
//
//    @Binds
//    @IntoSet
//    abstract fun bindFavoritesObserver(favoritesObserver: FavoritesObserver): LifecycleObserver
//
//    @Binds
//    abstract fun bindWidgetPreferences(store: AppWidgetThemePreferences): WidgetPreferences
//}

val storeModule = module {
    single { StatisticsSharedPreferences(get()) as StatisticsDataStore }
    single { ThemingSharedPreferences(get()) as ThemingDataStore }
    single { OnboardingSharedPreferences(get()) as OnboardingStore }
    single { TermsOfServiceRawStore(get(), androidContext()) as TermsOfServiceStore }
    single<AppInstallStore> { AppInstallSharedPreferences(androidContext()) }
    single { UnsentForgetAllPixelStoreSharedPreferences(get()) as UnsentForgetAllPixelStore }
//    single { OfflinePixelCountSharedPreferences(get()) as OfflinePixelCountDataStore }
    single<UserStageStore> { AppUserStageStore(get(), get())  }
    single { AppUserEventsStore(get(), get()) as UserEventsStore }
    single { TabsDbSanitizer(get()) }
    single { FavoritesObserver(androidContext(), get(), get(named("AppCoroutineScope"))) }
    single { AppWidgetThemePreferences(get()) as WidgetPreferences }
}



