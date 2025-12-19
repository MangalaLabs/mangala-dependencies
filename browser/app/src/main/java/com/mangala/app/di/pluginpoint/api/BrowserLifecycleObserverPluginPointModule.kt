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



package com.mangala.app.di.pluginpoint.api

import com.mangala.app.anr.AnrSupervisor
import com.mangala.app.anr.AnrSupervisorRunnable
import com.mangala.app.di.pluginpoint.api.BrowserLifecycleObserverPluginPoint
import com.mangala.app.fire.AutomaticDataClearer
import com.mangala.app.fire.DataClearer
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.browser.api.BrowserLifecycleObserver
import com.mangala.di.DaggerSet
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//interface BrowserLifecycleObserverPluginPointModule {
//    @Multibinds
//    fun browserLifecycleObserver(): DaggerSet<BrowserLifecycleObserver>
//
//    @Binds
//    @IntoSet
//    @Singleton
//    fun bindAutomaticDataClearer(automaticDataClearer: AutomaticDataClearer): BrowserLifecycleObserver
//
//    @Binds
//    @IntoSet
//    fun bindAnrSupervisor(anrSupervisor: AnrSupervisor): BrowserLifecycleObserver
//
//    @Binds
//    fun bindBrowserLifecycleObserverPluginPoint(browserLifecycleObserverPluginPoint: BrowserLifecycleObserverPluginPoint): PluginPoint<BrowserLifecycleObserver>
//}

val browserLifecycleObserverModule = module {

    // You need to define an instance of AutomaticDataClearer and AnrSupervisor here
    // Here I'm just using placeholders
    single{ AutomaticDataClearer(get(), get(), get(), get(), get()) }
    single { AnrSupervisorRunnable(get()) }
    single { AnrSupervisor(get()) }

    // Define set of BrowserLifecycleObserver
//    single { setOf(get<AutomaticDataClearer>(), get<AnrSupervisor>()) }

    // Define BrowserLifecycleObserverPluginPoint
    single<BrowserLifecycleObserverPluginPoint> {
        BrowserLifecycleObserverPluginPoint()
    }
}
