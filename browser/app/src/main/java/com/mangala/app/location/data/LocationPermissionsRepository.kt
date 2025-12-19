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

package com.mangala.app.location.data

import androidx.lifecycle.LiveData
import com.mangala.app.browser.favicon.FaviconManager
import com.mangala.app.global.DispatcherProvider
import kotlinx.coroutines.withContext

class LocationPermissionsRepository (
    private val locationPermissionsDao: LocationPermissionsDao,
    private var faviconManager: FaviconManager?,
    private val dispatchers: DispatcherProvider
) {

    fun setFaviconManager(faviconManager: FaviconManager) {
        this.faviconManager = faviconManager
    }

    fun getLocationPermissionsSync(): List<LocationPermissionEntity> = locationPermissionsDao.allPermissions()
    fun getLocationPermissionsAsync(): LiveData<List<LocationPermissionEntity>> = locationPermissionsDao.allPermissionsEntities()

    suspend fun savePermission(
        domain: String,
        permission: LocationPermissionType
    ): LocationPermissionEntity? {
        val locationPermissionEntity = LocationPermissionEntity(domain = domain, permission = permission)
        val id = withContext(dispatchers.io()) {
            locationPermissionsDao.insert(locationPermissionEntity)
        }
        return if (id >= 0) {
            locationPermissionEntity
        } else {
            null
        }
    }

    suspend fun getDomainPermission(domain: String): LocationPermissionEntity? {
        return withContext(dispatchers.io()) {
            locationPermissionsDao.getPermission(domain)
        }
    }

    suspend fun deletePermission(domain: String) {
        withContext(dispatchers.io()) {
            val entity = locationPermissionsDao.getPermission(domain)
            entity?.let {
                faviconManager?.deletePersistedFavicon(domain)
                locationPermissionsDao.delete(it)
            }
        }
    }

    suspend fun permissionEntitiesCountByDomain(domain: String): Int {
        return withContext(dispatchers.io()) {
            locationPermissionsDao.permissionEntitiesCountByDomain(domain)
        }
    }
}
