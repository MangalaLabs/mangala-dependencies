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

package com.mangala.app.tabs.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mangala.app.browser.session.WebViewSessionStorage
import com.mangala.app.global.SingleLiveEvent
import com.mangala.app.tabs.model.TabEntity
import com.mangala.app.tabs.model.TabRepository


class TabSwitcherViewModel(
    private val tabRepository: TabRepository,
    private val webViewSessionStorage: WebViewSessionStorage
) : ViewModel() {

    var tabs: LiveData<List<TabEntity>> = tabRepository.liveTabs
    var deletableTabs: LiveData<List<TabEntity>> = tabRepository.flowDeletableTabs.asLiveData(
        context = viewModelScope.coroutineContext
    )
    val command: SingleLiveEvent<Command> = SingleLiveEvent()

    sealed class Command {
        object Close : Command()
    }

    suspend fun onNewTabRequested() {
        tabRepository.add()
        command.value = Command.Close
    }

    suspend fun onTabSelected(tab: TabEntity) {
        tabRepository.select(tab.tabId)
        command.value = Command.Close
    }

    suspend fun onTabDeleted(tab: TabEntity) {
        tabRepository.delete(tab)
        webViewSessionStorage.deleteSession(tab.tabId)
    }

    suspend fun onMarkTabAsDeletable(tab: TabEntity) {
        tabRepository.markDeletable(tab)
    }

    suspend fun undoDeletableTab(tab: TabEntity) {
        tabRepository.undoDeletable(tab)
    }

    suspend fun purgeDeletableTabs() {
        tabRepository.purgeDeletableTabs()
    }
}
