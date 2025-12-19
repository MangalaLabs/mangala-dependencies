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



package com.mangala.downloads.impl.di

import android.content.Context
import androidx.room.Room
import com.mangala.downloads.api.DownloadCallback
import com.mangala.downloads.api.DownloadsRepository
import com.mangala.downloads.api.FileDownloadNotificationManager
import com.mangala.downloads.impl.DefaultDownloadsRepository
import com.mangala.downloads.impl.DefaultFileDownloadNotificationManager
import com.mangala.downloads.impl.FileDownloadCallback
import com.mangala.downloads.store.DownloadsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//class DownloadsModule {
//
//    @Provides
//    @Singleton
//    fun provideDownloadsDatabase(@ApplicationContext context: Context): DownloadsDatabase {
//        return Room.databaseBuilder(context, DownloadsDatabase::class.java, "downloads.db")
//            .enableMultiInstanceInvalidation()
//            .fallbackToDestructiveMigration()
//            .build()
//    }
//
//    @Module()
//    @InstallIn(SingletonComponent::class)
//    interface Binding {
//        @Binds
//        @Singleton
//        fun bindDefaultDownloadsRepository(defaultDownloadsRepository: DefaultDownloadsRepository): DownloadsRepository
//
//        @Binds
//        @Singleton
//        fun bindDefaultFileDownloadNotificationManager(defaultFileDownloadNotificationManager: DefaultFileDownloadNotificationManager): FileDownloadNotificationManager
//
//        @Binds
//        @Singleton
//        fun bindFileDownloadCallback(fileDownloadCallback: FileDownloadCallback): DownloadCallback
//    }
//}

val downloadsModule = module {
    single {
        Room.databaseBuilder(get(), DownloadsDatabase::class.java, "downloads.db")
            .enableMultiInstanceInvalidation()
            .fallbackToDestructiveMigration()
            .build()
    }

    single<DownloadsRepository> { DefaultDownloadsRepository(get())  }
    single<FileDownloadNotificationManager> { DefaultFileDownloadNotificationManager(get(), androidContext()) }
    single<DownloadCallback> { FileDownloadCallback(get(), get(), get(), get(), get(named("AppCoroutineScope")))  }
}
