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

package com.mangala.app.accessibility

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangala.app.accessibility.data.AccessibilitySettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AccessibilitySettingsViewModel(
    private val accessibilitySettings: AccessibilitySettingsDataStore
) : ViewModel() {

    data class ViewState(
        val overrideSystemFontSize: Boolean = false,
        val appFontSize: Float = 100f,
        val forceZoom: Boolean = false
    )

    private val viewState = MutableStateFlow(ViewState())

    fun viewState(): StateFlow<ViewState> = viewState

    fun start() {
        viewModelScope.launch {
            viewState.emit(
                currentViewState().copy(
                    overrideSystemFontSize = accessibilitySettings.overrideSystemFontSize,
                    appFontSize = accessibilitySettings.appFontSize,
                    forceZoom = accessibilitySettings.forceZoom
                )
            )
        }
    }

    fun onForceZoomChanged(checked: Boolean) {
        Timber.v("AccessibilityActSettings: onForceZoomChanged $checked")
        accessibilitySettings.forceZoom = checked
        viewModelScope.launch {
            viewState.emit(
                currentViewState().copy(
                    forceZoom = accessibilitySettings.forceZoom
                )
            )
        }
    }

    fun onSystemFontSizeChanged(checked: Boolean) {
        Timber.v("AccessibilityActSettings: onOverrideSystemFontSizeChanged $checked")
        accessibilitySettings.overrideSystemFontSize = checked
        viewModelScope.launch {
            viewState.emit(
                currentViewState().copy(
                    overrideSystemFontSize = accessibilitySettings.overrideSystemFontSize
                )
            )
        }
    }

    fun onFontSizeChanged(newValue: Float) {
        Timber.v("AccessibilityActSettings: onFontSizeChanged $newValue")
        accessibilitySettings.appFontSize = newValue
        viewModelScope.launch {
            viewState.emit(
                currentViewState().copy(
                    appFontSize = accessibilitySettings.appFontSize
                )
            )
        }
    }

    private fun currentViewState() = viewState.value
}
