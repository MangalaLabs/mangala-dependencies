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



package com.mangala.app.browser.shortcut

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.mangala.app.browser.BrowserActivity
import com.mangala.app.browser.BrowserTabViewModel
import java.util.UUID

class ShortcutBuilder {

    private fun buildPinnedPageShortcut(
        context: Context,
        homeShortcut: BrowserTabViewModel.Command.AddHomeShortcut
    ): ShortcutInfoCompat {
        val intent = Intent(context, BrowserActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(Intent.EXTRA_TEXT, homeShortcut.url)
        intent.putExtra(SHORTCUT_EXTRA_ARG, true)

        val icon = when {
            homeShortcut.icon != null -> IconCompat.createWithBitmap(homeShortcut.icon)
            else -> IconCompat.createWithResource(context, com.schoolonair.wallet.component.resources.R.drawable.logo_full)
        }

        return ShortcutInfoCompat.Builder(context, UUID.randomUUID().toString())
            .setShortLabel(homeShortcut.title)
            .setIntent(intent)
            .setIcon(icon)
            .build()
    }

    private fun buildPendingIntent(
        context: Context,
        url: String,
        title: String
    ): PendingIntent? {
        val pinnedShortcutCallbackIntent = Intent(SHORTCUT_ADDED_ACTION)
        pinnedShortcutCallbackIntent.putExtra(SHORTCUT_URL_ARG, url)
        pinnedShortcutCallbackIntent.putExtra(SHORTCUT_TITLE_ARG, title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(
                context,
                SHORTCUT_ADDED_CODE,
                pinnedShortcutCallbackIntent,
                PendingIntent.FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
            )
        }else{
            return PendingIntent.getBroadcast(
                context,
                SHORTCUT_ADDED_CODE,
                pinnedShortcutCallbackIntent,
                FLAG_UPDATE_CURRENT
            )
        }
    }

    fun requestPinShortcut(
        context: Context,
        homeShortcut: BrowserTabViewModel.Command.AddHomeShortcut
    ) {
        val shortcutInfo = buildPinnedPageShortcut(context, homeShortcut)
        val pendingIntent = buildPendingIntent(context, homeShortcut.url, homeShortcut.title)

        ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, pendingIntent?.intentSender)
    }

    companion object {
        const val SHORTCUT_ADDED_ACTION: String = "appShortcutAdded"
        const val SHORTCUT_ADDED_CODE = 9000

        const val SHORTCUT_EXTRA_ARG = "shortCutAdded"
        const val SHORTCUT_URL_ARG = "shortcutUrl"
        const val SHORTCUT_TITLE_ARG = "shortcutTitle"
    }
}
