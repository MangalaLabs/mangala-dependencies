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



package com.mangala.app.di.pluginpoint.global

//import com.mangala.app.dev.settings.api.ApiDevTdsInterceptor
import com.mangala.app.browser.state.BrowserApplicationStateInfo
import com.mangala.app.global.ActivityLifecycleCallbacks
import com.mangala.app.global.api.ApiInterceptorPlugin
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.di.DaggerSet
import org.koin.dsl.module


//@Module
//@InstallIn(SingletonComponent::class)
//interface ApiInterceptorPluginPluginPointModule {
//    @Multibinds
//    fun refreshRetentionAtbPlugins(): DaggerSet<ApiInterceptorPlugin>
//
////    @Binds
////    @IntoSet
////    fun bindApiDevTdsInterceptor(apiDevTdsInterceptor: ApiDevTdsInterceptor): ApiInterceptorPlugin
//
//    @Binds
//    fun bindApiInterceptorPluginPluginPoint(apiInterceptorPluginPluginPoint: ApiInterceptorPluginPluginPoint): PluginPoint<ApiInterceptorPlugin>
//}

val apiInterceptorPluginPluginPointModule = module {
    // Define the ApiInterceptorPluginPluginPoint
    single<PluginPoint<ApiInterceptorPlugin>> {
        ApiInterceptorPluginPluginPoint()
    }
}