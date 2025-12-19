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



package com.mangala.autofill.ui.credential.management

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.autofill.domain.app.LoginCredentials
import com.mangala.autofill.impl.R
import com.mangala.autofill.impl.databinding.ActivityAutofillSettingsBinding
import com.mangala.autofill.ui.AutofillSettingsActivityLauncher
import com.mangala.autofill.ui.credential.management.AutofillSettingsViewModel.Command.*
import com.mangala.autofill.ui.credential.management.viewing.AutofillManagementDisabledMode
import com.mangala.autofill.ui.credential.management.viewing.AutofillManagementEditMode
import com.mangala.autofill.ui.credential.management.viewing.AutofillManagementLockedMode
import com.mangala.autofill.ui.credential.management.viewing.AutofillManagementListMode
import com.mangala.deviceauth.api.DeviceAuthenticator
import com.mangala.deviceauth.api.DeviceAuthenticator.AuthResult
import com.mangala.deviceauth.api.DeviceAuthenticator.Features.AUTOFILL
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class AutofillManagementActivity : MangalaBrowserActivity(), KoinComponent {

    private val binding: ActivityAutofillSettingsBinding by viewBinding()
    private val viewModel: AutofillSettingsViewModel by viewModels()

    private var inEditMode: Boolean = false

//    @Inject
    val deviceAuthenticator: DeviceAuthenticator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(binding.root)
        setupToolbar(binding.includeToolbar.findViewById(com.mangala.mobile.android.R.id.toolbar))
        observeViewModel()
        setTitle(R.string.managementScreenTitle)
    }

    override fun onStart() {
        super.onStart()
        viewModel.launchDeviceAuth()
    }

    override fun onStop() {
        super.onStop()
        viewModel.lock()
    }

    private fun launchDeviceAuth() {
        if (deviceAuthenticator.hasValidDeviceAuthentication()) {
            deviceAuthenticator.authenticate(AUTOFILL, this) {
                if (it == AuthResult.Success) {
                    viewModel.unlock()
                    showListMode()
                } else {
                    finish()
                }
            }
        } else {
            viewModel.disabled()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { state ->
                    processState(state)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.commands.collect { commands ->
                    commands.forEach { processCommand(it) }
                }
            }
        }
    }

    private fun processState(state: AutofillSettingsViewModel.ViewState) {
        if (state.isLocked) {
            showLockMode()
        }
    }

    private fun processCommand(command: AutofillSettingsViewModel.Command) {
        var processed = true
        when (command) {
            is ShowListMode -> showListMode()
            is ShowEditMode -> showEditMode(command.credentials)
            is ShowUserUsernameCopied -> showCopiedToClipboardSnackbar("Username")
            is ShowUserPasswordCopied -> showCopiedToClipboardSnackbar("Password")
            is ShowDisabledMode -> showDisabledMode()
            is LaunchDeviceAuth -> launchDeviceAuth()
            else -> processed = false
        }
        if (processed) {
            Timber.v("Processed command $command")
            viewModel.commandProcessed(command)
        }
    }

    private fun showCopiedToClipboardSnackbar(type: String) {
        Snackbar.make(binding.root, "$type copied to clipboard", Snackbar.LENGTH_SHORT).show()
    }

    private fun showListMode() {
        Timber.e("Show view mode")
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, AutofillManagementListMode.instance())
        }
        inEditMode = false
    }

    private fun showEditMode(credentials: LoginCredentials) {
        Timber.e("Show edit mode")
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, AutofillManagementEditMode.instance(credentials))
        }

        inEditMode = true
    }

    private fun showLockMode() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(
                R.id.fragment_container_view,
                AutofillManagementLockedMode.instance()
            )
        }
        inEditMode = false
    }

    private fun showDisabledMode() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, AutofillManagementDisabledMode.instance())
        }
        inEditMode = false
    }

    override fun onBackPressed() {
        if (inEditMode) {
            showListMode()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        fun intent(context: Context): Intent {
            return Intent(context, AutofillManagementActivity::class.java)
        }
    }
}

//@InstallIn(SingletonComponent::class)
//@Module
//class AutofillSettingsModule {
//
//    @Provides
//    fun activityLauncher(): AutofillSettingsActivityLauncher {
//        return object : AutofillSettingsActivityLauncher {
//            override fun intent(context: Context): Intent {
//                return AutofillManagementActivity.intent(context)
//            }
//        }
//    }
//}
