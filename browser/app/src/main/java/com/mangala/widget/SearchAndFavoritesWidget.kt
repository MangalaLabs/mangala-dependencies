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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.mangala.app.systemsearch.SystemSearchActivity
import com.mangala.navigation.BrowserActivityNavigationUtils
import com.mangala.navigation.BrowserActivityNavigationUtils.FAVORITES_ONBOARDING_EXTRA
import com.mangala.widget.FavoritesWidgetService.Companion.THEME_EXTRAS
import com.schoolonair.wallet.browser.app.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

enum class WidgetTheme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT;

    companion object {
        fun getThemeFrom(value: String?): WidgetTheme {
            if (value.isNullOrEmpty()) return SYSTEM_DEFAULT
            return runCatching { valueOf(value) }.getOrDefault(SYSTEM_DEFAULT)
        }
    }
}

class SearchAndFavoritesWidget : AppWidgetProvider(), KoinComponent {

    companion object {
        const val ACTION_FAVORITE = "com.mangala.widget.actionFavorite"
    }

    val widgetPrefs: WidgetPreferences by inject()

    val gridCalculator: SearchAndFavoritesGridCalculator by inject()

    private var layoutId: Int = R.layout.search_favorites_widget_daynight_auto

    override fun onReceive(
        context: Context,
        intent: Intent?
    ) {
//        inject(context)
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        Timber.i("SearchAndFavoritesWidget - onUpdate")
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id, null)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        Timber.i("SearchAndFavoritesWidget - onAppWidgetOptionsChanged")
        updateWidget(context, appWidgetManager, appWidgetId, newOptions)
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach {
            widgetPrefs.removeWidgetSettings(it)
        }
        super.onDeleted(context, appWidgetIds)
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        val widgetTheme = widgetPrefs.widgetTheme(appWidgetId)
        Timber.i("SearchAndFavoritesWidget theme for $appWidgetId is $widgetTheme")

        val (columns, rows) = getCurrentWidgetSize(context, appWidgetManager.getAppWidgetOptions(appWidgetId), newOptions)
        layoutId = getLayoutThemed(columns, widgetTheme)
        widgetPrefs.storeWidgetSize(appWidgetId, columns, rows)

        val remoteViews = RemoteViews(context.packageName, layoutId)

        remoteViews.setViewVisibility(R.id.searchInputBox, if (columns == 2) View.INVISIBLE else View.VISIBLE)
        remoteViews.setOnClickPendingIntent(R.id.widgetSearchBarContainer, buildPendingIntent(context))

