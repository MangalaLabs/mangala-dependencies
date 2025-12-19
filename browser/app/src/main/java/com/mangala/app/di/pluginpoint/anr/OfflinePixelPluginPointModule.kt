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



package com.mangala.app.di.pluginpoint.anr

import com.mangala.app.anr.AnrOfflinePixelSender
import com.mangala.app.di.pluginpoint.anr.OfflinePixelPluginPoint
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.statistics.api.OfflinePixel
import com.mangala.di.DaggerSet
import org.koin.dsl.module


//@Module
//@InstallIn(SingletonComponent::class)
//interface OfflinePixelPluginPointModule {
//    @Multibinds
//    fun browserLifecycleObserver(): DaggerSet<OfflinePixel>
//
//    @Binds
//    @IntoSet
//    fun bindAnrOfflinePixelSender(anrOfflinePixelSender: AnrOfflinePixelSender): OfflinePixel
//
//    @Binds
//    fun bindOfflinePixelPluginPoint(offlinePixelPluginPoint: OfflinePixelPluginPoint): PluginPoint<OfflinePixel>
//}
//


val offlinePixelModule = module {
    single<OfflinePixel> { AnrOfflinePixelSender(get(), get()) }
    single<PluginPoint<OfflinePixel>> { OfflinePixelPluginPoint() }
//    single { setOf(get<AnrOfflinePixelSender>()) }
}
