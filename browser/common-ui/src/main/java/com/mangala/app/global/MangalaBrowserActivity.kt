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


package com.mangala.app.global

import android.annotation.SuppressLint
import android.app.UiModeManager
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mangala.mobile.android.ui.MangalaTheme
import com.mangala.mobile.android.ui.MangalaTheme.DARK
import com.mangala.mobile.android.ui.applyTheme
import com.mangala.mobile.android.ui.store.ThemingDataStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class MangalaBrowserActivity : AppCompatActivity(), KoinComponent {

    val themingDataStore: ThemingDataStore by inject()

    private var themeChangeReceiver: BroadcastReceiver? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        onCreate(savedInstanceState, true)
    }

    /**
     * We need to conditionally defer the Dagger initialization in certain places. So if this method
     * is called from an Activity with daggerInject=false, you'll probably need to call
     * daggerInject() directly.
     */
    fun onCreate(
        savedInstanceState: Bundle?,
        daggerInject: Boolean = true
    ) {
//        daggerInject()
        themeChangeReceiver = applyTheme(themingDataStore.theme)
        super.onCreate(savedInstanceState)
    }

//    protected fun daggerInject() {
//        themingDataStore = EntryPointAccessors.fromApplication(this, ThemingDataStoreFactory::class.java).getThemingDataStore()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        themeChangeReceiver?.let {
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(it)
        }
        super.onDestroy()
    }

    fun setupToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(com.schoolonair.wallet.component.resources.R.drawable.ic_back_24)
    }

    fun isDarkThemeEnabled(): Boolean {
        return when (themingDataStore.theme) {
            MangalaTheme.SYSTEM_DEFAULT -> {
                val uiManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                when (uiManager.nightMode) {
                    UiModeManager.MODE_NIGHT_YES -> true
                    else -> false
                }
            }
            DARK -> true
            else -> false
        }
    }

    interface ThemingDataStoreFactory {
        fun getThemingDataStore(): ThemingDataStore
    }
}
