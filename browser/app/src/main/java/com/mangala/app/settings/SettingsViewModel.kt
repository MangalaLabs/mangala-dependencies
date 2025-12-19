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


package com.mangala.app.settings

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangala.app.browser.defaultbrowsing.DefaultBrowserDetector
import com.mangala.app.email.EmailManager
import com.mangala.app.fire.FireAnimationLoader
import com.mangala.app.pixels.AppPixelName.*
import com.mangala.app.settings.clear.ClearWhatOption
import com.mangala.app.settings.clear.ClearWhenOption
import com.mangala.app.settings.clear.FireAnimation
import com.mangala.app.settings.clear.getPixelValue
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.statistics.pixels.Pixel.PixelName
import com.mangala.app.statistics.pixels.Pixel.PixelParameter.FIRE_ANIMATION
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.autofill.InternalTestUserChecker
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.macos_api.MacOsWaitlist
import com.mangala.macos_api.MacWaitlistState
import com.mangala.mobile.android.ui.MangalaTheme
import com.mangala.mobile.android.ui.store.ThemingDataStore
import com.mangala.privacy.config.api.Gpc
import com.mangala.privacy.config.api.PrivacyFeatureName
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class SettingsViewModel (
    private val themingDataStore: ThemingDataStore,
    private val settingsDataStore: SettingsDataStore,
    private val defaultWebBrowserCapability: DefaultBrowserDetector,
    private val variantManager: VariantManager,
    private val fireAnimationLoader: FireAnimationLoader,
    private val gpc: Gpc,
    private val featureToggle: FeatureToggle,
    private val pixel: Pixel,
    private val appBuildConfig: AppBuildConfig,
    private val emailManager: EmailManager,
    private val macOsWaitlist: MacOsWaitlist,
    private val internalTestUserChecker: InternalTestUserChecker
) : ViewModel(), DefaultLifecycleObserver {

    private var deviceShieldStatePollingJob: Job? = null

    data class ViewState(
        val loading: Boolean = true,
        val version: String = "",
        val theme: MangalaTheme = MangalaTheme.LIGHT,
        val autoCompleteSuggestionsEnabled: Boolean = true,
        val showDefaultBrowserSetting: Boolean = false,
        val isAppDefaultBrowser: Boolean = false,
        val selectedFireAnimation: FireAnimation = FireAnimation.HeroFire,
        val automaticallyClearData: AutomaticallyClearData = AutomaticallyClearData(ClearWhatOption.CLEAR_NONE, ClearWhenOption.APP_EXIT_ONLY),
        val globalPrivacyControlEnabled: Boolean = false,
        val appLinksSettingType: AppLinkSettingType = AppLinkSettingType.ASK_EVERYTIME,
        val appTrackingProtectionEnabled: Boolean = false,
        val emailAddress: String? = null,
        val macOsWaitlistState: MacWaitlistState = MacWaitlistState.NotJoinedQueue,
        val showAutofill: Boolean = false
    )

    data class AutomaticallyClearData(
        val clearWhatOption: ClearWhatOption,
        val clearWhenOption: ClearWhenOption,
        val clearWhenOptionEnabled: Boolean = true
    )

    sealed class Command {
        object LaunchDefaultBrowser : Command()
        object LaunchEmailProtection : Command()
        object LaunchFeedback : Command()
        object LaunchFireproofWebsites : Command()
        object LaunchAutofillSettings : Command()
        object LaunchAccessibilitySettings : Command()
        object LaunchLocation : Command()
        object LaunchWhitelist : Command()
        object LaunchAddHomeScreenWidget : Command()
        data class LaunchFireAnimationSettings(val animation: FireAnimation) : Command()
        data class LaunchThemeSettings(val theme: MangalaTheme) : Command()
        data class LaunchAppLinkSettings(val appLinksSettingType: AppLinkSettingType) : Command()
        object LaunchGlobalPrivacyControl : Command()
        object UpdateTheme : Command()
        data class ShowClearWhatDialog(val option: ClearWhatOption) : Command()
        data class ShowClearWhenDialog(val option: ClearWhenOption) : Command()
        object LaunchMacOs : Command()
    }

    private val viewState = MutableStateFlow(ViewState())

    private val command = Channel<Command>(1, BufferOverflow.DROP_OLDEST)

    init {
        pixel.fire(SETTINGS_OPENED)
    }

    fun start() {
        val defaultBrowserAlready = defaultWebBrowserCapability.isDefaultBrowser()
        val variant = variantManager.getVariant()
        val savedTheme = themingDataStore.theme
        val automaticallyClearWhat = settingsDataStore.automaticallyClearWhatOption
        val automaticallyClearWhen = settingsDataStore.automaticallyClearWhenOption
        val automaticallyClearWhenEnabled = isAutomaticallyClearingDataWhenSettingEnabled(automaticallyClearWhat)

        viewModelScope.launch {
            viewState.emit(
                currentViewState().copy(
                    loading = false,
                    theme = savedTheme,
                    autoCompleteSuggestionsEnabled = settingsDataStore.autoCompleteSuggestionsEnabled,
                    isAppDefaultBrowser = defaultBrowserAlready,
                    showDefaultBrowserSetting = defaultWebBrowserCapability.deviceSupportsDefaultBrowserConfiguration(),
                    version = obtainVersion(variant.key),
                    automaticallyClearData = AutomaticallyClearData(automaticallyClearWhat, automaticallyClearWhen, automaticallyClearWhenEnabled),
                    selectedFireAnimation = settingsDataStore.selectedFireAnimation,
                    globalPrivacyControlEnabled = gpc.isEnabled() && featureToggle.isFeatureEnabled(
                        PrivacyFeatureName.GpcFeatureName) == true,
                    appLinksSettingType = getAppLinksSettingsState(settingsDataStore.appLinksEnabled, settingsDataStore.showAppLinksPrompt),
                    emailAddress = emailManager.getEmailAddress(),
                    macOsWaitlistState = macOsWaitlist.getWaitlistState(),
                    showAutofill = internalTestUserChecker.isInternalTestUser
                )
            )
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        deviceShieldStatePollingJob?.cancel()
    }

    fun viewState(): StateFlow<ViewState> {
        return viewState
    }

    fun commands(): Flow<Command> {
        return command.receiveAsFlow()
    }

    fun userRequestedToAddHomeScreenWidget() {
        viewModelScope.launch { command.send(Command.LaunchAddHomeScreenWidget) }
    }

    fun userRequestedToChangeFireAnimation() {
        viewModelScope.launch { command.send(Command.LaunchFireAnimationSettings(viewState.value.selectedFireAnimation)) }
        pixel.fire(FIRE_ANIMATION_SETTINGS_OPENED)
    }

    fun onAccessibilitySettingClicked() {
        viewModelScope.launch { command.send(Command.LaunchAccessibilitySettings) }
    }

    fun userRequestedToChangeTheme() {
        viewModelScope.launch { command.send(Command.LaunchThemeSettings(viewState.value.theme)) }
        pixel.fire(SETTINGS_THEME_OPENED)
    }

    fun userRequestedToChangeAppLinkSetting() {
        viewModelScope.launch { command.send(Command.LaunchAppLinkSettings(viewState.value.appLinksSettingType)) }
        pixel.fire(SETTINGS_APP_LINKS_PRESSED)
    }

    fun onFireproofWebsitesClicked() {
        viewModelScope.launch { command.send(Command.LaunchFireproofWebsites) }
    }

    fun onAutofillSettingsClick() {
        viewModelScope.launch { command.send(Command.LaunchAutofillSettings) }
    }

    fun onLocationClicked() {
        viewModelScope.launch { command.send(Command.LaunchLocation) }
    }

    fun onAutomaticallyClearWhatClicked() {
        viewModelScope.launch { command.send(Command.ShowClearWhatDialog(viewState.value.automaticallyClearData.clearWhatOption)) }
    }

    fun onAutomaticallyClearWhenClicked() {
        viewModelScope.launch { command.send(Command.ShowClearWhenDialog(viewState.value.automaticallyClearData.clearWhenOption)) }
    }

    fun onGlobalPrivacyControlClicked() {
        viewModelScope.launch { command.send(Command.LaunchGlobalPrivacyControl) }
    }

    fun onEmailProtectionSettingClicked() {
        viewModelScope.launch { command.send(Command.LaunchEmailProtection) }
    }

    fun onMacOsSettingClicked() {
        viewModelScope.launch { command.send(Command.LaunchMacOs) }
    }

    fun onDefaultBrowserToggled(enabled: Boolean) {
        Timber.i("User toggled default browser, is now enabled: $enabled")
        val defaultBrowserSelected = defaultWebBrowserCapability.isDefaultBrowser()
        if (enabled && defaultBrowserSelected) return
        viewModelScope.launch {
            viewState.emit(currentViewState().copy(isAppDefaultBrowser = enabled))
            command.send(Command.LaunchDefaultBrowser)
        }
    }

    fun onAutocompleteSettingChanged(enabled: Boolean) {
        Timber.i("User changed autocomplete setting, is now enabled: $enabled")
        settingsDataStore.autoCompleteSuggestionsEnabled = enabled
        viewModelScope.launch { viewState.emit(currentViewState().copy(autoCompleteSuggestionsEnabled = enabled)) }
    }

    fun onAppLinksSettingChanged(appLinkSettingType: AppLinkSettingType) {
        Timber.i("User changed app links setting, is now: ${appLinkSettingType.name}")

        val pixelName =
            when (appLinkSettingType) {
                AppLinkSettingType.ASK_EVERYTIME -> {
                    settingsDataStore.appLinksEnabled = true
                    settingsDataStore.showAppLinksPrompt = true
                    SETTINGS_APP_LINKS_ASK_EVERY_TIME_SELECTED
                }
                AppLinkSettingType.ALWAYS -> {
                    settingsDataStore.appLinksEnabled = true
                    settingsDataStore.showAppLinksPrompt = false
                    SETTINGS_APP_LINKS_ALWAYS_SELECTED
                }
                AppLinkSettingType.NEVER -> {
                    settingsDataStore.appLinksEnabled = false
                    settingsDataStore.showAppLinksPrompt = false
                    SETTINGS_APP_LINKS_NEVER_SELECTED
                }
            }
        viewModelScope.launch { viewState.emit(currentViewState().copy(appLinksSettingType = appLinkSettingType)) }

        pixel.fire(pixelName)
    }

    private fun getAppLinksSettingsState(
        appLinksEnabled: Boolean,
        showAppLinksPrompt: Boolean
    ): AppLinkSettingType {
        return if (appLinksEnabled) {
            if (showAppLinksPrompt) {
                AppLinkSettingType.ASK_EVERYTIME
            } else {
                AppLinkSettingType.ALWAYS
            }
        } else {
            AppLinkSettingType.NEVER
        }
    }

    private fun obtainVersion(variantKey: String): String {
        val formattedVariantKey = if (variantKey.isBlank()) " " else " $variantKey "
        return "${appBuildConfig.versionName}$formattedVariantKey(${appBuildConfig.versionCode})"
    }

    fun onAutomaticallyWhatOptionSelected(clearWhatNewSetting: ClearWhatOption) {
        if (settingsDataStore.isCurrentlySelected(clearWhatNewSetting)) {
            Timber.v("User selected same thing they already have set: $clearWhatNewSetting; no need to do anything else")
            return
        }

        pixel.fire(clearWhatNewSetting.pixelEvent())

        settingsDataStore.automaticallyClearWhatOption = clearWhatNewSetting

        viewModelScope.launch {
            viewState.emit(
                currentViewState().copy(
                    automaticallyClearData = AutomaticallyClearData(
                        clearWhatOption = clearWhatNewSetting,
                        clearWhenOption = settingsDataStore.automaticallyClearWhenOption,
                        clearWhenOptionEnabled = isAutomaticallyClearingDataWhenSettingEnabled(clearWhatNewSetting)
                    )
                )
            )
        }
    }

    private fun isAutomaticallyClearingDataWhenSettingEnabled(clearWhatOption: ClearWhatOption?): Boolean {
        return clearWhatOption != null && clearWhatOption != ClearWhatOption.CLEAR_NONE
    }

    fun onAutomaticallyWhenOptionSelected(clearWhenNewSetting: ClearWhenOption) {
        if (settingsDataStore.isCurrentlySelected(clearWhenNewSetting)) {
            Timber.v("User selected same thing they already have set: $clearWhenNewSetting; no need to do anything else")
            return
        }

        clearWhenNewSetting.pixelEvent()?.let {
            pixel.fire(it)
        }

        settingsDataStore.automaticallyClearWhenOption = clearWhenNewSetting
        viewModelScope.launch {
            viewState.emit(
                currentViewState().copy(
                    automaticallyClearData = AutomaticallyClearData(
                        settingsDataStore.automaticallyClearWhatOption,
                        clearWhenNewSetting
                    )
                )
            )
        }
    }

    fun onThemeSelected(selectedTheme: MangalaTheme) {
        Timber.d("User toggled theme, theme to set: $selectedTheme")
        if (themingDataStore.isCurrentlySelected(selectedTheme)) {
            Timber.d("User selected same theme they've already set: $selectedTheme; no need to do anything else")
            return
        }
        themingDataStore.theme = selectedTheme
        viewModelScope.launch {
            viewState.emit(currentViewState().copy(theme = selectedTheme))
            command.send(Command.UpdateTheme)
        }

        val pixelName =
            when (selectedTheme) {
                MangalaTheme.LIGHT -> SETTINGS_THEME_TOGGLED_LIGHT
                MangalaTheme.DARK -> SETTINGS_THEME_TOGGLED_DARK
                MangalaTheme.SYSTEM_DEFAULT -> SETTINGS_THEME_TOGGLED_SYSTEM_DEFAULT
            }
        pixel.fire(pixelName)
    }

    fun onFireAnimationSelected(selectedFireAnimation: FireAnimation) {
        if (settingsDataStore.isCurrentlySelected(selectedFireAnimation)) {
            Timber.v("User selected same thing they already have set: $selectedFireAnimation; no need to do anything else")
            return
        }
        settingsDataStore.selectedFireAnimation = selectedFireAnimation
        fireAnimationLoader.preloadSelectedAnimation()
        viewModelScope.launch {
            viewState.emit(currentViewState().copy(selectedFireAnimation = selectedFireAnimation))
        }
        pixel.fire(FIRE_ANIMATION_NEW_SELECTED, mapOf(FIRE_ANIMATION to selectedFireAnimation.getPixelValue()))
    }

    fun onManageWhitelistSelected() {
        pixel.fire(SETTINGS_MANAGE_WHITELIST)
        viewModelScope.launch { command.send(Command.LaunchWhitelist) }
    }

    private fun currentViewState(): ViewState {
        return viewState.value
    }

    private fun ClearWhatOption.pixelEvent(): PixelName {
        return when (this) {
            ClearWhatOption.CLEAR_NONE -> AUTOMATIC_CLEAR_DATA_WHAT_OPTION_NONE
            ClearWhatOption.CLEAR_TABS_ONLY -> AUTOMATIC_CLEAR_DATA_WHAT_OPTION_TABS
            ClearWhatOption.CLEAR_TABS_AND_DATA -> AUTOMATIC_CLEAR_DATA_WHAT_OPTION_TABS_AND_DATA
        }
    }

    private fun ClearWhenOption.pixelEvent(): PixelName? {
        return when (this) {
            ClearWhenOption.APP_EXIT_ONLY -> AUTOMATIC_CLEAR_DATA_WHEN_OPTION_APP_EXIT_ONLY
            ClearWhenOption.APP_EXIT_OR_5_MINS -> AUTOMATIC_CLEAR_DATA_WHEN_OPTION_APP_EXIT_OR_5_MINS
            ClearWhenOption.APP_EXIT_OR_15_MINS -> AUTOMATIC_CLEAR_DATA_WHEN_OPTION_APP_EXIT_OR_15_MINS
            ClearWhenOption.APP_EXIT_OR_30_MINS -> AUTOMATIC_CLEAR_DATA_WHEN_OPTION_APP_EXIT_OR_30_MINS
            ClearWhenOption.APP_EXIT_OR_60_MINS -> AUTOMATIC_CLEAR_DATA_WHEN_OPTION_APP_EXIT_OR_60_MINS
            else -> null
        }
    }
}

enum class AppLinkSettingType {
    ASK_EVERYTIME,
    ALWAYS,
    NEVER
}
