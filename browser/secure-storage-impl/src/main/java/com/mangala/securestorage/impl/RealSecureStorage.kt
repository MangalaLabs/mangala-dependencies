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



package com.mangala.securestorage.impl

import com.mangala.app.global.DispatcherProvider
import com.mangala.securestorage.api.SecureStorage
import com.mangala.securestorage.api.WebsiteLoginDetailsWithCredentials
import com.mangala.securestorage.api.WebsiteLoginDetails
import com.mangala.securestorage.impl.encryption.EncryptionHelper.EncryptedString
import com.mangala.securestorage.store.SecureStorageRepository
import com.mangala.securestorage.store.db.WebsiteLoginCredentialsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RealSecureStorage (
    private val secureStorageRepository: SecureStorageRepository,
    private val dispatchers: DispatcherProvider,
    private val l2DataTransformer: L2DataTransformer
) : SecureStorage {

    override fun canAccessSecureStorage(): Boolean = l2DataTransformer.canProcessData()

    override suspend fun addWebsiteLoginDetailsWithCredentials(websiteLoginDetailsWithCredentials: WebsiteLoginDetailsWithCredentials) {
        withContext(dispatchers.io()) {
            secureStorageRepository.addWebsiteLoginCredential(websiteLoginDetailsWithCredentials.toDataEntity())
        }
    }

    override suspend fun websiteLoginDetailsForDomain(domain: String): Flow<List<WebsiteLoginDetails>> =
        withContext(dispatchers.io()) {
            secureStorageRepository.websiteLoginCredentialsForDomain(domain).map { list ->
                list.map {
                    it.toDetails()
                }
            }
        }

    override suspend fun websiteLoginDetails(): Flow<List<WebsiteLoginDetails>> =
        withContext(dispatchers.io()) {
            secureStorageRepository.websiteLoginCredentials().map { list ->
                list.map {
                    it.toDetails()
                }
            }
        }

    override suspend fun getWebsiteLoginDetailsWithCredentials(id: Int): WebsiteLoginDetailsWithCredentials =
        withContext(dispatchers.io()) {
            secureStorageRepository.getWebsiteLoginCredentialsForId(id).toCredentials()
        }

    override suspend fun websiteLoginDetailsWithCredentialsForDomain(domain: String): Flow<List<WebsiteLoginDetailsWithCredentials>> =
        withContext(dispatchers.io()) {
            secureStorageRepository.websiteLoginCredentialsForDomain(domain).map { list ->
                list.map {
                    it.toCredentials()
                }
            }
        }

    override suspend fun websiteLoginDetailsWithCredentials(): Flow<List<WebsiteLoginDetailsWithCredentials>> =
        withContext(dispatchers.io()) {
            secureStorageRepository.websiteLoginCredentials().map { list ->
                list.map {
                    it.toCredentials()
                }
            }
        }

    override suspend fun updateWebsiteLoginDetailsWithCredentials(websiteLoginDetailsWithCredentials: WebsiteLoginDetailsWithCredentials) =
        withContext(dispatchers.io()) {
            secureStorageRepository.updateWebsiteLoginCredentials(websiteLoginDetailsWithCredentials.toDataEntity())
        }

    override suspend fun deleteWebsiteLoginDetailsWithCredentials(id: Int) =
        withContext(dispatchers.io()) {
            secureStorageRepository.deleteWebsiteLoginCredentials(id)
        }

    private fun WebsiteLoginDetailsWithCredentials.toDataEntity(): WebsiteLoginCredentialsEntity {
        val encryptedData = encryptData(password)
        return WebsiteLoginCredentialsEntity(
            id = details.id ?: 0,
            domain = details.domain,
            username = details.username,
            password = encryptedData?.data,
            iv = encryptedData?.iv
        )
    }

    private fun WebsiteLoginCredentialsEntity.toCredentials(): WebsiteLoginDetailsWithCredentials =
        WebsiteLoginDetailsWithCredentials(
            details = toDetails(),
            password = decryptData(password, iv)
        )

    private fun WebsiteLoginCredentialsEntity.toDetails(): WebsiteLoginDetails =
        WebsiteLoginDetails(
            domain = domain,
            username = username,
            id = id
        )

    // only encrypt when there's data
    private fun encryptData(data: String?): EncryptedString? = data?.let { l2DataTransformer.encrypt(it) }

    private fun decryptData(
        data: String?,
        iv: String?
    ): String? {
        // only decrypt when there's data and iv
        return data?.let { _data ->
            iv?.let { _iv ->
                l2DataTransformer.decrypt(_data, _iv)
            }
        }
    }
}
