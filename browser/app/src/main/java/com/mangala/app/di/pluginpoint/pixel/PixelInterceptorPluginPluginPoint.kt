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

import com.mangala.app.global.api.AtpPixelRemovalInterceptor
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.global.plugins.pixel.PixelInterceptorPlugin
import com.mangala.app.pixels.OsVersionPixelInterceptor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PixelInterceptorPluginPluginPoint (): PluginPoint<PixelInterceptorPlugin>, KoinComponent {

    private val atpPixelRemovalInterceptor: AtpPixelRemovalInterceptor by inject()
    private val osVersionPixelInterceptor: OsVersionPixelInterceptor by inject()
    override fun getPlugins(): Collection<PixelInterceptorPlugin> = listOf(atpPixelRemovalInterceptor, osVersionPixelInterceptor)
}
