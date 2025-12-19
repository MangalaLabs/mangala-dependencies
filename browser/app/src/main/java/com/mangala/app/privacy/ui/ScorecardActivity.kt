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

package com.mangala.app.privacy.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.mobile.android.ui.view.gone
import com.mangala.app.global.view.html
import com.mangala.mobile.android.ui.view.show
import com.mangala.app.privacy.renderer.*
import com.mangala.app.privacy.renderer.PrivacyUpgradeRenderer
import com.mangala.app.privacy.renderer.TrackersRenderer
import com.mangala.app.privacy.renderer.banner
import com.mangala.app.privacy.renderer.smallIcon
import com.mangala.app.privacy.renderer.successFailureIcon
import com.mangala.app.privacy.renderer.text
import com.mangala.app.tabs.tabId
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivityPrivacyScorecardBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ScorecardActivity : MangalaBrowserActivity() {

    private val binding: ActivityPrivacyScorecardBinding by viewBinding()
    private val trackersRenderer = TrackersRenderer()
    private val upgradeRenderer = PrivacyUpgradeRenderer()

    private val viewModel: ScorecardViewModel by viewModel()

    private val toolbar
        get() = binding.includeToolbar.findViewById<Toolbar>(R.id.toolbar)

    private val privacyScorecard
        get() = binding.contentPrivacyScorecard

    private val privacyScorecardHeader
        get() = privacyScorecard.privacyGrade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(toolbar)

        lifecycleScope.launch {
            viewModel.scoreCard(intent.tabId!!)
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { render(it) }
        }
    }

    private fun render(viewState: ScorecardViewModel.ViewState) {
        with(privacyScorecard) {
            val context = this@ScorecardActivity
            renderHeading(viewState)
            https.text = viewState.httpsStatus.text(context)
            https.setDrawableEnd(viewState.httpsStatus.successFailureIcon())
            practices.text = viewState.practices.text(context)
            practices.setDrawableEnd(viewState.practices.successFailureIcon())
            beforeGrade.setDrawableEnd(viewState.beforeGrade.smallIcon())
            afterGrade.setDrawableEnd(viewState.afterGrade.smallIcon())
            trackers.text = trackersRenderer.trackersText(context, viewState.trackerCount, viewState.allTrackersBlocked)
            trackers.setDrawableEnd(trackersRenderer.successFailureIcon(viewState.trackerCount))
            majorNetworks.text = trackersRenderer.majorNetworksText(context, viewState.majorNetworkCount, viewState.allTrackersBlocked)
            majorNetworks.setDrawableEnd(trackersRenderer.successFailureIcon(viewState.majorNetworkCount))
            showIsMemberOfMajorNetwork(viewState.showIsMemberOfMajorNetwork)
            showEnhancedGrade(viewState.showEnhancedGrade)
        }
    }

    private fun renderHeading(viewState: ScorecardViewModel.ViewState) {
        with(privacyScorecardHeader) {
            privacyBanner.setImageResource(viewState.afterGrade.banner(viewState.privacyOn))
            domain.text = viewState.domain
            if (viewState.isSiteInTempAllowedList) {
                heading.gone()
                protectionsTemporarilyDisabled.show()
            } else {
                protectionsTemporarilyDisabled.gone()
                heading.show()
                heading.text = upgradeRenderer.heading(this@ScorecardActivity, viewState.beforeGrade, viewState.afterGrade, viewState.privacyOn)
                    .html(this@ScorecardActivity)
            }
        }
    }

    private fun showIsMemberOfMajorNetwork(show: Boolean) {
        privacyScorecard.siteIsMajorNetworkMember.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEnhancedGrade(show: Boolean) {
        privacyScorecard.afterGrade.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun TextView.setDrawableEnd(@DrawableRes resource: Int) {
        if (resource == 0) {
            return
        }
        val drawable = AppCompatResources.getDrawable(this@ScorecardActivity, resource)
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    }

    companion object {

        fun intent(
            context: Context,
            tabId: String
        ): Intent {
            val intent = Intent(context, ScorecardActivity::class.java)
            intent.tabId = tabId
            return intent
        }
    }
}
