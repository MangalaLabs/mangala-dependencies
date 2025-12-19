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



package com.mangala.app.widget.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.app.widget.ui.AddWidgetInstructionsViewModel.Command.Close
import com.mangala.app.widget.ui.AddWidgetInstructionsViewModel.Command.ShowHome
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.databinding.ActivityAddWidgetInstructionsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddWidgetInstructionsActivity : MangalaBrowserActivity() {

    private val binding: ActivityAddWidgetInstructionsBinding by viewBinding()

    private val viewModel: AddWidgetInstructionsViewModel by viewModel()

    private val instructionsButtons
        get() = binding.includeAddWidgetInstructionButtons

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configureListeners()
        configureCommandObserver()
    }

    private fun configureListeners() {
        instructionsButtons.homeButton.setOnClickListener {
            viewModel.onShowHomePressed()
        }
        instructionsButtons.closeButton.setOnClickListener {
            viewModel.onClosePressed()
        }
    }

    private fun configureCommandObserver() {
        viewModel.command.observe(this) {
            when (it) {
                ShowHome -> showHome()
                Close -> close()
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onClosePressed()
    }

    fun showHome() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    fun close() {
        finishAfterTransition()
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, AddWidgetInstructionsActivity::class.java)
        }
    }
}
