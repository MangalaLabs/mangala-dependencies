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
import android.content.pm.PackageManager
import androidx.lifecycle.LifecycleObserver
import com.mangala.app.fire.FireAnimationLoader
import com.mangala.app.fire.LottieFireAnimationLoader
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.systemsearch.DeviceAppListProvider
import com.mangala.app.systemsearch.DeviceAppLookup
import com.mangala.app.systemsearch.InstalledDeviceAppListProvider
import com.mangala.app.systemsearch.InstalledDeviceAppLookup
import com.mangala.app.di.AppCoroutineScope
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

//
//@Module
//@InstallIn(SingletonComponent::class)
//object SystemComponentsModule {
//
//    @Singleton
//    @Provides
//    fun packageManager(@ApplicationContext context: Context): PackageManager = context.packageManager
//
//    @Singleton
//    @Provides
//    fun deviceAppsListProvider(packageManager: PackageManager): DeviceAppListProvider = InstalledDeviceAppListProvider(packageManager)
//
//    @Provides
//    @Singleton
//    fun deviceAppLookup(deviceAppListProvider: DeviceAppListProvider): DeviceAppLookup = InstalledDeviceAppLookup(deviceAppListProvider)
//
//    @Provides
//    fun animatorLoader(
//        @ApplicationContext context: Context,
//        settingsDataStore: SettingsDataStore,
//        dispatcherProvider: DispatcherProvider,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope
//    ): FireAnimationLoader {
//        return LottieFireAnimationLoader(context, settingsDataStore, dispatcherProvider, appCoroutineScope)
//    }
//}

//@Module
//@InstallIn(SingletonComponent::class)
//abstract class SystemComponentsModuleBindings {
//    @Binds
//    @IntoSet
//    abstract fun animatorLoaderObserver(fireAnimationLoader: FireAnimationLoader): LifecycleObserver
//}


val systemComponentsModule = module {
    single { androidContext().packageManager }
    single { InstalledDeviceAppListProvider(get()) as DeviceAppListProvider }
    single { InstalledDeviceAppLookup(get()) as DeviceAppLookup }
    factory<FireAnimationLoader> {
        LottieFireAnimationLoader(
            androidContext(),
            get(),
            get(),
            get(named("AppCoroutineScope"))
        )
    }
}

val systemComponentsModuleBindings = module {
    single {
        listOf(
            // Add other LifecycleObservers here
            get<FireAnimationLoader>() as LifecycleObserver
        )
    }
}

