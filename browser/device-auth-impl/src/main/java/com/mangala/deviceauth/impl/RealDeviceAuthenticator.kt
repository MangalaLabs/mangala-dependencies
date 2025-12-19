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



package com.mangala.deviceauth.impl

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.deviceauth.api.DeviceAuthenticator
import com.mangala.deviceauth.api.DeviceAuthenticator.AuthResult
import com.mangala.deviceauth.api.DeviceAuthenticator.Features

class RealDeviceAuthenticator(
    private val deviceAuthChecker: SupportedDeviceAuthChecker,
    private val appBuildConfig: AppBuildConfig,
    private val authLauncher: AuthLauncher
) : DeviceAuthenticator {

    override fun hasValidDeviceAuthentication(): Boolean {
        // https://developer.android.com/reference/androidx/biometric/BiometricManager#canAuthenticate(int)
        // BIOMETRIC_STRONG | DEVICE_CREDENTIAL is unsupported on API 28-29
        return if (appBuildConfig.sdkInt != Build.VERSION_CODES.Q && appBuildConfig.sdkInt != Build.VERSION_CODES.P) {
            deviceAuthChecker.supportsStrongAuthentication()
        } else {
            deviceAuthChecker.supportsLegacyAuthentication()
        }
    }

    override fun authenticate(
        featureToAuth: Features,
        fragment: Fragment,
        onResult: (AuthResult) -> Unit
    ) {
        authLauncher.launch(getAuthText(featureToAuth), fragment, onResult)
    }

    override fun authenticate(
        featureToAuth: Features,
        fragmentActivity: FragmentActivity,
        onResult: (AuthResult) -> Unit
    ) {
        authLauncher.launch(getAuthText(featureToAuth), fragmentActivity, onResult)
    }

    private fun getAuthText(
        feature: Features
    ): Int = when (feature) {
        Features.AUTOFILL -> com.schoolonair.wallet.component.resources.R.string.autofill_auth_text
    }
}
