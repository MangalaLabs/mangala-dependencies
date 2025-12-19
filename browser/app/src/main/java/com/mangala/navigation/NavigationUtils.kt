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

object NavigationUtils {

    fun goToWalletConnect(context: Context, chainId: Long, addressConnect: String, prevTabId: String, importPassData: String, url: String, accountId: String): Intent {
        val intent = Intent(context, Class.forName("com.mangala.wallet.walletconnect.WalletConnectActivity"))
        intent.putExtra("EXTRA_CHAIN_ID", chainId)
        intent.putExtra("EXTRA_ADDRESS_CONNECT", addressConnect)
        intent.putExtra("EXTRA_TAB_ID", prevTabId)
        intent.putExtra("EXTRA_IMPORT_PASS_DATA", importPassData)
        intent.putExtra("EXTRA_URL", url)
        intent.putExtra("EXTRA_ACCOUNT_ID", accountId)
        return intent
    }
}
