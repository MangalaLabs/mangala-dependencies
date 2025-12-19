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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.mangala.app.brokensite.BrokenSiteActivity
import com.mangala.app.brokensite.BrokenSiteData
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.mobile.android.ui.view.gone
import com.mangala.mobile.android.ui.view.hide
import com.mangala.app.global.view.html
import com.mangala.mobile.android.ui.view.show
import com.mangala.app.pixels.AppPixelName.*
import com.mangala.app.privacy.renderer.*
import com.mangala.app.privacy.ui.PrivacyDashboardViewModel.Command
import com.mangala.app.privacy.ui.PrivacyDashboardViewModel.Command.LaunchManageWhitelist
import com.mangala.app.privacy.ui.PrivacyDashboardViewModel.Command.LaunchReportBrokenSite
import com.mangala.app.privacy.ui.PrivacyDashboardViewModel.ViewState
import com.mangala.app.privacy.renderer.PrivacyUpgradeRenderer
import com.mangala.app.privacy.renderer.TrackersRenderer
import com.mangala.app.privacy.renderer.banner
import com.mangala.app.privacy.renderer.icon
import com.mangala.app.privacy.renderer.text
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.tabs.model.TabRepository
import com.mangala.app.tabs.tabId
import com.mangala.mobile.android.ui.view.quietlySetIsChecked
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivityPrivacyDashboardBinding
import com.schoolonair.wallet.browser.app.databinding.ContentPrivacyDashboardBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PrivacyDashboardActivity : MangalaBrowserActivity() {

    val repository: TabRepository by inject()

    val pixel: Pixel by inject()

    private val binding: ActivityPrivacyDashboardBinding by viewBinding()
    private val trackersRenderer = TrackersRenderer()
    private val upgradeRenderer = PrivacyUpgradeRenderer()

    private val viewModel: PrivacyDashboardViewModel by viewModel()

    private val toolbar
        get() = binding.includeToolbar.findViewById<Toolbar>(R.id.toolbar)

    private val contentPrivacyDashboard
        get() = binding.contentPrivacyDashboard

    private val privacyDashboardHeader
        get() = binding.contentPrivacyDashboard.privacyGrade

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(toolbar)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.viewState.observe(
            this,
            {
                it?.let { render(it) }
            }
        )
        viewModel.command.observe(
            this,
            {
                it?.let { processCommand(it) }
            }
        )
        repository.retrieveSiteData(intent.tabId!!).observe(
            this,
            {
                viewModel.onSiteChanged(it)
            }
        )
    }

    private val privacyToggleListener = CompoundButton.OnCheckedChangeListener { _, enabled ->
        viewModel.onPrivacyToggled(enabled)
    }

    private fun setupClickListeners() {
        with(contentPrivacyDashboard) {
            privacyDashboardHeader.privacyHeader.setOnClickListener {
                onScorecardClicked()
            }

            whitelistButton.setOnClickListener {
                viewModel.onManageWhitelistSelected()
            }

            brokenSiteButton.setOnClickListener {
                viewModel.onReportBrokenSiteSelected()
            }

            httpsContainer.setOnClickListener {
                pixel.fire(PRIVACY_DASHBOARD_ENCRYPTION)
            }

            networksContainer.setOnClickListener {
                pixel.fire(PRIVACY_DASHBOARD_NETWORKS)
                startActivity(TrackerNetworksActivity.intent(this@PrivacyDashboardActivity, intent.tabId!!))
            }

            practicesContainer.setOnClickListener {
                pixel.fire(PRIVACY_DASHBOARD_PRIVACY_PRACTICES)
                startActivity(PrivacyPracticesActivity.intent(this@PrivacyDashboardActivity, intent.tabId!!))
            }

            trackerNetworkLeaderboard.setOnClickListener {
                pixel.fire(PRIVACY_DASHBOARD_GLOBAL_STATS)
            }

            privacyToggle.setOnCheckedChangeListener(privacyToggleListener)
        }
    }

    private fun render(viewState: ViewState) {
        if (isFinishing) {
            return
        }
        with(contentPrivacyDashboard) {
            val context = this@PrivacyDashboardActivity
            val toggle = viewState.toggleEnabled ?: true
            privacyDashboardHeader.privacyBanner.setImageResource(viewState.afterGrade.banner(toggle))
            privacyDashboardHeader.domain.text = viewState.domain
            renderHeading(viewState, toggle)
            httpsIcon.setImageResource(viewState.httpsStatus.icon())
            httpsText.text = viewState.httpsStatus.text(context)
            networksIcon.setImageResource(trackersRenderer.networksIcon(viewState.allTrackersBlocked))
            networksText.text = trackersRenderer.trackersText(context, viewState.trackerCount, viewState.allTrackersBlocked)
            practicesIcon.setImageResource(viewState.practices.icon())
            practicesText.text = viewState.practices.text(context)
            renderToggle(toggle, viewState.isSiteInTempAllowedList)
            renderTrackerNetworkLeaderboard(viewState)
            renderButtonContainer(viewState.isSiteInTempAllowedList)
            updateActivityResult(viewState.shouldReloadPage)
        }
    }

    private fun renderButtonContainer(isSiteIntTempAllowedList: Boolean) {
        if (isSiteIntTempAllowedList) {
            contentPrivacyDashboard.buttonContainer.gone()
        } else {
            contentPrivacyDashboard.buttonContainer.show()
        }
    }

    private fun renderHeading(
        viewState: ViewState,
        isPrivacyOn: Boolean
    ) {
        with(privacyDashboardHeader) {
            if (viewState.isSiteInTempAllowedList) {
                heading.gone()
                protectionsTemporarilyDisabled.show()
            } else {
                protectionsTemporarilyDisabled.gone()
                heading.show()
                heading.text = upgradeRenderer.heading(this@PrivacyDashboardActivity, viewState.beforeGrade, viewState.afterGrade, isPrivacyOn)
                    .html(this@PrivacyDashboardActivity)
            }
        }
    }

    private fun renderTrackerNetworkLeaderboard(viewState: ViewState) {
        with(contentPrivacyDashboard) {
            if (!viewState.shouldShowTrackerNetworkLeaderboard) {
                hideTrackerNetworkLeaderboard()
                return
            }
            trackerNetworkPill1.render(viewState.trackerNetworkEntries.elementAtOrNull(0), viewState.sitesVisited)
            trackerNetworkPill2.render(viewState.trackerNetworkEntries.elementAtOrNull(1), viewState.sitesVisited)
            trackerNetworkPill3.render(viewState.trackerNetworkEntries.elementAtOrNull(2), viewState.sitesVisited)
            showTrackerNetworkLeaderboard()
        }
    }

    private fun ContentPrivacyDashboardBinding.showTrackerNetworkLeaderboard() {
        trackerNetworkLeaderboardHeader.show()
        trackerNetworkPill1.show()
        trackerNetworkPill2.show()
        trackerNetworkPill3.show()
        trackerNetworkLeaderboardNotReady.hide()
    }

    private fun ContentPrivacyDashboardBinding.hideTrackerNetworkLeaderboard() {
        trackerNetworkLeaderboardHeader.hide()
        trackerNetworkPill1.hide()
        trackerNetworkPill2.hide()
        trackerNetworkPill3.hide()
        trackerNetworkLeaderboardNotReady.show()
    }

    private fun renderToggle(
        enabled: Boolean,
        isSiteIntTempAllowedList: Boolean
    ) {
        val backgroundColor = if (enabled && !isSiteIntTempAllowedList) com.schoolonair.wallet.component.resources.R.color.midGreen else com.schoolonair.wallet.component.resources.R.color.warmerGray
        contentPrivacyDashboard.privacyToggleContainer.setBackgroundColor(ContextCompat.getColor(this, backgroundColor))
        contentPrivacyDashboard.privacyToggle.quietlySetIsChecked(enabled && !isSiteIntTempAllowedList, privacyToggleListener)
        contentPrivacyDashboard.privacyToggle.isEnabled = !isSiteIntTempAllowedList
    }

    private fun processCommand(command: Command) {
        when (command) {
            is LaunchManageWhitelist -> launchWhitelistActivity()
            is LaunchReportBrokenSite -> launchReportBrokenSite(command.data)
        }
    }

    private fun onScorecardClicked() {
        pixel.fire(PRIVACY_DASHBOARD_SCORECARD)
        startActivity(ScorecardActivity.intent(this, intent.tabId!!))
    }

    private fun launchReportBrokenSite(data: BrokenSiteData) {
        startActivity(BrokenSiteActivity.intent(this, data))
    }

    private fun launchWhitelistActivity() {
        startActivity(WhitelistActivity.intent(this))
    }

    private fun updateActivityResult(shouldReload: Boolean) {
        if (shouldReload) {
            setResult(RELOAD_RESULT_CODE)
        } else {
            setResult(Activity.RESULT_OK)
        }
    }

    companion object {

        const val RELOAD_RESULT_CODE = 100

        fun intent(
            context: Context,
            tabId: String
        ): Intent {
            val intent = Intent(context, PrivacyDashboardActivity::class.java)
            intent.tabId = tabId
            return intent
        }
    }
}
