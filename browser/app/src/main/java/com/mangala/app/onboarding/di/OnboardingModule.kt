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

import com.mangala.app.browser.defaultbrowsing.DefaultBrowserDetector
import com.mangala.app.global.DefaultRoleBrowserDialog
import com.mangala.app.onboarding.ui.OnboardingFragmentPageBuilder
import com.mangala.app.onboarding.ui.OnboardingPageBuilder
import com.mangala.app.onboarding.ui.OnboardingPageManager
import com.mangala.app.onboarding.ui.OnboardingPageManagerWithTrackerBlocking
import com.mangala.app.onboarding.ui.OnboardingViewModel
import com.mangala.app.onboarding.ui.page.DefaultBrowserPageViewModel
import com.mangala.app.onboarding.ui.page.WelcomePageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


//@Module
//@InstallIn(SingletonComponent::class)
//class OnboardingModule {
//
//    @Provides
//    fun onboardingPageManger(
//        defaultRoleBrowserDialog: DefaultRoleBrowserDialog,
//        onboardingPageBuilder: OnboardingPageBuilder,
//        defaultBrowserDetector: DefaultBrowserDetector,
//    ): OnboardingPageManager {
//        return OnboardingPageManagerWithTrackerBlocking(defaultRoleBrowserDialog, onboardingPageBuilder, defaultBrowserDetector)
//    }
//
//    @Provides
//    @Singleton
//    fun onboardingPageBuilder(): OnboardingPageBuilder {
//        return OnboardingFragmentPageBuilder()
//    }
//}

val onboardingModule = module {
    single { OnboardingFragmentPageBuilder() as OnboardingPageBuilder }
    factory {
        OnboardingPageManagerWithTrackerBlocking(get(), get(), get()) as OnboardingPageManager
    }
    viewModel{ DefaultBrowserPageViewModel(get(), get(), get()) }
    viewModel{ WelcomePageViewModel(get(), get(), get(),get()) }
    viewModel{ OnboardingViewModel(get(), get(), get()) }
}
