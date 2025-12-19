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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import com.mangala.app.widget.ui.AddWidgetInstructionsActivity
import com.mangala.app.widget.ui.WidgetCapabilities
import com.mangala.widget.SearchAndFavoritesWidget
import com.schoolonair.wallet.component.resources.R

interface AddWidgetLauncher {
    fun launchAddWidget(activity: Activity?)
}

class AddWidgetCompatLauncher(
//    @Named("appWidgetManagerAddWidgetLauncher")
    private val defaultAddWidgetLauncher: AddWidgetLauncher,
//    @Named("legacyAddWidgetLauncher")
    private val legacyAddWidgetLauncher: AddWidgetLauncher,
    private val widgetCapabilities: WidgetCapabilities
) : AddWidgetLauncher {

    override fun launchAddWidget(activity: Activity?) {
        if (widgetCapabilities.supportsAutomaticWidgetAdd)
            defaultAddWidgetLauncher.launchAddWidget(activity) else legacyAddWidgetLauncher.launchAddWidget(activity)
    }
}

class AppWidgetManagerAddWidgetLauncher  : AddWidgetLauncher {
    companion object {
        const val ACTION_ADD_WIDGET = "actionWidgetAdded"
        const val EXTRA_WIDGET_ADDED_LABEL = "extraWidgetAddedLabel"
        private const val CODE_ADD_WIDGET = 11922
    }

    @SuppressLint("NewApi")
    override fun launchAddWidget(activity: Activity?) {
        activity?.let {
            val provider = ComponentName(it, SearchAndFavoritesWidget::class.java)
            AppWidgetManager.getInstance(it).requestPinAppWidget(provider, null, buildPendingIntent(it))
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun buildPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(ACTION_ADD_WIDGET).run {
            putExtra(EXTRA_WIDGET_ADDED_LABEL, context.getString(R.string.favoritesWidgetLabel))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(
                context,
                CODE_ADD_WIDGET,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            ) // Will not return null since FLAG_UPDATE_CURRENT has been supplied
        }else{
            return PendingIntent.getBroadcast(
                context,
                CODE_ADD_WIDGET,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            ) // Will not return null since FLAG_UPDATE_CURRENT has been supplied
        }

    }
}

class LegacyAddWidgetLauncher : AddWidgetLauncher {
    override fun launchAddWidget(activity: Activity?) {
        activity?.let {
            val options = ActivityOptions.makeSceneTransitionAnimation(it).toBundle()
            it.startActivity(AddWidgetInstructionsActivity.intent(it), options)
        }
    }
}
