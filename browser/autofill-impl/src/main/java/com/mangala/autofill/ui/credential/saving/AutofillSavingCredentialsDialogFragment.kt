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



package com.mangala.autofill.ui.credential.saving

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.global.extractDomain
import com.mangala.autofill.CredentialSavePickerDialog
import com.mangala.autofill.domain.app.LoginCredentials
import com.mangala.autofill.impl.R
import com.mangala.autofill.impl.databinding.ContentAutofillSaveNewCredentialsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

//@AndroidEntryPoint
class AutofillSavingCredentialsDialogFragment : BottomSheetDialogFragment(),
    CredentialSavePickerDialog, KoinComponent {

//    @Inject
    val faviconManager: FaviconManager by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = ContentAutofillSaveNewCredentialsBinding.inflate(inflater, container, false)
        configureViews(binding)
        return binding.root
    }

    private fun configureViews(binding: ContentAutofillSaveNewCredentialsBinding) {
        configureSiteDetails(binding)
    }

    private fun configureSiteDetails(binding: ContentAutofillSaveNewCredentialsBinding) {
        val originalUrl = getOriginalUrl()
        val url = originalUrl.extractDomain() ?: originalUrl

        binding.siteName.text = url

        lifecycleScope.launch {
            faviconManager.loadToViewFromLocalOrFallback(url = url, view = binding.favicon)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.saveLoginButton).setOnClickListener {

            val result = Bundle().also {
                it.putString(CredentialSavePickerDialog.KEY_URL, getOriginalUrl())
                it.putParcelable(CredentialSavePickerDialog.KEY_CREDENTIALS, getCredentialsToSave())
            }
            parentFragment?.setFragmentResult(CredentialSavePickerDialog.RESULT_KEY_CREDENTIAL_RESULT_SAVE, result)
            dismiss()
        }

        view.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dismiss()
        }

    }

    private fun getCredentialsToSave() = arguments?.getParcelable<LoginCredentials>(
        CredentialSavePickerDialog.KEY_CREDENTIALS)!!

    private fun getOriginalUrl() = arguments?.getString(CredentialSavePickerDialog.KEY_URL)!!

    override fun asDialogFragment(): DialogFragment = this

    companion object {

        fun instance(url: String, credentials: LoginCredentials): AutofillSavingCredentialsDialogFragment {

            val fragment = AutofillSavingCredentialsDialogFragment()
            fragment.arguments =
                Bundle().also {
                    it.putString(CredentialSavePickerDialog.KEY_URL, url)
                    it.putParcelable(CredentialSavePickerDialog.KEY_CREDENTIALS, credentials)
                }
            return fragment
        }
    }
}
