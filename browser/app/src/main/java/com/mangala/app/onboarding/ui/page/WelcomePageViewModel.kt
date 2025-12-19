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
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mangala.app.global.DefaultRoleBrowserDialog
import com.mangala.app.global.install.AppInstallStore
import com.mangala.app.pixels.AppPixelName
import com.mangala.app.statistics.pixels.Pixel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@SuppressLint("StaticFieldLeak")
class WelcomePageViewModel(
    private val appInstallStore: AppInstallStore,
    private val context: Context,
    private val pixel: Pixel,
    private val defaultRoleBrowserDialog: DefaultRoleBrowserDialog
) : ViewModel() {

    fun reduce(event: WelcomePageView.Event): Flow<WelcomePageView.State> {
        return when (event) {
            WelcomePageView.Event.OnPrimaryCtaClicked -> onPrimaryCtaClicked()
            WelcomePageView.Event.OnDefaultBrowserSet -> onDefaultBrowserSet()
            WelcomePageView.Event.OnDefaultBrowserNotSet -> onDefaultBrowserNotSet()
        }
    }

    private fun onPrimaryCtaClicked(): Flow<WelcomePageView.State> = flow {
        if (defaultRoleBrowserDialog.shouldShowDialog()) {
            val intent = defaultRoleBrowserDialog.createIntent(context)
            if (intent != null) {
                emit(WelcomePageView.State.ShowDefaultBrowserDialog(intent))
            } else {
                pixel.fire(AppPixelName.DEFAULT_BROWSER_DIALOG_NOT_SHOWN)
                emit(WelcomePageView.State.Finish)
            }
        } else {
            emit(WelcomePageView.State.Finish)
        }
    }

    private fun onDefaultBrowserSet(): Flow<WelcomePageView.State> = flow {
        defaultRoleBrowserDialog.dialogShown()

        appInstallStore.defaultBrowser = true

        pixel.fire(
            AppPixelName.DEFAULT_BROWSER_SET,
            mapOf(Pixel.PixelParameter.DEFAULT_BROWSER_SET_FROM_ONBOARDING to true.toString())
        )

        emit(WelcomePageView.State.Finish)
    }

    private fun onDefaultBrowserNotSet(): Flow<WelcomePageView.State> = flow {
        defaultRoleBrowserDialog.dialogShown()

        appInstallStore.defaultBrowser = false

        pixel.fire(
            AppPixelName.DEFAULT_BROWSER_NOT_SET,
            mapOf(Pixel.PixelParameter.DEFAULT_BROWSER_SET_FROM_ONBOARDING to true.toString())
        )

        emit(WelcomePageView.State.Finish)
    }
}

@Suppress("UNCHECKED_CAST")
class WelcomePageViewModelFactory(
    private val appInstallStore: AppInstallStore,
    private val context: Context,
    private val pixel: Pixel,
    private val defaultRoleBrowserDialog: DefaultRoleBrowserDialog
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return with(modelClass) {
            when {
                isAssignableFrom(WelcomePageViewModel::class.java) -> WelcomePageViewModel(
                    appInstallStore, context, pixel, defaultRoleBrowserDialog
                )
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
    }
}
