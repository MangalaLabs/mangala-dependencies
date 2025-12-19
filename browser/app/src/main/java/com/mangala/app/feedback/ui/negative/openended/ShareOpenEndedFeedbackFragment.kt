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



package com.mangala.app.feedback.ui.negative.openended

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.viewModels
import com.mangala.app.feedback.ui.common.FeedbackFragment
import com.mangala.app.feedback.ui.common.LayoutScrollingTouchListener
import com.mangala.app.feedback.ui.negative.FeedbackType.MainReason
import com.mangala.app.feedback.ui.negative.FeedbackType.SubReason
import com.mangala.app.feedback.ui.negative.FeedbackTypeDisplay.Companion.mainReasons
import com.mangala.app.feedback.ui.negative.FeedbackTypeDisplay.Companion.subReasons
import com.mangala.app.feedback.ui.negative.openended.ShareOpenEndedNegativeFeedbackViewModel.Command
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ContentFeedbackOpenEndedFeedbackBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class ShareOpenEndedFeedbackFragment : FeedbackFragment() {

    interface OpenEndedFeedbackListener {
        fun userProvidedNegativeOpenEndedFeedback(
            mainReason: MainReason,
            subReason: SubReason?,
            feedback: String
        )

        fun userProvidedPositiveOpenEndedFeedback(feedback: String)
        fun userCancelled()
    }

    private val binding: ContentFeedbackOpenEndedFeedbackBinding by viewBinding()

    private val viewModel: ShareOpenEndedNegativeFeedbackViewModel by viewModel()

    private val listener: OpenEndedFeedbackListener?
        get() = activity as OpenEndedFeedbackListener

    private var isPositiveFeedback: Boolean = true

    private var mainReason: MainReason? = null
    private var subReason: SubReason? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_feedback_open_ended_feedback, container, false)
    }

    override fun configureViewModelObservers() {
        viewModel.command.observe(this) { command ->
            when (command) {
                is Command.Exit -> {
                    listener?.userCancelled()
                }
                is Command.ExitAndSubmitNegativeFeedback -> {
                    listener?.userProvidedNegativeOpenEndedFeedback(command.mainReason, command.subReason, command.feedback)
                }
                is Command.ExitAndSubmitPositiveFeedback -> {
                    listener?.userProvidedPositiveOpenEndedFeedback(command.feedback)
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments == null) throw IllegalArgumentException("Missing required arguments")

        arguments?.let { args ->
            isPositiveFeedback = args.getBoolean(IS_POSITIVE_FEEDBACK_EXTRA)

            if (isPositiveFeedback) {
                updateDisplayForPositiveFeedback()
            } else {
                updateDisplayForNegativeFeedback(args)
            }
        }
    }

    private fun updateDisplayForPositiveFeedback() {
        binding.title.text = getString(com.schoolonair.wallet.component.resources.R.string.feedbackShareDetails)
        binding.subtitle.text = getString(com.schoolonair.wallet.component.resources.R.string.sharePositiveFeedbackWithTheTeam)
        binding.openEndedFeedbackContainer.hint = getString(com.schoolonair.wallet.component.resources.R.string.whatHaveYouBeenEnjoying)
        binding.emoticonImage.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_happy_face)
    }

    private fun updateDisplayForNegativeFeedback(args: Bundle) {
        mainReason = args.getSerializable(MAIN_REASON_EXTRA) as MainReason
        subReason = args.getSerializable(SUB_REASON_EXTRA) as SubReason?

        binding.title.text = getDisplayText(mainReason!!)
        binding.subtitle.text = getDisplayText(subReason)
        binding.openEndedFeedbackContainer.hint = getInputHintText(mainReason!!)
        binding.emoticonImage.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.ic_sad_face)
    }

    private fun getDisplayText(reason: MainReason): String {
        val display = mainReasons[reason] ?: return ""
        return getString(display.titleDisplayResId)
    }

    private fun getDisplayText(reason: SubReason?): String {
        val display = subReasons[reason] ?: return getString(com.schoolonair.wallet.component.resources.R.string.tellUsHowToImprove)
        return getString(display.subtitleDisplayResId)
    }

    private fun getInputHintText(reason: MainReason): String {
        return if (reason == MainReason.OTHER) {
            getString(com.schoolonair.wallet.component.resources.R.string.feedbackSpecificAsPossible)
        } else {
            getString(com.schoolonair.wallet.component.resources.R.string.openEndedInputHint)
        }
    }

    override fun configureListeners() {
        with(binding) {
            rootScrollView.doOnNextLayout {
                binding.openEndedFeedback.setOnTouchListener(LayoutScrollingTouchListener(rootScrollView, openEndedFeedbackContainer.y.toInt()))
            }

            submitFeedbackButton.setOnClickListener {
                val openEndedComment = openEndedFeedback.text.toString()
                if (isPositiveFeedback) {
                    viewModel.userSubmittingPositiveFeedback(openEndedComment)
                } else {
                    viewModel.userSubmittingNegativeFeedback(mainReason!!, subReason, openEndedComment)
                }
            }
        }
    }

    companion object {

        private const val MAIN_REASON_EXTRA = "MAIN_REASON_EXTRA"
        private const val SUB_REASON_EXTRA = "SUB_REASON_EXTRA"
        private const val IS_POSITIVE_FEEDBACK_EXTRA = "IS_POSITIVE_FEEDBACK_EXTRA"

        fun instanceNegativeFeedback(
            mainReason: MainReason,
            subReason: SubReason?
        ): ShareOpenEndedFeedbackFragment {
            val fragment = ShareOpenEndedFeedbackFragment()
            fragment.arguments = Bundle().also {
                it.putBoolean(IS_POSITIVE_FEEDBACK_EXTRA, false)
                it.putSerializable(MAIN_REASON_EXTRA, mainReason)

                if (subReason != null) {
                    it.putSerializable(SUB_REASON_EXTRA, subReason)
                }
            }
            return fragment
        }

        fun instancePositiveFeedback(): ShareOpenEndedFeedbackFragment {
            val fragment = ShareOpenEndedFeedbackFragment()
            fragment.arguments = Bundle().also {
                it.putBoolean(IS_POSITIVE_FEEDBACK_EXTRA, true)
            }
            return fragment
        }
    }
}
