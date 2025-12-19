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



package com.mangala.autofill.ui.credential.management.viewing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.autofill.domain.app.LoginCredentials
import com.mangala.autofill.impl.databinding.FragmentAutofillManagementEditModeBinding
import com.mangala.autofill.ui.credential.management.AutofillSettingsViewModel
import com.mangala.autofill.ui.credential.management.AutofillSettingsViewModel.Command
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

//@AndroidEntryPoint
class AutofillManagementEditMode : Fragment(), KoinComponent {

//    @Inject
    val faviconManager: FaviconManager by inject()

    val viewModel: AutofillSettingsViewModel by activityViewModels()

    private lateinit var binding: FragmentAutofillManagementEditModeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAutofillManagementEditModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        populateFields(getCredentials())
        configureUiEventHandlers()
    }

    private fun configureUiEventHandlers() {
        binding.saveButton.setOnClickListener { saveCredentials() }
        binding.deleteButton.setOnClickListener { deleteCredentials() }
        binding.copyUsernameButton.setOnClickListener { copyUsername() }
        binding.copyPasswordButton.setOnClickListener { copyPassword() }
    }

    private fun copyUsername() {
        viewModel.onCopyUsername(binding.usernameEditText.text.toString())
    }

    private fun copyPassword() {
        viewModel.onCopyPassword(binding.passwordEditText.text.toString())
    }

    private fun saveCredentials() {
        val updatedCredentials = getCredentials().copy(
            username = binding.usernameEditText.text.toString().convertBlankToNull(),
            password = binding.passwordEditText.text.toString().convertBlankToNull(),

        )
        viewModel.updateCredentials(updatedCredentials)
    }

    private fun deleteCredentials() {
        viewModel.onDeleteCredentials(getCredentials())
    }

    private fun populateFields(credentials: LoginCredentials) {
        binding.usernameEditText.setText(credentials.username)
        binding.passwordEditText.setText(credentials.password)
        binding.domainEditText.setText(credentials.domain)
    }

    private fun getCredentials(): LoginCredentials {
        return requireArguments().getParcelable(EXTRA_KEY_CREDENTIALS)!!
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                }
            }
        }
    }

    private fun String.convertBlankToNull(): String? = this.ifBlank { null }

    private fun processCommand(command: Command) {
        var processed = true
        when (command) {
            else -> processed = false
        }
        if (processed) {
            Timber.v("Processed command $command")
            viewModel.commandProcessed(command)
        }
    }

    companion object {

        private const val EXTRA_KEY_CREDENTIALS = "credentials"

        fun instance(credentials: LoginCredentials) =
            AutofillManagementEditMode().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_KEY_CREDENTIALS, credentials)
                }
            }
    }
}
