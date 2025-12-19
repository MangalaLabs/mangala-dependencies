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

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.mangala.app.bookmarks.model.FavoritesRepository
import com.mangala.mobile.android.R as CommonR
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.global.domain
import com.mangala.app.global.view.generateDefaultDrawable
import com.mangala.navigation.BrowserActivityNavigationUtils.LAUNCH_FROM_FAVORITES_WIDGET
import com.mangala.navigation.BrowserActivityNavigationUtils.NEW_SEARCH_EXTRA
import com.mangala.navigation.BrowserActivityNavigationUtils.NOTIFY_DATA_CLEARED_EXTRA
import com.schoolonair.wallet.browser.app.R
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class FavoritesWidgetService : RemoteViewsService() {

    companion object {
        const val MAX_ITEMS_EXTRAS = "MAX_ITEMS_EXTRAS"
        const val THEME_EXTRAS = "THEME_EXTRAS"
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FavoritesWidgetItemFactory(this.applicationContext, intent)
    }

    class FavoritesWidgetItemFactory(
        val context: Context,
        intent: Intent
    ) : RemoteViewsFactory, KoinComponent {

        private val theme = WidgetTheme.getThemeFrom(intent.extras?.getString(THEME_EXTRAS))

        val favoritesDataRepository: FavoritesRepository by inject()

        val faviconManager: FaviconManager  by inject()

        val widgetPrefs: WidgetPreferences by inject()

        private val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        private val faviconItemSize = context.resources.getDimension(CommonR.dimen.savedSiteGridItemFavicon).toInt()
        private val faviconItemCornerRadius = context.resources.getDimension(CommonR.dimen.savedSiteGridItemCornerRadiusFavicon).toInt()

        private val maxItems: Int
            get() {
                return widgetPrefs.widgetSize(appWidgetId).let { it.first * it.second }
            }

        data class WidgetFavorite(
            val title: String,
            val url: String,
            val bitmap: Bitmap?
        )

        private val domains = mutableListOf<WidgetFavorite>()

        override fun onCreate() {
//            inject(context)
        }

        override fun onDataSetChanged() {
            val newList = favoritesDataRepository.favoritesSync().take(maxItems).map {
                val bitmap = runBlocking {
                    faviconManager.loadFromDiskWithParams(
                        url = it.url,
                        cornerRadius = faviconItemCornerRadius,
                        width = faviconItemSize,
                        height = faviconItemSize
                    )
                        ?: generateDefaultDrawable(context, it.url.extractDomain().orEmpty()).toBitmap(faviconItemSize, faviconItemSize)
                }
                WidgetFavorite(it.title, it.url, bitmap)
            }
            domains.clear()
            domains.addAll(newList)
        }

        override fun onDestroy() {
        }

        override fun getCount(): Int {
            return maxItems
        }

        private fun String.extractDomain(): String? {
            return if (this.startsWith("http")) {
                this.toUri().domain()
            } else {
                "https://$this".extractDomain()
            }
        }

        override fun getViewAt(position: Int): RemoteViews {
            val item = if (position >= domains.size) null else domains[position]
            val remoteViews = RemoteViews(context.packageName, getItemLayout())
            if (item != null) {
                if (item.bitmap != null) {
                    remoteViews.setImageViewBitmap(R.id.quickAccessFavicon, item.bitmap)
                }
                remoteViews.setTextViewText(R.id.quickAccessTitle, item.title)
                configureClickListener(remoteViews, item.url)
            } else {
                remoteViews.setTextViewText(R.id.quickAccessTitle, "")
                remoteViews.setImageViewResource(R.id.quickAccessFavicon, getEmptyBackgroundDrawable())
            }

            return remoteViews
        }

        private fun getItemLayout(): Int {
            return when (theme) {
                WidgetTheme.LIGHT -> R.layout.view_favorite_widget_light_item
                WidgetTheme.DARK -> R.layout.view_favorite_widget_dark_item
                WidgetTheme.SYSTEM_DEFAULT -> R.layout.view_favorite_widget_daynight_item
            }
        }

        private fun getEmptyBackgroundDrawable(): Int {
            return when (theme) {
                WidgetTheme.LIGHT -> com.schoolonair.wallet.component.resources.R.drawable.search_widget_favorite_favicon_light_background
                WidgetTheme.DARK -> com.schoolonair.wallet.component.resources.R.drawable.search_widget_favorite_favicon_dark_background
                WidgetTheme.SYSTEM_DEFAULT -> com.schoolonair.wallet.component.resources.R.drawable.search_widget_favorite_favicon_daynight_background
            }
        }

        private fun configureClickListener(
            remoteViews: RemoteViews,
            item: String
        ) {
            val bundle = Bundle()
            bundle.putString(Intent.EXTRA_TEXT, item)
            bundle.putBoolean(NEW_SEARCH_EXTRA, false)
            bundle.putBoolean(LAUNCH_FROM_FAVORITES_WIDGET, true)
            bundle.putBoolean(NOTIFY_DATA_CLEARED_EXTRA, false)
            val intent = Intent()
            intent.putExtras(bundle)
            remoteViews.setOnClickFillInIntent(R.id.quickAccessFaviconContainer, intent)
        }

        override fun getLoadingView(): RemoteViews {
            return RemoteViews(context.packageName, getItemLayout())
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
