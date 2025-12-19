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



package com.mangala.mobile.android.ui.store

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.mangala.mobile.android.ui.MangalaTheme

class ThemingSharedPreferences (private val context: Context) :
    ThemingDataStore {

    private val themePrefMapper = ThemePrefsMapper()

    override var theme: MangalaTheme
        get() = selectedThemeSavedValue()
        set(theme) = preferences.edit { putString(KEY_THEME, themePrefMapper.prefValue(theme)) }

    override fun isCurrentlySelected(theme: MangalaTheme): Boolean {
        return selectedThemeSavedValue() == theme
    }

    private fun selectedThemeSavedValue(): MangalaTheme {
        val savedValue = preferences.getString(KEY_THEME, null)
        return themePrefMapper.themeFrom(savedValue, MangalaTheme.LIGHT)
    }

    private val preferences: SharedPreferences
        get() = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)

    private class ThemePrefsMapper {

        companion object {
            private const val THEME_LIGHT = "LIGHT"
            private const val THEME_DARK = "DARK"
            private const val THEME_SYSTEM_DEFAULT = "SYSTEM_DEFAULT"
        }

        fun prefValue(theme: MangalaTheme) =
            when (theme) {
                MangalaTheme.SYSTEM_DEFAULT -> THEME_SYSTEM_DEFAULT
                MangalaTheme.LIGHT -> THEME_LIGHT
                MangalaTheme.DARK -> THEME_DARK
            }

        fun themeFrom(
            value: String?,
            defValue: MangalaTheme
        ) =
            when (value) {
                THEME_LIGHT -> MangalaTheme.LIGHT
                THEME_DARK -> MangalaTheme.DARK
                THEME_SYSTEM_DEFAULT -> MangalaTheme.SYSTEM_DEFAULT
                else -> defValue
            }
    }

    companion object {
        const val FILENAME = "com.mangala.app.settings_activity.settings"
        const val KEY_THEME = "THEME"
    }
}
