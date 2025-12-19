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

package com.mangala.app.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.onboarding.store.AppStage
import com.mangala.app.onboarding.store.UserStageStore
import com.mangala.app.onboarding.ui.page.OnboardingPageFragment
import kotlinx.coroutines.launch

class OnboardingViewModel (
    private val userStageStore: UserStageStore,
    private val pageLayoutManager: OnboardingPageManager,
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    fun initializePages() {
        pageLayoutManager.buildPageBlueprints()
    }

    fun pageCount(): Int {
        return pageLayoutManager.pageCount()
    }

    fun getItem(position: Int): OnboardingPageFragment? {
        return pageLayoutManager.buildPage(position)
    }

    fun onOnboardingDone() {
        // Executing this on IO to avoid any delay changing threads between Main-IO.
        viewModelScope.launch(dispatchers.io()) {
            userStageStore.stageCompleted(AppStage.NEW)
        }
    }
}
