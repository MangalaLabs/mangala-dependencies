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



package com.mangala.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.mangala.app.bookmarks.model.FavoritesRepository
import com.schoolonair.wallet.browser.app.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EmptyFavoritesWidgetService : RemoteViewsService() {

    companion object {
        const val MAX_ITEMS_EXTRAS = "MAX_ITEMS_EXTRAS"
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return EmptyFavoritesWidgetItemFactory(this.applicationContext, intent)
    }

    /**
     * This RemoteViewsFactory will not render any item. It's used by is used for convenience to simplify executing background operations to show/hide empty widget CTA.
     * If this RemoteViewsFactory count is 0, SearchAndFavoritesWidget R.id.emptyfavoritesGrid will show the configured EmptyView.
     */
    class EmptyFavoritesWidgetItemFactory(
        val context: Context,
        intent: Intent
    ) : RemoteViewsFactory, KoinComponent {

        val favoritesDataRepository: FavoritesRepository by inject()

        private var count = 0

        override fun onCreate() {
//            inject(context)
        }

        override fun onDataSetChanged() {
            count = if (favoritesDataRepository.userHasFavorites()) 1 else 0
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            return count
        }

        override fun getViewAt(position: Int): RemoteViews {
            return RemoteViews(context.packageName, R.layout.empty_view)
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(context.packageName, R.layout.empty_view)
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

    }
}
