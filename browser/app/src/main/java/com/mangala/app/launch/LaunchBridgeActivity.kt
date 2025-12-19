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

package com.mangala.app.launch

import android.os.Bundle
import androidx.activity.viewModels
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.app.launch.LaunchViewModel
import com.mangala.app.onboarding.ui.OnboardingActivity
import com.mangala.app.statistics.VariantManager
import com.mangala.navigation.BrowserActivityNavigationUtils
import com.schoolonair.wallet.browser.app.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber



class LaunchBridgeActivity : MangalaBrowserActivity() {

    val variantManager: VariantManager by inject()

    private val viewModel: LaunchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        configureObservers()

//        MainScope().launch { viewModel.determineViewToShow() }

        showHome()
//        showOnboarding()
    }

    private fun configureObservers() {
        viewModel.command.observe(this) {
            processCommand(it)
        }
    }

    private fun processCommand(it: LaunchViewModel.Command?) {
        when (it) {
            LaunchViewModel.Command.Onboarding -> {
                showOnboarding()
            }
            is LaunchViewModel.Command.Home -> {
                showHome()
            }
            else -> {}
        }
    }

    private fun showOnboarding() {
        startActivity(OnboardingActivity.intent(this))
        finish()
    }

    private fun showHome() {
        val chainId = intent?.getLongExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_ID, 1L) ?: 1L
        val address = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_ADDRESS) ?: ""
        val rpcServerUrl = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_RPC_SERVER_URL) ?: ""
        val chainNetworks = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK) ?: ""
        val accountId = intent?.getStringExtra(BrowserActivityNavigationUtils.EXTRA_ACCOUNT_ID) ?: ""
        Timber.d("1991 showHome address $address chainId $chainId rpcServerUrl $rpcServerUrl accountId $accountId")
        startActivity(BrowserActivityNavigationUtils.intent(this, chainId = chainId, address = address, rpcServerUrl = rpcServerUrl, chainNetWorks = chainNetworks, accountId = accountId))
        overridePendingTransition(0, 0)
        finish()
    }

}
