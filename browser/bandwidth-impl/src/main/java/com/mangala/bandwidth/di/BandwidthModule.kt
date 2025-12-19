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


package com.mangala.bandwidth.di

import android.content.Context
import androidx.room.Room
import com.mangala.bandwidth.impl.*
import com.mangala.bandwidth.impl.BandwidthCollector
import com.mangala.bandwidth.impl.BandwidthRepository
import com.mangala.bandwidth.impl.RealBandwidthCollector
import com.mangala.bandwidth.impl.RealBandwidthRepository
import com.mangala.bandwidth.impl.RealTrafficStatsProvider
import com.mangala.bandwidth.impl.TrafficStatsProvider
import com.mangala.bandwidth.store.BandwidthDatabase
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//object BandwidthModule {
//    @Provides
//    @Singleton
//    fun provideBandwidthDatabase(@ApplicationContext context: Context): BandwidthDatabase {
//        return Room.databaseBuilder(context, BandwidthDatabase::class.java, "bandwidth_database.db")
//            .enableMultiInstanceInvalidation()
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//
//    @Module()
//    @InstallIn(SingletonComponent::class)
//    interface Binding {
//        @Binds
//        fun bindRealBandwidthCollector(realBandwidthCollector: RealBandwidthCollector): BandwidthCollector
//
//        @Binds
//        fun bindRealBandwidthRepository(realBandwidthRepository: RealBandwidthRepository): BandwidthRepository
//
//        @Binds
//        fun bindRealTrafficStatsProvider(realTrafficStatsProvider: RealTrafficStatsProvider): TrafficStatsProvider
//    }
//}


val bandwidthModule = module {
    single {
        Room.databaseBuilder(get(), BandwidthDatabase::class.java, "bandwidth_database.db")
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }

    single<BandwidthCollector> { RealBandwidthCollector(get(),get()) }
    single<BandwidthRepository> { RealBandwidthRepository(get(), get()) }
    single<TrafficStatsProvider> { RealTrafficStatsProvider() }
}
