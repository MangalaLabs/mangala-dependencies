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

package com.mangala.app.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mangala.app.bookmarks.model.FavoritesRepository
import com.mangala.app.di.AppCoroutineScope
import com.mangala.widget.SearchAndFavoritesWidget
import com.schoolonair.wallet.browser.app.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch



class FavoritesObserver (
    context: Context,
    private val favoritesRepository: FavoritesRepository,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope
) : DefaultLifecycleObserver {

    private val instance = AppWidgetManager.getInstance(context)
    private val componentName = ComponentName(context, SearchAndFavoritesWidget::class.java)

    override fun onStart(owner: LifecycleOwner) {
        appCoroutineScope.launch {
            favoritesRepository.favorites().collect {
                instance.notifyAppWidgetViewDataChanged(instance.getAppWidgetIds(componentName), R.id.favoritesGrid)
                instance.notifyAppWidgetViewDataChanged(instance.getAppWidgetIds(componentName), R.id.emptyfavoritesGrid)
            }
        }
    }
}
