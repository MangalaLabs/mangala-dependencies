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
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.app.privacy.renderer.TrackersRenderer
import com.mangala.app.tabs.tabId
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivityTrackerNetworksBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackerNetworksActivity : MangalaBrowserActivity() {

    private val binding: ActivityTrackerNetworksBinding by viewBinding()

    private val trackersRenderer = TrackersRenderer()
    private val networksAdapter = TrackerNetworksAdapter()

    private val viewModel: TrackerNetworksViewModel by viewModel()

    private val toolbar
        get() = binding.includeToolbar.findViewById<Toolbar>(R.id.toolbar)

    private val trackerNetworks
        get() = binding.contentTrackerNetworks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(toolbar)
        configureRecycler()

        lifecycleScope.launch {
            viewModel.trackers(intent.tabId!!)
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { render(it) }
        }
    }

    private fun configureRecycler() {
        with(trackerNetworks) {
            networksList.layoutManager = LinearLayoutManager(this@TrackerNetworksActivity)
            networksList.adapter = networksAdapter
        }
    }

    private fun render(viewState: TrackerNetworksViewModel.ViewState) {
        with(trackerNetworks) {
            networksBanner.setImageResource(trackersRenderer.networksBanner(viewState.allTrackersBlocked))
            domain.text = viewState.domain
            heading.text = trackersRenderer.trackersText(this@TrackerNetworksActivity, viewState.trackerCount, viewState.allTrackersBlocked)
            networksAdapter.updateData(viewState.trackingEventsByNetwork)
        }
    }

    companion object {
        fun intent(
            context: Context,
            tabId: String
        ): Intent {
            val intent = Intent(context, TrackerNetworksActivity::class.java)
            intent.tabId = tabId
            return intent
        }
    }
}
