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

package com.mangala.app.global.migrations

import com.mangala.app.global.plugins.migrations.MigrationPlugin
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.privacy.config.api.Gpc
import timber.log.Timber


class GpcMigrationPlugin (
    private val settingsDataStore: SettingsDataStore,
    private val gpc: Gpc
) : MigrationPlugin {

    override val version: Int = 1

    override fun run() {
        Timber.d("Migrating gpc settings")
        val gpcEnabled = settingsDataStore.globalPrivacyControlEnabled
        if (gpcEnabled) {
            gpc.enableGpc()
        } else {
            gpc.disableGpc()
        }
    }
}
