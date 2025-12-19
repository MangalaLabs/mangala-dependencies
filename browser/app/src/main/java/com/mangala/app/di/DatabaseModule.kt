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

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mangala.app.browser.DefaultWebViewDatabaseProvider
import com.mangala.app.browser.WebViewDatabaseProvider
import com.mangala.app.global.db.AppDatabase
import com.mangala.app.global.db.MigrationsProvider
import com.mangala.app.settings.db.SettingsDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//@InstallIn(SingletonComponent::class)
//@Module
//object DatabaseModule {
//
//    @Provides
//    @Singleton
//    fun provideWebViewDatabaseProvider(@ApplicationContext context: Context): WebViewDatabaseProvider {
//        return DefaultWebViewDatabaseProvider(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideAppDatabase(
//        @ApplicationContext context: Context,
//        migrationsProvider: MigrationsProvider
//    ): AppDatabase {
//        return Room.databaseBuilder(context, AppDatabase::class.java, "app.db")
//            .addMigrations(*migrationsProvider.ALL_MIGRATIONS.toTypedArray())
//            .addCallback(migrationsProvider.BOOKMARKS_DB_ON_CREATE)
//            .addCallback(migrationsProvider.CHANGE_JOURNAL_ON_OPEN)
//            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
//            .build()
//    }
//
//    @Provides
//    fun provideDatabaseMigrations(
//        @ApplicationContext context: Context,
//        settingsDataStore: SettingsDataStore
//    ): MigrationsProvider {
//        return MigrationsProvider(context, settingsDataStore)
//    }
//}

val databaseModule = module {
    single<WebViewDatabaseProvider> { DefaultWebViewDatabaseProvider(androidContext()) }
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app.db"
        )
            .addMigrations(*get<MigrationsProvider>().ALL_MIGRATIONS.toTypedArray())
            .addCallback(get<MigrationsProvider>().BOOKMARKS_DB_ON_CREATE)
            .addCallback(get<MigrationsProvider>().CHANGE_JOURNAL_ON_OPEN)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .build()
    }
    single { MigrationsProvider(androidContext(), get()) }
}