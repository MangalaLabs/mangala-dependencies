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

package com.mangala.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.CompoundButton.OnCheckedChangeListener
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.app.global.view.html
import com.mangala.mobile.android.ui.view.quietlySetIsChecked
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.mangala.navigation.BrowserActivityNavigationUtils
import com.schoolonair.wallet.browser.app.databinding.ActivityGlobalPrivacyControlBinding
import com.schoolonair.wallet.component.resources.R
import org.koin.androidx.viewmodel.ext.android.viewModel


class GlobalPrivacyControlActivity : MangalaBrowserActivity() {

    private val binding: ActivityGlobalPrivacyControlBinding by viewBinding()

    private val viewModel: GlobalPrivacyControlViewModel by viewModel()

    private val toolbar
        get() = binding.includeToolbar.findViewById<Toolbar>(com.schoolonair.wallet.browser.app.R.id.toolbar)

    private val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            viewModel.onLearnMoreSelected()
        }
    }

    private val globalPrivacyControlToggleListener = OnCheckedChangeListener { _, isChecked ->
        viewModel.onUserToggleGlobalPrivacyControl(isChecked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(toolbar)
        configureUiEventHandlers()
        configureClickableLink()
        observeViewModel()
    }

    private fun configureClickableLink() {
        val htmlGPCText = getString(R.string.globalPrivacyControlDescription).html(this)
        val gpcSpannableString = SpannableStringBuilder(htmlGPCText)
        val urlSpans = htmlGPCText.getSpans(0, htmlGPCText.length, URLSpan::class.java)
        urlSpans?.forEach {
            gpcSpannableString.apply {
                setSpan(
                    clickableSpan,
                    gpcSpannableString.getSpanStart(it),
                    gpcSpannableString.getSpanEnd(it),
                    gpcSpannableString.getSpanFlags(it)
                )
                removeSpan(it)
            }
        }
        binding.globalPrivacyControlDescription.apply {
            text = gpcSpannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    private fun configureUiEventHandlers() {
        binding.globalPrivacyControlToggle.setOnCheckedChangeListener(globalPrivacyControlToggleListener)
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(
            this,
            { viewState ->
                viewState?.let {
                    binding.globalPrivacyControlToggle.quietlySetIsChecked(it.globalPrivacyControlEnabled, globalPrivacyControlToggleListener)
                    binding.globalPrivacyControlToggle.isEnabled = it.globalPrivacyControlFeatureEnabled
                }
            }
        )

        viewModel.command.observe(
            this,
            { command ->
                command?.let {
                    when (it) {
                        is GlobalPrivacyControlViewModel.Command.OpenLearnMore -> openLearnMoreSite(it.url)
                    }
                }
            }
        )
    }

    private fun openLearnMoreSite(url: String) {
        startActivity(BrowserActivityNavigationUtils.intent(this, url))
        finish()
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, GlobalPrivacyControlActivity::class.java)
        }
    }
}
