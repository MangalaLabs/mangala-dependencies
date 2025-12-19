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



package com.mangala.app.privacy.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.mangala.app.privacy.db.NetworkLeaderboardEntry
import com.mangala.app.privacy.renderer.TrackersRenderer
import com.schoolonair.wallet.browser.app.R

class TrackerNetworkLeaderboardPillView : FrameLayout {

    val renderer = TrackersRenderer()

    constructor(context: Context) : super(context, null) {
        initLayout()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs, 0) {
        initLayout()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initLayout()
    }

    private fun initLayout() {
        View.inflate(context, R.layout.view_network_tracker_pill, this)
    }

    fun render(
        networkEntity: NetworkLeaderboardEntry?,
        totalSitesVisited: Int
    ) {
        networkEntity ?: return
        findViewById<ImageView>(R.id.icon).setImageResource(renderer.networkPillIcon(context, networkEntity.networkName) ?: com.schoolonair.wallet.component.resources.R.drawable.network_pill_generic)
        val percentText = renderer.networkPercentage(networkEntity, totalSitesVisited)
        findViewById<ImageView>(R.id.icon).contentDescription = "${networkEntity.networkName} $percentText"
        findViewById<TextView>(R.id.percentage).text = percentText
    }
}
