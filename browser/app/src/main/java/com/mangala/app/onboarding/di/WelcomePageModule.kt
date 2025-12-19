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

package com.mangala.app.onboarding.di

import android.content.Context
import com.mangala.app.global.DefaultRoleBrowserDialog
import com.mangala.app.global.RealDefaultRoleBrowserDialog
import com.mangala.app.global.install.AppInstallStore
import com.mangala.app.onboarding.ui.page.WelcomePageViewModelFactory
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.appbuildconfig.api.AppBuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//class WelcomePageModule {
//
//    @Provides
//    fun welcomePageViewModelFactory(
//        appInstallStore: AppInstallStore,
//        @ApplicationContext context: Context,
//        pixel: Pixel,
//        defaultRoleBrowserDialog: DefaultRoleBrowserDialog
//    ) = WelcomePageViewModelFactory(appInstallStore, context, pixel, defaultRoleBrowserDialog)
//
//    @Provides
//    fun defaultRoleBrowserDialog(
//        appInstallStore: AppInstallStore,
//        appBuildConfig: AppBuildConfig,
//    ): DefaultRoleBrowserDialog = RealDefaultRoleBrowserDialog(appInstallStore, appBuildConfig)
//}


val welcomePageModule = module {
    factory {
        RealDefaultRoleBrowserDialog(
            appInstallStore = get(),
            appBuildConfig = get()
        ) as DefaultRoleBrowserDialog
    }
    factory {
        WelcomePageViewModelFactory(
            appInstallStore = get(),
            context = androidContext(),
            pixel = get(),
            defaultRoleBrowserDialog = get()
        )
    }
}
