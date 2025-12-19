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

package com.mangala.app.httpsupgrade

import android.net.Uri
import androidx.annotation.WorkerThread
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.mangala.app.global.isHttps
import com.mangala.app.global.toHttps
import com.mangala.app.httpsupgrade.store.HttpsFalsePositivesDao
import com.mangala.app.privacy.db.UserWhitelistDao
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.privacy.config.api.Https
import com.mangala.privacy.config.api.PrivacyFeatureName
import timber.log.Timber
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

interface HttpsUpgrader {

    @WorkerThread
    fun shouldUpgrade(uri: Uri): Boolean

    fun upgrade(uri: Uri): Uri {
        return uri.toHttps
    }

    @WorkerThread
    fun reloadData()
}


class HttpsUpgraderImpl (
    private val bloomFactory: HttpsBloomFilterFactory,
    private val bloomFalsePositiveDao: HttpsFalsePositivesDao,
    private val userAllowListDao: UserWhitelistDao,
    private val toggle: FeatureToggle,
    private val https: Https
) : HttpsUpgrader, LifecycleObserver {

    private var bloomFilter: BloomFilter? = null
    private val bloomReloadLock = ReentrantLock()

//    override fun onCreate(owner: LifecycleOwner) {
//        thread { reloadData() }
//    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onApplicationCreated() {
        thread { reloadData() }
    }

    @WorkerThread
    override fun shouldUpgrade(uri: Uri): Boolean {
        val host = uri.host ?: return false

        if (!toggle.isFeatureEnabled(PrivacyFeatureName.HttpsFeatureName)) {
            Timber.d("https is disabled in the remote config and so $host is not upgradable")
            return false
        }

        if (uri.isHttps) {
            return false
        }

        if (https.isAnException(uri.toString())) {
            Timber.d("$host is in the remote exception list and so not upgradable")
            return false
        }

        if (userAllowListDao.contains(host)) {
            Timber.d("$host is in user allowlist and so not upgradable")
            return false
        }

        if (bloomFalsePositiveDao.contains(host)) {
            Timber.d("$host is in https whitelist and so not upgradable")
            return false
        }

        val isUpgradable = isInUpgradeList(host)
        Timber.d("$host ${if (isUpgradable) "is" else "is not"} upgradable")
        return isUpgradable
    }

    @WorkerThread
    private fun isInUpgradeList(host: String): Boolean {
        waitForAnyReloadsToComplete()
        return bloomFilter?.contains(host) == true
    }

    @WorkerThread
    override fun reloadData() {
        Timber.v("Reload Https upgrader data")
        bloomReloadLock.lock()
        try {
            bloomFilter = bloomFactory.create()
        } finally {
            bloomReloadLock.unlock()
        }
    }

    private fun waitForAnyReloadsToComplete() {
        // wait for lock (by locking and unlocking) before continuing
        if (bloomReloadLock.isLocked) {
            bloomReloadLock.lock()
            bloomReloadLock.unlock()
        }
    }
}