        configureFavoritesGridView(context, appWidgetId, remoteViews, widgetTheme)
        configureEmptyWidgetCta(context, appWidgetId, remoteViews, widgetTheme)

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.favoritesGrid)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.emptyfavoritesGrid)
    }

    private fun getLayoutThemed(
        numColumns: Int,
        theme: WidgetTheme
    ): Int {
        // numcolumns method is not available for remoteViews. We rely on different xml to use different values on that attribute
        return when (theme) {
            WidgetTheme.LIGHT -> {
                when (numColumns) {
                    2 -> R.layout.search_favorites_widget_light_col2
                    3 -> R.layout.search_favorites_widget_light_col3
                    4 -> R.layout.search_favorites_widget_light_col4
                    5 -> R.layout.search_favorites_widget_light_col5
                    6 -> R.layout.search_favorites_widget_light_col6
                    else -> R.layout.search_favorites_widget_light_auto
                }
            }
            WidgetTheme.DARK -> {
                when (numColumns) {
                    2 -> R.layout.search_favorites_widget_dark_col2
                    3 -> R.layout.search_favorites_widget_dark_col3
                    4 -> R.layout.search_favorites_widget_dark_col4
                    5 -> R.layout.search_favorites_widget_dark_col5
                    6 -> R.layout.search_favorites_widget_dark_col6
                    else -> R.layout.search_favorites_widget_dark_auto
                }
            }
            WidgetTheme.SYSTEM_DEFAULT -> {
                when (numColumns) {
                    2 -> R.layout.search_favorites_widget_daynight_col2
                    3 -> R.layout.search_favorites_widget_daynight_col3
                    4 -> R.layout.search_favorites_widget_daynight_col4
                    5 -> R.layout.search_favorites_widget_daynight_col5
                    6 -> R.layout.search_favorites_widget_daynight_col6
                    else -> R.layout.search_favorites_widget_daynight_auto
                }
            }
        }
    }

    private fun getCurrentWidgetSize(
        context: Context,
        appWidgetOptions: Bundle,
        newOptions: Bundle?
    ): Pair<Int, Int> {
        var portraitWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        var landsWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
        var landsHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        var portraitHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

        if (newOptions != null) {
            portraitWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
            landsWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)
            landsHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            portraitHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)
        }

        val orientation = context.resources.configuration.orientation
        val width = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landsWidth
        } else {
            portraitWidth
        }
        val height = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landsHeight
        } else {
            portraitHeight
        }

        var columns = gridCalculator.calculateColumns(context, width)
        var rows = gridCalculator.calculateRows(context, height)

        Timber.i("SearchAndFavoritesWidget $portraitWidth x $portraitHeight -> $columns x $rows")
        return Pair(columns, rows)
    }

    private fun configureFavoritesGridView(
        context: Context,
        appWidgetId: Int,
        remoteViews: RemoteViews,
        widgetTheme: WidgetTheme
    ) {
        val browserActivityClassRef = BrowserActivityNavigationUtils.getBrowserActivityClassRef()
        val favoriteItemClickIntent = Intent(context, browserActivityClassRef)
        var favoriteClickPendingIntent: PendingIntent? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            favoriteClickPendingIntent = PendingIntent.getActivity(context, 0, favoriteItemClickIntent, PendingIntent.FLAG_IMMUTABLE or 0)
        }else{
            favoriteClickPendingIntent = PendingIntent.getActivity(context, 0, favoriteItemClickIntent, PendingIntent.FLAG_IMMUTABLE)
        }

        val extras = Bundle()
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        extras.putString(THEME_EXTRAS, widgetTheme.toString())

        val adapterIntent = Intent(context, FavoritesWidgetService::class.java)
        adapterIntent.putExtras(extras)
        adapterIntent.data = Uri.parse(adapterIntent.toUri(Intent.URI_INTENT_SCHEME))
        remoteViews.setRemoteAdapter(R.id.favoritesGrid, adapterIntent)
        remoteViews.setPendingIntentTemplate(R.id.favoritesGrid, favoriteClickPendingIntent)
    }

    private fun configureEmptyWidgetCta(
        context: Context,
        appWidgetId: Int,
        remoteViews: RemoteViews,
        widgetTheme: WidgetTheme
    ) {
        remoteViews.setOnClickPendingIntent(R.id.emptyGridViewContainer, buildOnboardingPendingIntent(context, appWidgetId))

        val extras = Bundle()
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        extras.putString(THEME_EXTRAS, widgetTheme.toString())

        val emptyAdapterIntent = Intent(context, EmptyFavoritesWidgetService::class.java)
        emptyAdapterIntent.putExtras(extras)
        emptyAdapterIntent.data = Uri.parse(emptyAdapterIntent.toUri(Intent.URI_INTENT_SCHEME))
        remoteViews.setEmptyView(R.id.emptyfavoritesGrid, R.id.emptyGridViewContainer)
        remoteViews.setRemoteAdapter(R.id.emptyfavoritesGrid, emptyAdapterIntent)
    }

    private fun buildPendingIntent(context: Context): PendingIntent {
        val intent = SystemSearchActivity.fromFavWidget(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }else{
            return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun buildOnboardingPendingIntent(
        context: Context,
        appWidgetId: Int
    ): PendingIntent {
        val intent = BrowserActivityNavigationUtils.intent(context, newSearch = true)
        intent.putExtra(FAVORITES_ONBOARDING_EXTRA, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE or 0)
        }else{
            return PendingIntent.getActivity(context, appWidgetId, intent, PendingIntent.FLAG_IMMUTABLE)
        }
    }

}
