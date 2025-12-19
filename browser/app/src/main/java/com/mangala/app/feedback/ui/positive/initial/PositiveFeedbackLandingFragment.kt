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


package com.mangala.app.feedback.ui.positive.initial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mangala.app.feedback.ui.common.FeedbackFragment
import com.mangala.app.playstore.PlayStoreUtils
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ContentFeedbackPositiveLandingBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class PositiveFeedbackLandingFragment : FeedbackFragment() {

    interface PositiveFeedbackLandingListener {
        fun userSelectedToRateApp()
        fun userSelectedToGiveFeedback()
        fun userGavePositiveFeedbackNoDetails()
    }

    private val binding: ContentFeedbackPositiveLandingBinding by viewBinding()

    private val viewModel: PositiveFeedbackLandingViewModel by viewModel()

    private val listener: PositiveFeedbackLandingListener?
        get() = activity as PositiveFeedbackLandingListener


    val playStoreUtils: PlayStoreUtils by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_feedback_positive_landing, container, false)
    }

    override fun configureViewModelObservers() {
        viewModel.command.observe(this) { command ->
            when (command) {
                Command.LaunchPlayStore -> {
                    launchPlayStore()
                    listener?.userSelectedToRateApp()
                }
                Command.Exit -> {
                    listener?.userGavePositiveFeedbackNoDetails()
                }
                Command.LaunchShareFeedbackPage -> {
                    listener?.userSelectedToGiveFeedback()
                }
            }
        }
    }

    override fun configureListeners() {
        binding.rateAppButton.setOnClickListener { viewModel.userSelectedToRateApp() }
        binding.shareFeedbackButton.setOnClickListener { viewModel.userSelectedToProvideFeedbackDetails() }
        binding.cancelButton.setOnClickListener { viewModel.userFinishedGivingPositiveFeedback() }
    }

    private fun launchPlayStore() {
        playStoreUtils.launchPlayStore()
    }

    companion object {
        fun instance(): PositiveFeedbackLandingFragment {
            return PositiveFeedbackLandingFragment()
        }
    }
}
