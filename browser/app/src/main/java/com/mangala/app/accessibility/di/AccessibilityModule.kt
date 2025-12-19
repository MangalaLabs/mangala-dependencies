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

package com.mangala.app.accessibility.di

import android.content.Context
import com.mangala.app.accessibility.AccessibilityManager
import com.mangala.app.accessibility.AccessibilitySettingsViewModel
import com.mangala.app.accessibility.AppAccessibilityManager
import com.mangala.app.accessibility.data.AccessibilitySettingsDataStore
import com.mangala.app.accessibility.data.AccessibilitySettingsSharedPreferences
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider

import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//class AccessibilityModule {
//
//    @Provides
//    @Singleton
//    fun providesAccessibilitySettingsDataStore(
//        @ApplicationContext context: Context,
//        dispatcherProvider: DispatcherProvider,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope
//    ): AccessibilitySettingsDataStore = AccessibilitySettingsSharedPreferences(context, dispatcherProvider, appCoroutineScope)
//
//    @Provides
//    @Singleton
//    fun providesAccessibilityManager(
//        accessibilitySettingsDataStore: AccessibilitySettingsDataStore
//    ): AccessibilityManager = AppAccessibilityManager(accessibilitySettingsDataStore)
//}


val accessibilityModule = module {
    single { AccessibilitySettingsSharedPreferences(androidContext(), get(), get(named("AppCoroutineScope"))) as AccessibilitySettingsDataStore }
    single { AppAccessibilityManager(get()) as AccessibilityManager }

    viewModel { AccessibilitySettingsViewModel(get()) }
}
