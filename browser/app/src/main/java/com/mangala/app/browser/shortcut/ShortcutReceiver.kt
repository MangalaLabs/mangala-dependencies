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

package com.mangala.app.browser.shortcut

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mangala.app.browser.shortcut.ShortcutBuilder.Companion.SHORTCUT_TITLE_ARG
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.statistics.pixels.Pixel
import com.schoolonair.wallet.component.resources.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber


class ShortcutReceiver (
    private val context: Context,
    private val pixel: Pixel,
    private val dispatcher: DispatcherProvider,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope
) : BroadcastReceiver(), DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        Timber.v("Registering shortcut receiver")
        // ensure we unregister the receiver first
        kotlin.runCatching { context.unregisterReceiver(this) }
        context.registerReceiver(this, IntentFilter(ShortcutBuilder.SHORTCUT_ADDED_ACTION))
    }

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        val title = intent?.getStringExtra(SHORTCUT_TITLE_ARG)

        if (!IGNORE_MANUFACTURERS_LIST.contains(Build.MANUFACTURER)) {
            context?.let {
                Toast.makeText(it, it.getString(R.string.shortcutAddedText, title), Toast.LENGTH_SHORT).show()
            }
        }

        appCoroutineScope.launch(dispatcher.io()) {
            pixel.fire(AppPixelName.SHORTCUT_ADDED)
        }
    }

    companion object {
        val IGNORE_MANUFACTURERS_LIST = listOf("samsung", "huawei")
    }
}
