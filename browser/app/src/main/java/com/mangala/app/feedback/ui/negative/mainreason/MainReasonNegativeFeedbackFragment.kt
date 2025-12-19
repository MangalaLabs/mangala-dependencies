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

package com.mangala.app.feedback.ui.negative.mainreason

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mangala.app.feedback.ui.common.FeedbackFragment
import com.mangala.app.feedback.ui.common.FeedbackItemDecoration
import com.mangala.app.feedback.ui.negative.FeedbackType.MainReason
import com.mangala.app.feedback.ui.negative.FeedbackTypeDisplay
import com.mangala.app.feedback.ui.negative.FeedbackTypeDisplay.FeedbackTypeMainReasonDisplay
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ContentFeedbackNegativeDisambiguationMainReasonBinding



class MainReasonNegativeFeedbackFragment : FeedbackFragment() {
    private lateinit var recyclerAdapter: MainReasonAdapter

    interface MainReasonNegativeFeedbackListener {

        fun userSelectedNegativeFeedbackMainReason(type: MainReason)
    }

    private val binding: ContentFeedbackNegativeDisambiguationMainReasonBinding by viewBinding()

    private val listener: MainReasonNegativeFeedbackListener?
        get() = activity as MainReasonNegativeFeedbackListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_feedback_negative_disambiguation_main_reason, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerAdapter = MainReasonAdapter(object : (FeedbackTypeMainReasonDisplay) -> Unit {
            override fun invoke(reason: FeedbackTypeMainReasonDisplay) {
                listener?.userSelectedNegativeFeedbackMainReason(reason.mainReason)
            }
        })

        activity?.let {
            binding.recyclerView.layoutManager = LinearLayoutManager(it)
            binding.recyclerView.adapter = recyclerAdapter
            binding.recyclerView.addItemDecoration(FeedbackItemDecoration(ContextCompat.getDrawable(it, com.schoolonair.wallet.component.resources.R.drawable.feedback_list_divider)!!))

            val listValues = getMainReasonsDisplayText()
            recyclerAdapter.submitList(listValues)
        }
    }

    private fun getMainReasonsDisplayText(): List<FeedbackTypeMainReasonDisplay> {
        return MainReason.values().mapNotNull {
            FeedbackTypeDisplay.mainReasons[it]
        }
    }

    companion object {

        fun instance(): MainReasonNegativeFeedbackFragment {
            return MainReasonNegativeFeedbackFragment()
        }
    }
}
