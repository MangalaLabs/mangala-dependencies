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



package com.mangala.app.di

import com.mangala.app.statistics.ExperimentationVariantManager
import com.mangala.app.statistics.IndexRandomizer
import com.mangala.app.statistics.VariantManager
import com.mangala.app.statistics.WeightedRandomizer
import com.mangala.app.statistics.store.StatisticsDataStore
import com.mangala.appbuildconfig.api.AppBuildConfig
import org.koin.dsl.module


//@Module
//@InstallIn(SingletonComponent::class)
//object VariantModule {
//
//    @Provides
//    @Singleton
//    fun variantManager(
//        statisticsDataStore: StatisticsDataStore,
//        weightedRandomizer: WeightedRandomizer,
//        appBuildConfig: AppBuildConfig,
//    ): VariantManager =
//        ExperimentationVariantManager(statisticsDataStore, weightedRandomizer, appBuildConfig)
//
//    @Provides
//    fun weightedRandomizer() = WeightedRandomizer()
//}

val variantModule = module {
    single<IndexRandomizer> { WeightedRandomizer() }
    single<VariantManager> { ExperimentationVariantManager(get(), get(), get()) }
}
