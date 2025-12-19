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

package com.mangala.app.settings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.schoolonair.wallet.browser.app.R

class SettingsAppLinksSelectorFragment : DialogFragment() {

    interface Listener {
        fun onAppLinkSettingSelected(selectedSetting: AppLinkSettingType)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val currentOption: AppLinkSettingType =
            arguments?.getSerializable(DEFAULT_OPTION_EXTRA) as AppLinkSettingType? ?: AppLinkSettingType.ASK_EVERYTIME

        val rootView =
            View.inflate(activity, R.layout.dialog_radio_group_selector_fragment, null)

        updateCurrentSelection(currentOption, rootView.findViewById(R.id.selectorRadioGroup))

        val alertBuilder = AlertDialog.Builder(requireActivity())
            .setView(rootView)
            .setTitle(com.schoolonair.wallet.component.resources.R.string.settingsTitleAppLinksDialog)
            .setPositiveButton(com.schoolonair.wallet.component.resources.R.string.dialogSave) { _, _ ->
                dialog?.let {
                    val radioGroup = it.findViewById(R.id.selectorRadioGroup) as RadioGroup
                    val selectedOption = when (radioGroup.checkedRadioButtonId) {
                        R.id.selectorRadioButton1 -> AppLinkSettingType.ASK_EVERYTIME
                        R.id.selectorRadioButton2 -> AppLinkSettingType.ALWAYS
                        R.id.selectorRadioButton3 -> AppLinkSettingType.NEVER
                        else -> AppLinkSettingType.ASK_EVERYTIME
                    }
                    val listener = activity as Listener?
                    listener?.onAppLinkSettingSelected(selectedOption)
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }

        return alertBuilder.create()
    }

    private fun updateCurrentSelection(
        currentOption: AppLinkSettingType,
        radioGroup: RadioGroup
    ) {
        val selectedId = currentOption.radioButtonId()
        radioGroup.check(selectedId)
    }

    @IdRes
    private fun AppLinkSettingType.radioButtonId(): Int {
        return when (this) {
            AppLinkSettingType.ASK_EVERYTIME -> R.id.selectorRadioButton1
            AppLinkSettingType.ALWAYS -> R.id.selectorRadioButton2
            AppLinkSettingType.NEVER -> R.id.selectorRadioButton3
        }
    }

    companion object {

        private const val DEFAULT_OPTION_EXTRA = "DEFAULT_OPTION"

        fun create(appLinkSettingType: AppLinkSettingType?): SettingsAppLinksSelectorFragment {
            val fragment = SettingsAppLinksSelectorFragment()

            fragment.arguments = Bundle().also {
                it.putSerializable(DEFAULT_OPTION_EXTRA, appLinkSettingType)
            }
            return fragment
        }
    }
}
