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

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mangala.app.accessibility.AccessibilityActivity
import com.mangala.app.email.EmailManager
import com.mangala.app.email.ui.EmailProtectionActivity
import com.mangala.app.feedback.ui.common.FeedbackActivity
import com.mangala.app.fire.fireproofwebsite.ui.FireproofWebsitesActivity
import com.mangala.app.global.MangalaBrowserActivity
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.global.view.launchDefaultAppActivity
import com.mangala.app.ui.GlobalPrivacyControlActivity
import com.mangala.app.location.ui.LocationPermissionsActivity
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.privacy.ui.WhitelistActivity
import com.mangala.app.settings.SettingsViewModel.AutomaticallyClearData
import com.mangala.app.settings.SettingsViewModel.Command
import com.mangala.app.settings.clear.ClearWhatOption
import com.mangala.app.settings.clear.ClearWhenOption
import com.mangala.app.settings.clear.FireAnimation
import com.mangala.app.settings.extension.InternalFeaturePlugin
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.widget.AddWidgetLauncher
import com.mangala.autofill.ui.AutofillSettingsActivityLauncher
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.macos_api.MacWaitlistState
import com.mangala.macos_api.MacWaitlistState.*
import com.mangala.macos_impl.waitlist.ui.MacOsWaitlistActivity
import com.mangala.mobile.android.ui.MangalaTheme
import com.mangala.mobile.android.ui.sendThemeChangedBroadcast
import com.mangala.mobile.android.ui.view.quietlySetIsChecked
import com.mangala.mobile.android.ui.viewbinding.viewBinding
import com.schoolonair.wallet.browser.app.R
import com.schoolonair.wallet.browser.app.databinding.ActivitySettingsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity :
    MangalaBrowserActivity(),
    SettingsAutomaticallyClearWhatFragment.Listener,
    SettingsAutomaticallyClearWhenFragment.Listener,
    SettingsThemeSelectorFragment.Listener,
    SettingsAppLinksSelectorFragment.Listener,
    SettingsFireAnimationSelectorFragment.Listener {

    private val viewModel: SettingsViewModel  by viewModel()
    private val binding: ActivitySettingsBinding by viewBinding()

    val pixel: Pixel by inject()

    val internalFeaturePlugins: PluginPoint<InternalFeaturePlugin> by inject()

    val addWidgetLauncher: AddWidgetLauncher by inject()

    val appBuildConfig: AppBuildConfig by inject()

    val autofillSettingsActivityLauncher: AutofillSettingsActivityLauncher by inject()

    private val defaultBrowserChangeListener = OnCheckedChangeListener { _, isChecked ->
        viewModel.onDefaultBrowserToggled(isChecked)
    }

    private val autocompleteToggleListener = OnCheckedChangeListener { _, isChecked ->
        viewModel.onAutocompleteSettingChanged(isChecked)
    }

    private val viewsGeneral
        get() = binding.includeSettings.contentSettingsGeneral

    private val viewsPrivacy
        get() = binding.includeSettings.contentSettingsPrivacy

    private val viewsMore
        get() = binding.includeSettings.contentSettingsMore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.includeToolbar.findViewById<Toolbar>(R.id.toolbar))

        configureUiEventHandlers()
        configureAppLinksSettingVisibility()
        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        viewModel.start()
    }

    private fun configureUiEventHandlers() {
        with(viewsGeneral) {
            selectedThemeSetting.setOnClickListener { viewModel.userRequestedToChangeTheme() }
            autocompleteToggle.setOnCheckedChangeListener(autocompleteToggleListener)
            homeScreenWidgetSetting.setOnClickListener { viewModel.userRequestedToAddHomeScreenWidget() }
            selectedFireAnimationSetting.setOnClickListener { viewModel.userRequestedToChangeFireAnimation() }
            accessibilitySetting.setOnClickListener { viewModel.onAccessibilitySettingClicked() }
        }

        with(viewsPrivacy) {
            globalPrivacyControlSetting.setOnClickListener { viewModel.onGlobalPrivacyControlClicked() }
            fireproofWebsites.setOnClickListener { viewModel.onFireproofWebsitesClicked() }
            locationPermissions.setOnClickListener { viewModel.onLocationClicked() }
            automaticallyClearWhatSetting.setOnClickListener { viewModel.onAutomaticallyClearWhatClicked() }
            automaticallyClearWhenSetting.setOnClickListener { viewModel.onAutomaticallyClearWhenClicked() }
            whitelist.setOnClickListener { viewModel.onManageWhitelistSelected() }
            appLinksSetting.setOnClickListener { viewModel.userRequestedToChangeAppLinkSetting() }
        }

        with(viewsMore) {
            emailSetting.setOnClickListener { viewModel.onEmailProtectionSettingClicked() }
            macOsSetting.setOnClickListener { viewModel.onMacOsSettingClicked() }
        }
    }

    private fun configureAppLinksSettingVisibility() {
        if (appBuildConfig.sdkInt < Build.VERSION_CODES.N) {
            viewsPrivacy.appLinksSetting.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.viewState()
            .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
            .onEach { viewState ->
                viewState.let {
                    updateSelectedTheme(it.theme)
                    viewsGeneral.autocompleteToggle.quietlySetIsChecked(it.autoCompleteSuggestionsEnabled, autocompleteToggleListener)
                    updateAutomaticClearDataOptions(it.automaticallyClearData)
                    setGlobalPrivacyControlSetting(it.globalPrivacyControlEnabled)
                    updateSelectedFireAnimation(it.selectedFireAnimation)
                    updateAppLinkBehavior(it.appLinksSettingType)
                    updateEmailSubtitle(it.emailAddress)
                    updateMacOsSettings(it.macOsWaitlistState)
                    updateAutofill(it.showAutofill)
                }
            }.launchIn(lifecycleScope)

        viewModel.commands()
            .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
            .onEach { processCommand(it) }
            .launchIn(lifecycleScope)
    }

    private fun updateAutofill(autofillEnabled: Boolean) {
        if (autofillEnabled) {
            viewsPrivacy.autofill.visibility = View.VISIBLE
            viewsPrivacy.autofill.setOnClickListener { viewModel.onAutofillSettingsClick() }
        } else {
            viewsPrivacy.autofill.visibility = View.GONE
        }
    }

    private fun updateEmailSubtitle(emailAddress: String?) {
        val subtitle = emailAddress ?: getString(com.schoolonair.wallet.component.resources.R.string.settingsEmailProtectionSubtitle)
        viewsMore.emailSetting.setSubtitle(subtitle)
    }

    private fun setGlobalPrivacyControlSetting(enabled: Boolean) {
        val stateText = if (enabled) {
            getString(com.schoolonair.wallet.component.resources.R.string.enabled)
        } else {
            getString(com.schoolonair.wallet.component.resources.R.string.disabled)
        }
        viewsPrivacy.globalPrivacyControlSetting.setSubtitle(stateText)
    }

    private fun updateSelectedFireAnimation(fireAnimation: FireAnimation) {
        val subtitle = getString(fireAnimation.nameResId)
        viewsGeneral.selectedFireAnimationSetting.setSubtitle(subtitle)
    }

    private fun updateSelectedTheme(selectedTheme: MangalaTheme) {
        val subtitle = getString(
            when (selectedTheme) {
                MangalaTheme.DARK -> com.schoolonair.wallet.component.resources.R.string.settingsDarkTheme
                MangalaTheme.LIGHT -> com.schoolonair.wallet.component.resources.R.string.settingsLightTheme
                MangalaTheme.SYSTEM_DEFAULT -> com.schoolonair.wallet.component.resources.R.string.settingsSystemTheme
            }
        )
        viewsGeneral.selectedThemeSetting.setSubtitle(subtitle)
    }

    private fun updateAppLinkBehavior(appLinkSettingType: AppLinkSettingType) {
        val subtitle = getString(
            when (appLinkSettingType) {
                AppLinkSettingType.ASK_EVERYTIME -> com.schoolonair.wallet.component.resources.R.string.settingsAppLinksAskEveryTime
                AppLinkSettingType.ALWAYS -> com.schoolonair.wallet.component.resources.R.string.settingsAppLinksAlways
                AppLinkSettingType.NEVER -> com.schoolonair.wallet.component.resources.R.string.settingsAppLinksNever
            }
        )
        viewsPrivacy.appLinksSetting.setSubtitle(subtitle)
    }

    private fun updateAutomaticClearDataOptions(automaticallyClearData: AutomaticallyClearData) {
        val clearWhatSubtitle = getString(automaticallyClearData.clearWhatOption.nameStringResourceId())
        viewsPrivacy.automaticallyClearWhatSetting.setSubtitle(clearWhatSubtitle)

        val clearWhenSubtitle = getString(automaticallyClearData.clearWhenOption.nameStringResourceId())
        viewsPrivacy.automaticallyClearWhenSetting.setSubtitle(clearWhenSubtitle)

        val whenOptionEnabled = automaticallyClearData.clearWhenOptionEnabled
        viewsPrivacy.automaticallyClearWhenSetting.isEnabled = whenOptionEnabled
    }

    private fun launchAutomaticallyClearWhatDialog(option: ClearWhatOption) {
        val dialog = SettingsAutomaticallyClearWhatFragment.create(option)
        dialog.show(supportFragmentManager, CLEAR_WHAT_DIALOG_TAG)
        pixel.fire(AppPixelName.AUTOMATIC_CLEAR_DATA_WHAT_SHOWN)
    }

    private fun launchAutomaticallyClearWhenDialog(option: ClearWhenOption) {
        val dialog = SettingsAutomaticallyClearWhenFragment.create(option)
        dialog.show(supportFragmentManager, CLEAR_WHEN_DIALOG_TAG)
        pixel.fire(AppPixelName.AUTOMATIC_CLEAR_DATA_WHEN_SHOWN)
    }

    private fun processCommand(it: Command?) {
        when (it) {
            is Command.LaunchDefaultBrowser -> launchDefaultAppScreen()
            is Command.LaunchFeedback -> launchFeedback()
            is Command.LaunchFireproofWebsites -> launchFireproofWebsites()
            is Command.LaunchAutofillSettings -> launchAutofillSettings()
            is Command.LaunchAccessibilitySettings -> launchAccessibilitySettings()
            is Command.LaunchLocation -> launchLocation()
            is Command.LaunchWhitelist -> launchWhitelist()
            is Command.LaunchGlobalPrivacyControl -> launchGlobalPrivacyControl()
            is Command.UpdateTheme -> sendThemeChangedBroadcast()
            is Command.LaunchEmailProtection -> launchEmailProtectionScreen()
            is Command.LaunchThemeSettings -> launchThemeSelector(it.theme)
            is Command.LaunchAppLinkSettings -> launchAppLinksSettingSelector(it.appLinksSettingType)
            is Command.LaunchFireAnimationSettings -> launchFireAnimationSelector(it.animation)
            is Command.ShowClearWhatDialog -> launchAutomaticallyClearWhatDialog(it.option)
            is Command.ShowClearWhenDialog -> launchAutomaticallyClearWhenDialog(it.option)
            is Command.LaunchAddHomeScreenWidget -> launchAddHomeScreenWidget()
            is Command.LaunchMacOs -> launchMacOsScreen()
            null -> TODO()
        }
    }

    private fun updateDeviceShieldSettings(
        appTPEnabled: Boolean,
        waitlistState: EmailManager.WaitlistState
    ) {
        with(viewsMore) {
            if (waitlistState != EmailManager.WaitlistState.InBeta) {
                vpnSetting.setSubtitle(getString(com.schoolonair.wallet.component.resources.R.string.atp_SettingsDeviceShieldNeverEnabled))
            } else {
                if (appTPEnabled) {
                    vpnSetting.setSubtitle(getString(com.schoolonair.wallet.component.resources.R.string.atp_SettingsDeviceShieldEnabled))
                } else {
                    vpnSetting.setSubtitle(getString(com.schoolonair.wallet.component.resources.R.string.atp_SettingsDeviceShieldDisabled))
                }
            }
        }
    }

    private fun updateMacOsSettings(waitlistState: MacWaitlistState) {
        with(viewsMore) {
            when (waitlistState) {
                InBeta -> macOsSetting.setSubtitle(getString(com.schoolonair.wallet.component.resources.R.string.macos_settings_description_ready))
                JoinedWaitlist -> macOsSetting.setSubtitle(getString(com.schoolonair.wallet.component.resources.R.string.macos_settings_description_list))
                NotJoinedQueue -> macOsSetting.setSubtitle(getString(com.schoolonair.wallet.component.resources.R.string.macos_settings_description))
            }
        }
    }

    @Suppress("NewApi") // we use appBuildConfig
    private fun launchDefaultAppScreen() {
        if (appBuildConfig.sdkInt >= Build.VERSION_CODES.N) {
            launchDefaultAppActivity()
        } else {
            throw IllegalStateException("Unable to launch default app activity on this OS")
        }
    }

    private fun launchFeedback() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivityForResult(Intent(FeedbackActivity.intent(this)), FEEDBACK_REQUEST_CODE, options)
    }

    private fun launchFireproofWebsites() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(FireproofWebsitesActivity.intent(this), options)
    }

    private fun launchAutofillSettings() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(autofillSettingsActivityLauncher.intent(this), options)
    }

    private fun launchAccessibilitySettings() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(AccessibilityActivity.intent(this), options)
    }

    private fun launchLocation() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(LocationPermissionsActivity.intent(this), options)
    }

    private fun launchWhitelist() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(WhitelistActivity.intent(this), options)
    }

    private fun launchFireAnimationSelector(animation: FireAnimation) {
        val dialog = SettingsFireAnimationSelectorFragment.create(animation)
        dialog.show(supportFragmentManager, FIRE_ANIMATION_SELECTOR_TAG)
    }

    private fun launchThemeSelector(theme: MangalaTheme) {
        val dialog = SettingsThemeSelectorFragment.create(theme)
        dialog.show(supportFragmentManager, THEME_SELECTOR_TAG)
    }

    private fun launchAppLinksSettingSelector(appLinkSettingType: AppLinkSettingType) {
        val dialog = SettingsAppLinksSelectorFragment.create(appLinkSettingType)
        dialog.show(supportFragmentManager, THEME_SELECTOR_TAG)
    }

    private fun launchGlobalPrivacyControl() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(GlobalPrivacyControlActivity.intent(this), options)
    }

    private fun launchEmailProtectionScreen() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(EmailProtectionActivity.intent(this), options)
    }

    private fun launchMacOsScreen() {
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(MacOsWaitlistActivity.intent(this), options)
    }

    private fun launchAddHomeScreenWidget() {
        pixel.fire(AppPixelName.SETTINGS_ADD_HOME_SCREEN_WIDGET_CLICKED)
        addWidgetLauncher.launchAddWidget(this)
    }

    override fun onThemeSelected(selectedTheme: MangalaTheme) {
        viewModel.onThemeSelected(selectedTheme)
    }

    override fun onAppLinkSettingSelected(selectedSetting: AppLinkSettingType) {
        viewModel.onAppLinksSettingChanged(selectedSetting)
    }

    override fun onAutomaticallyClearWhatOptionSelected(clearWhatSetting: ClearWhatOption) {
        viewModel.onAutomaticallyWhatOptionSelected(clearWhatSetting)
    }

    override fun onAutomaticallyClearWhenOptionSelected(clearWhenSetting: ClearWhenOption) {
        viewModel.onAutomaticallyWhenOptionSelected(clearWhenSetting)
    }

    override fun onFireAnimationSelected(selectedFireAnimation: FireAnimation) {
        viewModel.onFireAnimationSelected(selectedFireAnimation)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        when (requestCode) {
            FEEDBACK_REQUEST_CODE -> handleFeedbackResult(resultCode)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFeedbackResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, com.schoolonair.wallet.component.resources.R.string.thanksForTheFeedback, Toast.LENGTH_LONG).show()
        }
    }

    @StringRes
    private fun ClearWhatOption.nameStringResourceId(): Int {
        return when (this) {
            ClearWhatOption.CLEAR_NONE -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhatOptionNone
            ClearWhatOption.CLEAR_TABS_ONLY -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhatOptionTabs
            ClearWhatOption.CLEAR_TABS_AND_DATA -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhatOptionTabsAndData
        }
    }

    @StringRes
    private fun ClearWhenOption.nameStringResourceId(): Int {
        return when (this) {
            ClearWhenOption.APP_EXIT_ONLY -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhenAppExitOnly
            ClearWhenOption.APP_EXIT_OR_5_MINS -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhenAppExit5Minutes
            ClearWhenOption.APP_EXIT_OR_15_MINS -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhenAppExit15Minutes
            ClearWhenOption.APP_EXIT_OR_30_MINS -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhenAppExit30Minutes
            ClearWhenOption.APP_EXIT_OR_60_MINS -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhenAppExit60Minutes
            ClearWhenOption.APP_EXIT_OR_5_SECONDS -> com.schoolonair.wallet.component.resources.R.string.settingsAutomaticallyClearWhenAppExit5Seconds
        }
    }

    companion object {
        private const val FIRE_ANIMATION_SELECTOR_TAG = "FIRE_ANIMATION_SELECTOR_DIALOG_FRAGMENT"
        private const val THEME_SELECTOR_TAG = "THEME_SELECTOR_DIALOG_FRAGMENT"
        private const val CLEAR_WHAT_DIALOG_TAG = "CLEAR_WHAT_DIALOG_FRAGMENT"
        private const val CLEAR_WHEN_DIALOG_TAG = "CLEAR_WHEN_DIALOG_FRAGMENT"
        private const val FEEDBACK_REQUEST_CODE = 100
        private const val CHANGE_APP_ICON_REQUEST_CODE = 101
        private const val PRIVACY_POLICY_WEB_LINK = "https://mangala.com/privacy"

        fun intent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}
