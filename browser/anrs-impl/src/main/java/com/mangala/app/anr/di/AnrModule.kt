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



package com.mangala.app.anr.di

import android.content.Context
import androidx.room.Room
import com.mangala.app.anrs.store.AnrsDatabase
import com.mangala.anrs.api.AnrRepository
import com.mangala.app.anr.RealAnrRepository

import org.koin.dsl.module
//import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object AnrModule {
//
//    @Provides
//    @Singleton
//    fun provideAnrDatabase(@ApplicationContext context: Context): AnrsDatabase {
//        return Room.databaseBuilder(context, AnrsDatabase::class.java, "anr_database.db")
//            .enableMultiInstanceInvalidation()
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Module
//    @InstallIn(SingletonComponent::class)
//    interface Binding {
//        @Binds
//        fun bindRealAnrRepository(realAnrRepository: RealAnrRepository): AnrRepository
//    }
//}


val anrModule = module {
    single {
        Room.databaseBuilder(get(), AnrsDatabase::class.java, "anr_database.db")
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }
    single { RealAnrRepository(get()) as AnrRepository }
}
