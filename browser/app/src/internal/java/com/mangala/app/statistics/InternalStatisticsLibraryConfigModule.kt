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

package com.mangala.app.statistics

// @Module
// @InstallIn(SingletonComponent::class)
// @ContributesTo(
//     scope = AppScope::class,
//     replaces = [StatisticsLibraryConfigModule::class] // TODO: Hilt - figure out how to impl this replacement in Hilt
// )
// class InternalStatisticsLibraryConfigModule {
//     @Provides
//     @Singleton
//     fun provideStatisticsLibraryConfig(): StatisticsLibraryConfig {
//         return object : StatisticsLibraryConfig {
//             override fun shouldFirePixelsAsDev(): Boolean {
//                 for internal builds we always want to pixel as dev so that we can separate
//                 internal users from real production users
// return true
// }
// }
// }
// }
