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
import com.mangala.app.fire.AutomaticDataClearer
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.browser.api.BrowserLifecycleObserver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrowserLifecycleObserverPluginPoint (
): PluginPoint<BrowserLifecycleObserver>, KoinComponent {

    private val automaticDataClearer: AutomaticDataClearer by inject()
    private val anrSupervisor: AnrSupervisor by inject()

    val plugins: List<BrowserLifecycleObserver> by lazy { listOf(automaticDataClearer, anrSupervisor) }
    override fun getPlugins(): Collection<BrowserLifecycleObserver> {
        return plugins
    }
}
