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



package com.mangala.deviceauth.impl.di

import com.mangala.deviceauth.api.DeviceAuthenticator
import com.mangala.deviceauth.impl.AuthLauncher
import com.mangala.deviceauth.impl.RealAuthLauncher
import com.mangala.deviceauth.impl.RealDeviceAuthenticator
import com.mangala.deviceauth.impl.RealSupportedDeviceAuthChecker
import com.mangala.deviceauth.impl.SupportedDeviceAuthChecker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//import dagger.Binds
//import dagger.Module
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent

//@Module
//@InstallIn(SingletonComponent::class)
//interface DeviceAuthModule {
//    @Binds
//    fun bindRealSupportedDeviceAuthChecker(realSupportedDeviceAuthChecker: RealSupportedDeviceAuthChecker): SupportedDeviceAuthChecker
//
//    @Binds
//    fun bindRealDeviceAuthenticator(realDeviceAuthenticator: RealDeviceAuthenticator): DeviceAuthenticator
//
//    @Binds
//    fun bindRealAuthLauncher(realAuthLauncher: RealAuthLauncher): AuthLauncher
//}


val deviceAuthModule = module {
    single { RealSupportedDeviceAuthChecker(androidContext()) as SupportedDeviceAuthChecker }
    single { RealDeviceAuthenticator(get(), get(), get()) as DeviceAuthenticator }
    single { RealAuthLauncher(androidContext(), get()) as AuthLauncher }
}
