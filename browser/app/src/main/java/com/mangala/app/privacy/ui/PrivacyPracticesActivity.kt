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
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mangala.app.global.AppUrl.Url
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.app.privacy.renderer.banner
import com.mangala.app.privacy.renderer.text
import com.mangala.app.tabs.tabId
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.mangala.navigation.BrowserActivityNavigationUtils
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivityPrivacyPracticesBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrivacyPracticesActivity : MangalaBrowserActivity() {

    private val binding: ActivityPrivacyPracticesBinding by viewBinding()

    private val practicesAdapter = PrivacyPracticesAdapter()

    private val viewModel: PrivacyPracticesViewModel by viewModel()
    private val toolbar
        get() = binding.includeToolbar.findViewById<Toolbar>(R.id.toolbar)

    private val privacyPractices
        get() = binding.contentPrivacyPractices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(toolbar)
        configureRecycler()
        setupClickListeners()

        lifecycleScope.launch {
            viewModel.privacyPractices(intent.tabId!!)
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { render(it) }
        }
    }

    private fun configureRecycler() {
        privacyPractices.practicesList.layoutManager = LinearLayoutManager(this)
        privacyPractices.practicesList.adapter = practicesAdapter
    }

    private fun render(viewState: PrivacyPracticesViewModel.ViewState) {
        with(privacyPractices) {
            practicesBanner.setImageResource(viewState.practices.banner())
            domain.text = viewState.domain
            heading.text = viewState.practices.text(applicationContext)
            practicesAdapter.updateData(viewState.goodTerms, viewState.badTerms)
        }
    }

    private fun setupClickListeners() {
        privacyPractices.tosdrLink.setOnClickListener {
            startActivity(BrowserActivityNavigationUtils.intent(this, Url.TOSDR))
            finish()
        }
    }

    companion object {

        fun intent(
            context: Context,
            tabId: String
        ): Intent {
            val intent = Intent(context, PrivacyPracticesActivity::class.java)
            intent.tabId = tabId
            return intent
        }
    }
}
