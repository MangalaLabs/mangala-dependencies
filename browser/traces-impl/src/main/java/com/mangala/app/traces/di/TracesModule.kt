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

package com.mangala.app.traces.di

import com.mangala.app.traces.RealStartupTraces
import com.mangala.app.traces.api.StartupTraces
//import dagger.Binds
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//
//@Module
//@InstallIn(SingletonComponent::class)
//interface TracesModule {
//    @Binds
//    fun bindRealStartupTraces(realStartupTraces: RealStartupTraces): StartupTraces
//}

import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext

val tracesModule = module {
    single<StartupTraces> { RealStartupTraces(androidContext()) }
}
