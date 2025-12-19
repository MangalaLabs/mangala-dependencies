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

package com.mangala.app.buildconfig

import android.os.Build
import com.mangala.ConstantBrowser
import com.mangala.appbuildconfig.api.AppBuildConfig
import com.mangala.appbuildconfig.api.BuildFlavor
import com.schoolonair.wallet.browser.app.BuildConfig
import java.lang.IllegalStateException
import java.util.*

class RealAppBuildConfig : AppBuildConfig {
    override val isDebug: Boolean = BuildConfig.DEBUG
    override val applicationId: String = ConstantBrowser.APPLICATION_ID
    override val buildType: String = BuildConfig.BUILD_TYPE
    override val versionCode: Int = ConstantBrowser.VERSION_CODE
    override val versionName: String = ConstantBrowser.VERSION_NAME
//    override val flavor: BuildFlavor
//        get() = when (BuildConfig.FLAVOR) {
//            "internal" -> BuildFlavor.INTERNAL
//            "fdroid" -> BuildFlavor.FDROID
//            "play" -> BuildFlavor.PLAY
//            else -> throw IllegalStateException("Unknown app flavor")
//        }

    override val flavor: BuildFlavor
        get() = BuildFlavor.PLAY

    override val sdkInt: Int = Build.VERSION.SDK_INT
    override val manufacturer: String = Build.MANUFACTURER
    override val model: String = Build.MODEL
    override val isTest by lazy {
        try {
            Class.forName("org.junit.Test")
            true
        } catch (e: Exception) {
            false
        }
    }
    override val deviceLocale: Locale
        get() = Locale.getDefault()
}
