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

package com.mangala.app.feedback.ui.initial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mangala.app.feedback.ui.common.FeedbackFragment
import com.mangala.app.feedback.ui.initial.InitialFeedbackFragmentViewModel.Command.*
import com.mangala.mobile.android.ui.MangalaTheme
import com.mangala.mobile.android.ui.store.ThemingDataStore
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ContentFeedbackBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class InitialFeedbackFragment : FeedbackFragment() {

    interface InitialFeedbackListener {
        fun userSelectedPositiveFeedback()
        fun userSelectedNegativeFeedback()
        fun userCancelled()
    }


    val themingDataStore: ThemingDataStore by inject()

    private val binding: ContentFeedbackBinding by viewBinding()

    private val viewModel: InitialFeedbackFragmentViewModel by viewModel()

    private val listener: InitialFeedbackListener?
        get() = activity as InitialFeedbackListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_feedback, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (themingDataStore.theme == MangalaTheme.LIGHT) {
            binding.positiveFeedbackButton.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.button_happy_light_theme)
            binding.negativeFeedbackButton.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.button_sad_light_theme)
        } else {
            binding.positiveFeedbackButton.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.button_happy_dark_theme)
            binding.negativeFeedbackButton.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.button_sad_dark_theme)
        }
    }

    override fun configureViewModelObservers() {
        viewModel.command.observe(this) {
            when (it) {
                PositiveFeedbackSelected -> listener?.userSelectedPositiveFeedback()
                NegativeFeedbackSelected -> listener?.userSelectedNegativeFeedback()
                UserCancelled -> listener?.userCancelled()
            }
        }
    }

    override fun configureListeners() {
        binding.positiveFeedbackButton.setOnClickListener { viewModel.onPositiveFeedback() }
        binding.negativeFeedbackButton.setOnClickListener { viewModel.onNegativeFeedback() }
    }

    companion object {
        fun instance(): InitialFeedbackFragment {
            return InitialFeedbackFragment()
        }
    }
}
