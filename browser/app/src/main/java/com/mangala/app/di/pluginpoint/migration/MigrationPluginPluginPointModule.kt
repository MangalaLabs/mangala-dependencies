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



package com.mangala.app.di.pluginpoint.migration

import com.mangala.app.di.pluginpoint.migration.MigrationPluginPluginPoint
import com.mangala.app.global.migrations.GpcMigrationPlugin
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.global.plugins.migrations.MigrationPlugin
import com.mangala.di.DaggerSet
import com.mangala.feature.toggles.api.FeatureTogglesPlugin
import com.mangala.privacy.config.impl.plugins.PrivacyFeatureTogglesPlugin
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//interface MigrationPluginPluginPointModule {
//    @Multibinds
//    fun migrationPlugin(): DaggerSet<MigrationPlugin> // MigrationPlugin_PluginPoint_Module
//
//    @Binds
//    @IntoSet
//    fun bindGpcMigrationPlugin(gpcMigrationPlugin: GpcMigrationPlugin): MigrationPlugin
//
//    @Binds
//    fun bindMigrationPluginPluginPoint(migrationPluginPoint: MigrationPluginPluginPoint): PluginPoint<MigrationPlugin> // MigrationPlugin_PluginPoint_Module
//}


val migrationPluginPluginPointModule = module {
    // Define the MigrationPluginPluginPointModule
    single<MigrationPlugin>{GpcMigrationPlugin(get(), get())}

    single<PluginPoint<MigrationPlugin>> {
        MigrationPluginPluginPoint()
    }
}
