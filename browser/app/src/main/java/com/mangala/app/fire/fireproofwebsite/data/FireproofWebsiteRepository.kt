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

package com.mangala.app.fire.fireproofwebsite.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.fire.fireproofwebsite.data.FireproofWebsiteDao
import com.mangala.app.fire.fireproofwebsite.data.FireproofWebsiteEntity
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.global.UriString
import kotlinx.coroutines.withContext


class FireproofWebsiteRepository(
    private val fireproofWebsiteDao: FireproofWebsiteDao,
    private val dispatchers: DispatcherProvider,
    private var faviconManager: FaviconManager?
) {

    fun setFaviconManager(faviconManager: FaviconManager) {
        this.faviconManager = faviconManager
    }

    suspend fun fireproofWebsite(domain: String): FireproofWebsiteEntity? {
        if (!UriString.isValidDomain(domain)) return null

        val fireproofWebsiteEntity = FireproofWebsiteEntity(domain = domain)
        val id = withContext(dispatchers.io()) {
            fireproofWebsiteDao.insert(fireproofWebsiteEntity)
        }

        return if (id >= 0) {
            fireproofWebsiteEntity
        } else {
            null
        }
    }

    fun getFireproofWebsites(): LiveData<List<FireproofWebsiteEntity>> = fireproofWebsiteDao.fireproofWebsitesEntities()

    fun isDomainFireproofed(domain: String): Boolean {
        val uri = Uri.parse(domain)
        val host = uri.host ?: return false

        return fireproofWebsiteDao.getFireproofWebsiteSync(host) != null
    }

    suspend fun removeFireproofWebsite(fireproofWebsiteEntity: FireproofWebsiteEntity) {
        withContext(dispatchers.io()) {
            faviconManager?.deletePersistedFavicon(fireproofWebsiteEntity.domain)
            fireproofWebsiteDao.delete(fireproofWebsiteEntity)
        }
    }

    suspend fun fireproofWebsitesCountByDomain(domain: String): Int {
        return withContext(dispatchers.io()) {
            fireproofWebsiteDao.fireproofWebsitesCountByDomain(domain)
        }
    }

    suspend fun removeAllFireproofWebsites() {
        withContext(dispatchers.io()) {
            fireproofWebsiteDao.deleteAll()
        }
    }
}
