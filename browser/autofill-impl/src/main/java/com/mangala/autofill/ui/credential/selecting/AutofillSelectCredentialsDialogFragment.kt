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



package com.mangala.autofill.ui.credential.selecting

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.global.extractDomain
import com.mangala.autofill.CredentialAutofillPickerDialog
import com.mangala.autofill.domain.app.LoginCredentials
import com.mangala.autofill.impl.databinding.ContentAutofillSelectCredentialsTooltipBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

//@AndroidEntryPoint
class AutofillSelectCredentialsDialogFragment : BottomSheetDialogFragment(),
    CredentialAutofillPickerDialog, KoinComponent {

//    @Inject
    val faviconManager: FaviconManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = ContentAutofillSelectCredentialsTooltipBinding.inflate(inflater, container, false)
        configureViews(binding)
        return binding.root
    }

    private fun configureViews(binding: ContentAutofillSelectCredentialsTooltipBinding) {
        configureSiteDetails(binding)
        configureRecyclerView(binding)
    }

    private fun configureSiteDetails(binding: ContentAutofillSelectCredentialsTooltipBinding) {
        val originalUrl = getOriginalUrl()
        val url = originalUrl.extractDomain() ?: originalUrl

        binding.siteName.text = url

        lifecycleScope.launch {
            faviconManager.loadToViewFromLocalOrFallback(url = url, view = binding.favicon)
        }
    }

    private fun configureRecyclerView(binding: ContentAutofillSelectCredentialsTooltipBinding) {
        binding.availableCredentialsRecycler.adapter =
            CredentialsPickerRecyclerAdapter(this, faviconManager, getAvailableCredentials()) { selectedCredentials ->
                val result = Bundle().also {
                    it.putBoolean(CredentialAutofillPickerDialog.KEY_CANCELLED, false)
                    it.putString(CredentialAutofillPickerDialog.KEY_URL, getOriginalUrl())
                    it.putParcelable(CredentialAutofillPickerDialog.KEY_CREDENTIALS, selectedCredentials)
                }
                parentFragment?.setFragmentResult(CredentialAutofillPickerDialog.RESULT_KEY_CREDENTIAL_PICKER, result)
                dismiss()
            }
    }

    override fun onCancel(dialog: DialogInterface) {
        val result = Bundle().also {
            it.putBoolean(CredentialAutofillPickerDialog.KEY_CANCELLED, true)
            it.putString(CredentialAutofillPickerDialog.KEY_URL, getOriginalUrl())
        }
        parentFragment?.setFragmentResult(CredentialAutofillPickerDialog.RESULT_KEY_CREDENTIAL_PICKER, result)
    }

    private fun getAvailableCredentials() = arguments?.getParcelableArrayList<LoginCredentials>(
        CredentialAutofillPickerDialog.KEY_CREDENTIALS)!!

    private fun getOriginalUrl() = arguments?.getString(CredentialAutofillPickerDialog.KEY_URL)!!

    override fun asDialogFragment(): DialogFragment = this

    companion object {

        fun instance(url: String, credentials: List<LoginCredentials>): AutofillSelectCredentialsDialogFragment {

            val cr = ArrayList<LoginCredentials>(credentials)

            val fragment = AutofillSelectCredentialsDialogFragment()
            fragment.arguments =
                Bundle().also {
                    it.putString(CredentialAutofillPickerDialog.KEY_URL, url)
                    it.putParcelableArrayList(CredentialAutofillPickerDialog.KEY_CREDENTIALS, cr)
                }
            return fragment
        }
    }
}
