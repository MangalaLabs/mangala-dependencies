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


package com.mangala.mobile.android.ui

import android.app.UiModeManager
import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mangala.mobile.android.R
import com.mangala.mobile.android.ui.Theming.Constants.BROADCAST_THEME_CHANGED
import com.mangala.mobile.android.ui.Theming.Constants.THEME_MAP

enum class MangalaTheme {
    SYSTEM_DEFAULT,
    DARK,
    LIGHT
}

object Theming {

    fun getThemedDrawable(
        context: Context,
        drawableId: Int,
        theme: MangalaTheme
    ): Drawable {
        val themeId: Int = THEME_MAP[Pair(R.style.AppTheme3, theme)] ?: R.style.AppTheme3_Light
        return context.resources.getDrawable(
            drawableId, ContextThemeWrapper(context, themeId).theme
        )
    }

    object Constants {

        const val BROADCAST_THEME_CHANGED = "BROADCAST_THEME_CHANGED"

        val THEME_MAP =
            mapOf(
                Pair(R.style.AppTheme3, MangalaTheme.LIGHT) to R.style.AppTheme3_Light,
                Pair(R.style.AppTheme3, MangalaTheme.DARK) to R.style.AppTheme3_Dark
            )
    }
}

private fun AppCompatActivity.manifestThemeId(): Int {
    return try {
        packageManager.getActivityInfo(componentName, 0).themeResource
    } catch (exception: Exception) {
        R.style.AppTheme3
    }
}

fun AppCompatActivity.applyTheme(theme: MangalaTheme): BroadcastReceiver? {
    if (theme == MangalaTheme.SYSTEM_DEFAULT) {
        val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        val themeId =
            when (uiManager.nightMode) {
                MODE_NIGHT_YES -> THEME_MAP[Pair(manifestThemeId(), MangalaTheme.DARK)]
                MODE_NIGHT_NO -> THEME_MAP[Pair(manifestThemeId(), MangalaTheme.LIGHT)]
                else -> THEME_MAP[Pair(manifestThemeId(), MangalaTheme.LIGHT)]
            }
        if (themeId != null) setTheme(themeId)
    } else {
        val themeId = THEME_MAP[Pair(manifestThemeId(), theme)] ?: return null
        setTheme(themeId)
    }
    return registerForThemeChangeBroadcast()
}

// Move this to LiveData/Flow and use appDelegate for night/day theming
private fun AppCompatActivity.registerForThemeChangeBroadcast(): BroadcastReceiver {
    val manager = LocalBroadcastManager.getInstance(applicationContext)
    val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                recreate()
            }
        }
    manager.registerReceiver(receiver, IntentFilter(BROADCAST_THEME_CHANGED))
    return receiver
}

fun AppCompatActivity.sendThemeChangedBroadcast() {
    val manager = LocalBroadcastManager.getInstance(applicationContext)
    manager.sendBroadcast(Intent(BROADCAST_THEME_CHANGED))
}
