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

package com.mangala.navigation

import android.content.Context
import android.content.Intent

object BrowserActivityNavigationUtils {
    fun getBrowserActivityClassRef() = Class.forName("com.mangala.app.browser.BrowserActivity")

    fun intent(
        context: Context,
        queryExtra: String? = null,
        newSearch: Boolean = false,
        notifyDataCleared: Boolean = false,
        chainId: Long = 1L,
        address: String = "",
        rpcServerUrl: String = "",
        chainNetWorks: String = "",
        accountId: String = "",
    ): Intent {
        val browserActivity = getBrowserActivityClassRef()

        val intent = Intent(context, browserActivity)
        intent.putExtra(Intent.EXTRA_TEXT, queryExtra)
        intent.putExtra(NEW_SEARCH_EXTRA, newSearch)
        intent.putExtra(NOTIFY_DATA_CLEARED_EXTRA, notifyDataCleared)
        intent.putExtra(EXTRA_CHAIN_ID, chainId)
        intent.putExtra(EXTRA_ADDRESS, address)
        intent.putExtra(EXTRA_RPC_SERVER_URL, rpcServerUrl)
        intent.putExtra(EXTRA_CHAIN_NETWORK, chainNetWorks)
        intent.putExtra(EXTRA_ACCOUNT_ID, accountId)
        return intent
    }

    const val FAVORITES_ONBOARDING_EXTRA = "FAVORITES_ONBOARDING_EXTRA"
    const val NEW_SEARCH_EXTRA = "NEW_SEARCH_EXTRA"
    const val NOTIFY_DATA_CLEARED_EXTRA = "NOTIFY_DATA_CLEARED_EXTRA"
    const val PERFORM_FIRE_ON_ENTRY_EXTRA = "PERFORM_FIRE_ON_ENTRY_EXTRA"
    const val LAUNCH_FROM_DEFAULT_BROWSER_DIALOG = "LAUNCH_FROM_DEFAULT_BROWSER_DIALOG"
    const val LAUNCH_FROM_FAVORITES_WIDGET = "LAUNCH_FROM_FAVORITES_WIDGET"

    const val EXTRA_ADDRESS = "ADDRESS"
    const val EXTRA_CHAIN_ID = "EXTRA_CHAIN_ID"
    const val EXTRA_ACCOUNT_ID = "EXTRA_ACCOUNT_ID"
    const val EXTRA_RPC_SERVER_URL = "EXTRA_RPC_SERVER_URL"
    const val EXTRA_CHAIN_NETWORK = "EXTRA_CHAIN_NETWORK"
}
