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

package com.mangala.app.email

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.mangala.app.global.view.html
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.schoolonair.wallet.browser.app.R

class EmailAutofillTooltipFragment(
    context: Context,
    val address: String
) : BottomSheetDialog(context, R.style.EmailTooltip) {

    var useAddress: (() -> Unit) = {}
    var usePrivateAlias: (() -> Unit) = {}

    init {
        setContentView(R.layout.content_autofill_tooltip)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDialog()
    }

    private fun setDialog() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        val addressFormatted = context.getString(com.schoolonair.wallet.component.resources.R.string.autofillTooltipUseYourAlias, address)
        findViewById<TextView>(R.id.tooltipPrimaryCtaTitle)?.text = addressFormatted.html(context)

        findViewById<View>(R.id.secondaryCta)?.setOnClickListener {
            usePrivateAlias()
            dismiss()
        }

        findViewById<View>(R.id.primaryCta)?.setOnClickListener {
            useAddress()
            dismiss()
        }
    }
}
