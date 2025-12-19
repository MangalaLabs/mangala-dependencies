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

package com.mangala.app.browser.history

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.mangala.app.browser.BrowserTabViewModel.Command.ShowBackNavigationHistory
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.browser.history.NavigationHistoryAdapter.NavigationHistoryListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.schoolonair.wallet.browser.app.R

class NavigationHistorySheet(
    context: Context,
    private val viewLifecycleOwner: LifecycleOwner,
    private val faviconManager: FaviconManager,
    private val tabId: String,
    private val history: ShowBackNavigationHistory,
    private val listener: NavigationHistorySheetListener
) : BottomSheetDialog(context, com.mangala.mobile.android.R.style.NavigationHistoryDialog) {

    interface NavigationHistorySheetListener {
        fun historicalPageSelected(stackIndex: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        setContentView(R.layout.navigation_history_popup_view)

        findViewById<RecyclerView>(R.id.historyRecycler)?.also { recycler ->
            NavigationHistoryAdapter(
                viewLifecycleOwner, faviconManager, tabId,
                object : NavigationHistoryListener {
                    override fun historicalPageSelected(stackIndex: Int) {
                        dismiss()
                        listener.historicalPageSelected(stackIndex)
                    }
                }
            ).also { adapter ->
                recycler.adapter = adapter
                adapter.updateNavigationHistory(history.history)
            }
        }
    }
}
