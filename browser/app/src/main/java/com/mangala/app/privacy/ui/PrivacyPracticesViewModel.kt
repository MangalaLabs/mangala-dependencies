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

package com.mangala.app.privacy.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.mangala.app.global.model.domain
import com.mangala.app.privacy.model.PrivacyPractices
import com.mangala.app.privacy.model.PrivacyPractices.Summary.UNKNOWN
import com.mangala.app.tabs.model.TabRepository
import kotlinx.coroutines.flow.*


class PrivacyPracticesViewModel (
    private val tabRepository: TabRepository
) : ViewModel() {

    data class ViewState(
        val domain: String = "",
        val practices: PrivacyPractices.Summary = UNKNOWN,
        val goodTerms: List<String> = emptyList(),
        val badTerms: List<String> = emptyList()
    )

    fun privacyPractices(tabId: String): StateFlow<ViewState> = flow {
        tabRepository.retrieveSiteData(tabId).asFlow().collect { site ->
            emit(
                ViewState(
                    domain = site.domain ?: "",
                    practices = site.privacyPractices.summary,
                    goodTerms = site.privacyPractices.goodReasons,
                    badTerms = site.privacyPractices.badReasons
                )
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ViewState())
}
