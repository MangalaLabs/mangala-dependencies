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

package com.mangala.app.audit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mangala.app.audit.AuditSettingsViewModel.Command
import com.mangala.app.audit.AuditSettingsViewModel.Companion.COOKIES_3P_RETRIEVE
import com.mangala.app.audit.AuditSettingsViewModel.Companion.COOKIES_3P_STORE
import com.mangala.app.audit.AuditSettingsViewModel.Companion.FIRE_BUTTON_RETRIEVE
import com.mangala.app.audit.AuditSettingsViewModel.Companion.FIRE_BUTTON_STORE
import com.mangala.app.audit.AuditSettingsViewModel.Companion.GPC
import com.mangala.app.audit.AuditSettingsViewModel.Companion.GPC_OTHER
import com.mangala.app.audit.AuditSettingsViewModel.Companion.HTTPS_UPGRADES
import com.mangala.app.audit.AuditSettingsViewModel.Companion.REQUEST_BLOCKING
import com.mangala.app.audit.AuditSettingsViewModel.Companion.STEP_1
import com.mangala.app.audit.AuditSettingsViewModel.Companion.STEP_2
import com.mangala.app.audit.AuditSettingsViewModel.Companion.STEP_3
import com.mangala.app.audit.AuditSettingsViewModel.Companion.SURROGATES
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.databinding.ActivityAuditSettingsBinding
import com.schoolonair.wallet.navigation.utils.BrowserActivityNavigationUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AuditSettingsActivity : MangalaBrowserActivity() {

    private val binding: ActivityAuditSettingsBinding by viewBinding()

    private val viewModel: AuditSettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.includeToolbar.toolbar)

        configureUiEventHandlers()
        observeViewModel()
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }

    private fun configureUiEventHandlers() {
        binding.step1.setOnClickListener { viewModel.goToUrl(STEP_1) }
        binding.step2.setOnClickListener { viewModel.goToUrl(STEP_2) }
        binding.step3.setOnClickListener { viewModel.goToUrl(STEP_3) }
        binding.requestBlocking.setOnClickListener { viewModel.goToUrl(REQUEST_BLOCKING) }
        binding.httpsUpgrades.setOnClickListener { viewModel.goToUrl(HTTPS_UPGRADES) }
        binding.fireButtonStore.setOnClickListener { viewModel.goToUrl(FIRE_BUTTON_STORE) }
        binding.fireButtonRetrieve.setOnClickListener { viewModel.goToUrl(FIRE_BUTTON_RETRIEVE) }
        binding.cookies3pStore.setOnClickListener { viewModel.goToUrl(COOKIES_3P_STORE) }
        binding.cookies3pRetrieve.setOnClickListener { viewModel.goToUrl(COOKIES_3P_RETRIEVE) }
        binding.gpc.setOnClickListener { viewModel.goToUrl(GPC) }
        binding.surrogates.setOnClickListener { viewModel.goToUrl(SURROGATES) }
        binding.gpcOther.setOnClickListener { viewModel.goToUrl(GPC_OTHER) }
        binding.requestBlockingDisabled.setOnClickListener { viewModel.goToUrl(REQUEST_BLOCKING, false) }
        binding.surrogatesDisabled.setOnClickListener { viewModel.goToUrl(SURROGATES, false) }
    }

    private fun observeViewModel() {
        viewModel.commands()
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { processCommand(it) }
            .launchIn(lifecycleScope)
    }

    private fun processCommand(it: Command?) {
        when (it) {
            is Command.GoToUrl -> goToUrl(it.url)
            else -> TODO()
        }
    }

    private fun goToUrl(url: String) {
        startActivity(BrowserActivityNavigationUtils.intent(this, url))
        finish()
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, AuditSettingsActivity::class.java)
        }
    }
}
