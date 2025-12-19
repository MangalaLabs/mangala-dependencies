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



package com.mangala.app.di.pluginpoint.pixel

import androidx.lifecycle.LifecycleObserver
import com.mangala.app.di.pluginpoint.api.LifecycleObserverPluginPoint
import com.mangala.app.di.pluginpoint.pixel.PixelInterceptorPluginPluginPoint
import com.mangala.app.global.api.AtpPixelRemovalInterceptor
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.global.plugins.pixel.PixelInterceptorPlugin
import com.mangala.app.httpsupgrade.HttpsUpgraderImpl
import com.mangala.app.pixels.OsVersionPixelInterceptor
import com.mangala.di.DaggerSet
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//interface PixelInterceptorPluginPluginPointModule {
//    @Multibinds
//    fun pixelInterceptorPlugin(): DaggerSet<PixelInterceptorPlugin> // PixelInterceptorPlugin_PluginPoint_Module
//
//    @Binds
//    @IntoSet
//    fun bindAtpPixelRemovalInterceptor(atpPixelRemovalInterceptor: AtpPixelRemovalInterceptor): PixelInterceptorPlugin
//
//    @Binds
//    @IntoSet
//    fun bindOsVersionPixelInterceptor(osVersionPixelInterceptor: OsVersionPixelInterceptor): PixelInterceptorPlugin
//
//    /** Omitted {@link com.mangala.app.flipper.plugins.PixelFlipperPlugin} because it has a module to bind into set  */
//
//    @Binds
//    fun bindPixelInterceptorPluginPluginPoint(pixelInterceptorPluginPluginPoint: PixelInterceptorPluginPluginPoint): PluginPoint<PixelInterceptorPlugin> // PixelInterceptorPlugin_PluginPoint_Module
//}


val pixelInterceptorPluginPluginPointModule = module {
    // Define the PixelInterceptorPluginPluginPointModule
    single{ AtpPixelRemovalInterceptor() }
    single{ OsVersionPixelInterceptor(get()) }

    factory<PluginPoint<PixelInterceptorPlugin>> {
        PixelInterceptorPluginPluginPoint()
    }

}