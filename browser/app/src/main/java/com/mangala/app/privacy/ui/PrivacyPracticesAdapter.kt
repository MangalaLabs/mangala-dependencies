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

package com.mangala.app.privacy.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mangala.app.global.extensions.capitalizeFirstLetter
import com.schoolonair.wallet.browser.app.databinding.ItemPrivacyPracticeBinding
import com.schoolonair.wallet.component.resources.R
import kotlin.collections.ArrayList

class PrivacyPracticesAdapter : RecyclerView.Adapter<PrivacyPracticesAdapter.PracticeViewHolder>() {

    companion object {
        const val GOOD = 1
        const val BAD = 2
    }

    private var terms: List<Pair<Int, String>> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PracticeViewHolder {
        val binding = ItemPrivacyPracticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PracticeViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: PracticeViewHolder,
        position: Int
    ) {
        val term = terms[position]
        val descriptionResource = if (term.first == GOOD) com.schoolonair.wallet.component.resources.R.string.practicesIconContentGood else com.schoolonair.wallet.component.resources.R.string.practicesIconContentBad
        holder.binding.icon.contentDescription = holder.binding.icon.context.getText(descriptionResource)
        holder.binding.icon.setImageResource(if (term.first == GOOD) R.drawable.icon_success else R.drawable.icon_fail)
        holder.binding.description.text = term.second.capitalizeFirstLetter()
    }

    override fun getItemCount(): Int {
        return terms.size
    }

    fun updateData(
        goodTerms: List<String>,
        badTerms: List<String>
    ) {
        terms = goodTerms.map { GOOD to it } + badTerms.map { BAD to it }
        notifyDataSetChanged()
    }

    class PracticeViewHolder(
        val binding: ItemPrivacyPracticeBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
