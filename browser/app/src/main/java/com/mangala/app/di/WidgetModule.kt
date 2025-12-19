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
import com.mangala.app.widget.ui.AppWidgetCapabilities
import com.mangala.app.widget.ui.WidgetCapabilities
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.widget.SearchAndFavoritesGridCalculator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//object WidgetModule {
//
//    @Provides
//    @Singleton
//    fun widgetCapabilities(@ApplicationContext context: Context, appBuildConfig: AppBuildConfig): WidgetCapabilities = AppWidgetCapabilities(context, appBuildConfig)
//
//    @Provides
//    fun gridCalculator(): SearchAndFavoritesGridCalculator = SearchAndFavoritesGridCalculator()
//}

val widgetModule = module {
    single { SearchAndFavoritesGridCalculator() }
    single<WidgetCapabilities> { AppWidgetCapabilities(androidContext(), get()) }
}
