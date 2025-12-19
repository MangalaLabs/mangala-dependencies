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

package com.mangala.app.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.mangala.app.global.AppUrl.Url
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.mangala.navigation.BrowserActivityNavigationUtils
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivityAboutBinding

class AboutMangalaBrowserActivity : MangalaBrowserActivity() {

    private val binding: ActivityAboutBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.includeToolbar.findViewById(R.id.toolbar))

        binding.includeContent.learnMoreLink.setOnClickListener {
            startActivity(BrowserActivityNavigationUtils.intent(this, Url.ABOUT))
            finish()
        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, AboutMangalaBrowserActivity::class.java)
        }
    }
}
