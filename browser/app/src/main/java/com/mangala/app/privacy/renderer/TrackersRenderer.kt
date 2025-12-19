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

package com.mangala.app.privacy.renderer

import android.content.Context
import androidx.annotation.DrawableRes
import com.mangala.app.privacy.db.NetworkLeaderboardEntry
import com.schoolonair.wallet.component.resources.R
import java.util.*

class TrackersRenderer {

    fun trackersText(
        context: Context,
        trackerCount: Int,
        allTrackersBlocked: Boolean
    ): String {
        val resource = if (allTrackersBlocked) com.schoolonair.wallet.component.resources.R.plurals.trackerBlocked else com.schoolonair.wallet.component.resources.R.plurals.trackersFound
        return context.resources.getQuantityString(resource, trackerCount, trackerCount)
    }

    fun majorNetworksText(
        context: Context,
        networkCount: Int,
        allTrackersBlocked: Boolean
    ): String {
        val resource = if (allTrackersBlocked) com.schoolonair.wallet.component.resources.R.plurals.majorNetworksBlocked else com.schoolonair.wallet.component.resources.R.plurals.majorNetworksFound
        return context.resources.getQuantityString(resource, networkCount, networkCount)
    }

    @DrawableRes
    fun networksBanner(allTrackersBlocked: Boolean): Int {
        return if (allTrackersBlocked) R.drawable.networks_banner_good else R.drawable.networks_banner_bad
    }

    @DrawableRes
    fun networksIcon(allTrackersBlocked: Boolean): Int {
        return if (allTrackersBlocked) R.drawable.networks_icon_good else R.drawable.networks_icon_bad
    }

    @DrawableRes
    fun networkPillIcon(
        context: Context,
        networkName: String
    ): Int? {
        return networkIcon(context, networkName, "network_pill_")
    }

    @DrawableRes
    fun networkLogoIcon(
        context: Context,
        networkName: String
    ): Int? {
        return networkIcon(context, networkName, "network_logo_")
    }

    private fun networkIcon(
        context: Context,
        networkName: String,
        prefix: String
    ): Int? {
        val drawable = "$prefix$networkName"
            .replace(" ", "_")
            .replace(".", "")
            .replace(",", "")
            .lowercase(Locale.ROOT)
        val resource = context.resources.getIdentifier(drawable, "drawable", context.packageName)
        return if (resource != 0) resource else null
    }

    fun networkPercentage(
        network: NetworkLeaderboardEntry,
        totalDomainsVisited: Int
    ): String? {
        if (totalDomainsVisited == 0 || network.count == 0) return ""
        val to100 = ((network.count / totalDomainsVisited.toFloat()) * 100).toInt()
        return "$to100%"
    }

    @DrawableRes
    fun successFailureIcon(count: Int): Int = when (count) {
        0 -> R.drawable.icon_success
        else -> R.drawable.icon_fail
    }
}
