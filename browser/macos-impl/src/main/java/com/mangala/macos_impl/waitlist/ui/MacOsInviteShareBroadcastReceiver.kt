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



package com.mangala.macos_impl.waitlist.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.macos_impl.MacOsPixelNames.MACOS_WAITLIST_SHARE_SHARED
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject

//@AndroidEntryPoint
class MacOsInviteShareBroadcastReceiver : BroadcastReceiver(), KoinComponent {

//    @Inject
    private val pixel: Pixel by inject()

    override fun onReceive(context: Context, intent: Intent) {
        pixel.fire(MACOS_WAITLIST_SHARE_SHARED)
    }
}
