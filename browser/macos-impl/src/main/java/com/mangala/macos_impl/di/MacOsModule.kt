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


package com.mangala.macos_impl.di

import android.content.Context
import com.mangala.app.notification.model.SchedulableNotification
import com.mangala.macos_api.MacOsWaitlist
import com.mangala.macos_impl.waitlist.MacOsWaitlistCodeNotification
import com.mangala.macos_impl.waitlist.MacOsWaitlistManager
import com.mangala.macos_impl.waitlist.RealMacOsWaitlist
import com.mangala.macos_impl.waitlist.RealMacOsWaitlistManager
import com.mangala.macos_impl.waitlist.api.MacOsWaitlistService
import com.mangala.macos_impl.waitlist.api.Url.API
import com.mangala.macos_impl.waitlist.ui.MacOsWaitlistWorkRequestBuilder
import com.mangala.macos_impl.waitlist.ui.RealMacOsWaitlistWorkRequestBuilder
import com.mangala.macos_store.MacOsWaitlistDataStore
import com.mangala.macos_store.MacOsWaitlistDataStoreSharedPreferences
import com.mangala.macos_store.MacOsWaitlistRepository
import com.mangala.macos_store.RealMacOsWaitlistRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
//import javax.inject.Named
//import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//object NetworkModule {
//
//    @Provides
//    @Singleton
//    fun apiRetrofit(@Named("api") okHttpClient: OkHttpClient): MacOsWaitlistService {
//        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(API)
//            .client(okHttpClient)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .build()
//
//        return retrofit.create(MacOsWaitlistService::class.java)
//    }
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//object DatabaseModule {
//
//    @Singleton
//    @Provides
//    fun providePrivacyConfigRepository(dataStore: MacOsWaitlistDataStore): MacOsWaitlistRepository {
//        return RealMacOsWaitlistRepository(dataStore)
//    }
//
//    @Singleton
//    @Provides
//    fun provideMacOsWaitlistDataStore(@ApplicationContext context: Context): MacOsWaitlistDataStore {
//        return MacOsWaitlistDataStoreSharedPreferences(context)
//    }
//}
//
//@Module
//@InstallIn(SingletonComponent::class)
//interface Binding {
//    @Binds
//    @Singleton
//    fun bindRealMacOsWaitlistManager(realMacOsWaitlistManager: RealMacOsWaitlistManager): MacOsWaitlistManager
//
//    @Binds
//    @Singleton
//    fun bindRealMacOsWaitlist(realMacOsWaitlist: RealMacOsWaitlist): MacOsWaitlist
//
//    @Binds
//    fun bindRealMacOsWaitlistWorkRequestBuilder(realMacOsWaitlistWorkRequestBuilder: RealMacOsWaitlistWorkRequestBuilder): MacOsWaitlistWorkRequestBuilder
//
//    @Binds
//    fun bindMacOsWaitlistCodeNotification(macOsWaitlistCodeNotification: MacOsWaitlistCodeNotification): SchedulableNotification
//}

val macosNetworkModule = module {
    single {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(API)
            .client(get(named("api")))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        retrofit.create(MacOsWaitlistService::class.java)
    }
}

val macosDatabaseModule = module {
    single { MacOsWaitlistDataStoreSharedPreferences(get()) as MacOsWaitlistDataStore }
    single { RealMacOsWaitlistRepository(get()) as MacOsWaitlistRepository }
}

val macosBindingModule = module {
    single { RealMacOsWaitlistManager(get(), get(), get()) as MacOsWaitlistManager }
    single { RealMacOsWaitlist(get()) as MacOsWaitlist }
    single { RealMacOsWaitlistWorkRequestBuilder() as MacOsWaitlistWorkRequestBuilder }
    single { MacOsWaitlistCodeNotification(androidContext(), get()) as SchedulableNotification }
}



