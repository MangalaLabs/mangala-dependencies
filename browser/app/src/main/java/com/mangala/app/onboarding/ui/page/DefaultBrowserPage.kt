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

package com.mangala.app.onboarding.ui.page

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import com.mangala.app.browser.defaultbrowsing.DefaultBrowserSystemSettings
import com.mangala.mobile.android.ui.view.show
import com.mangala.app.statistics.VariantManager
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.navigation.BrowserActivityNavigationUtils.LAUNCH_FROM_DEFAULT_BROWSER_DIALOG
import com.schoolonair.wallet.browser.app.R
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DefaultBrowserPage : OnboardingPageFragment() {

    val variantManager: VariantManager by inject()

    val appBuildConfig: AppBuildConfig by inject()

    private var userTriedToSetDDGAsDefault = false
    private var userSelectedExternalBrowser = false
    private var toast: Toast? = null
    private var defaultCard: View? = null

    private val viewModel: DefaultBrowserPageViewModel by viewModel()

    override fun layoutResource(): Int = R.layout.content_onboarding_default_browser

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            applyStyle()
            viewModel.pageBecameVisible()
        }
    }

    private fun applyStyle() {
        activity?.window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.WHITE
        }
        ViewCompat.requestApplyInsets(view?.findViewById<View>(R.id.longDescriptionContainer) ?: View(requireContext()))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        defaultCard = activity?.findViewById(R.id.defaultCard)

        if (savedInstanceState != null) {
            userTriedToSetDDGAsDefault = savedInstanceState.getBoolean(SAVED_STATE_LAUNCHED_DEFAULT)
        }

        observeViewModel()

        setButtonsBehaviour()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUI()
    }

    override fun onStop() {
        super.onStop()
        userSelectedExternalBrowser = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_STATE_LAUNCHED_DEFAULT, userTriedToSetDDGAsDefault)
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            viewState?.let {
                when (it) {
                    is DefaultBrowserPageViewModel.ViewState.DefaultBrowserSettingsUI -> {
                        setUiForSettings()
                        hideInstructionsCard()
                    }
                    is DefaultBrowserPageViewModel.ViewState.DefaultBrowserDialogUI -> {
                        setUiForDialog()
                        if (it.showInstructionsCard) showInstructionsCard() else hideInstructionsCard()
                    }
                    is DefaultBrowserPageViewModel.ViewState.ContinueToBrowser -> {
                        hideInstructionsCard()
                        onContinuePressed()
                    }
                }
            }
        }

        viewModel.command.observe(viewLifecycleOwner) {
            when (it) {
                is DefaultBrowserPageViewModel.Command.OpenDialog -> onLaunchDefaultBrowserWithDialogClicked(it.url)
                is DefaultBrowserPageViewModel.Command.OpenSettings -> onLaunchDefaultBrowserSettingsClicked()
                is DefaultBrowserPageViewModel.Command.ContinueToBrowser -> {
                    hideInstructionsCard()
                    onContinuePressed()
                }
            }
        }
    }

    private fun setUiForDialog() {
        view?.findViewById<ImageView>(R.id.defaultBrowserImage)?.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.set_as_default_browser_illustration_dialog)
        view?.findViewById<TextView>(R.id.browserProtectionSubtitle)?.setText(com.schoolonair.wallet.component.resources.R.string.defaultBrowserDescriptionNoDefault)
        view?.findViewById<TextView>(R.id.browserProtectionTitle)?.setText(com.schoolonair.wallet.component.resources.R.string.onboardingDefaultBrowserTitle)
        view?.findViewById<Button>(R.id.launchSettingsButton)?.setText(com.schoolonair.wallet.component.resources.R.string.defaultBrowserLetsDoIt)
        setButtonsBehaviour()
    }

    private fun setUiForSettings() {
        view?.findViewById<ImageView>(R.id.defaultBrowserImage)?.setImageResource(com.schoolonair.wallet.component.resources.R.drawable.set_as_default_browser_illustration_settings)
        view?.findViewById<TextView>(R.id.browserProtectionSubtitle)?.setText(com.schoolonair.wallet.component.resources.R.string.onboardingDefaultBrowserDescription)
        view?.findViewById<TextView>(R.id.browserProtectionTitle)?.setText(com.schoolonair.wallet.component.resources.R.string.onboardingDefaultBrowserTitle)
        view?.findViewById<Button>(R.id.launchSettingsButton)?.setText(com.schoolonair.wallet.component.resources.R.string.defaultBrowserLetsDoIt)
        setButtonsBehaviour()
    }

    private fun setButtonsBehaviour() {
        view?.findViewById<Button>(R.id.launchSettingsButton)?.setOnClickListener {
            viewModel.onDefaultBrowserClicked()
        }
        view?.findViewById<Button>(R.id.continueButton)?.setOnClickListener {
            viewModel.onContinueToBrowser(userTriedToSetDDGAsDefault)
        }
    }

    @SuppressLint("InflateParams")
    private fun showInstructionsCard() {
        toast?.cancel()
        defaultCard?.show()
        defaultCard?.alpha = 1f

        val inflater = LayoutInflater.from(requireContext())
        val inflatedView = inflater.inflate(R.layout.content_onboarding_default_browser_card, null)

        toast = Toast(requireContext()).apply {
            view = inflatedView
            setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            duration = Toast.LENGTH_LONG
        }
        toast?.show()
    }

    private fun hideInstructionsCard() {
        toast?.cancel()
        defaultCard?.animate()?.alpha(0f)?.setDuration(100)?.start()
    }

    private fun onLaunchDefaultBrowserWithDialogClicked(url: String) {
        userTriedToSetDDGAsDefault = true
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.putExtra(LAUNCH_FROM_DEFAULT_BROWSER_DIALOG, true)
        startActivityForResult(intent, DEFAULT_BROWSER_REQUEST_CODE_DIALOG)
    }

    @Suppress("NewApi") // we use appBuildConfig
    private fun onLaunchDefaultBrowserSettingsClicked() {
        userTriedToSetDDGAsDefault = true
        if (appBuildConfig.sdkInt >= Build.VERSION_CODES.N) {
            val intent = DefaultBrowserSystemSettings.intent()
            try {
                startActivityForResult(intent, DEFAULT_BROWSER_REQUEST_CODE_SETTINGS)
            } catch (e: ActivityNotFoundException) {
                Timber.w(e, getString(com.schoolonair.wallet.component.resources.R.string.cannotLaunchDefaultAppSettings))
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        when (requestCode) {
            DEFAULT_BROWSER_REQUEST_CODE_SETTINGS -> {
                viewModel.handleResult(DefaultBrowserPageViewModel.Origin.Settings)
            }
            DEFAULT_BROWSER_REQUEST_CODE_DIALOG -> {
                val origin =
                    if (resultCode == DEFAULT_BROWSER_RESULT_CODE_DIALOG_INTERNAL) {
                        DefaultBrowserPageViewModel.Origin.InternalBrowser
                    } else {
                        if (userSelectedExternalBrowser) {
                            DefaultBrowserPageViewModel.Origin.ExternalBrowser
                        } else {
                            DefaultBrowserPageViewModel.Origin.DialogDismissed
                        }
                    }
                userSelectedExternalBrowser = false
                viewModel.handleResult(origin)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val DEFAULT_BROWSER_REQUEST_CODE_SETTINGS = 100
        private const val SAVED_STATE_LAUNCHED_DEFAULT = "SAVED_STATE_LAUNCHED_DEFAULT"
        const val DEFAULT_BROWSER_REQUEST_CODE_DIALOG = 101
        const val DEFAULT_BROWSER_RESULT_CODE_DIALOG_INTERNAL = 102
    }
}
