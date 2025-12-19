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

package com.mangala.app.browser.certificates

import android.content.Context
import com.mangala.app.browser.certificates.rootstore.IsrgRootX1
import com.mangala.app.browser.certificates.rootstore.IsrgRootX2
import com.mangala.app.browser.certificates.rootstore.LetsEncryptE1
import com.mangala.app.browser.certificates.rootstore.LetsEncryptR3
import com.mangala.app.browser.certificates.rootstore.TrustedCertificateStore
import com.mangala.app.browser.certificates.rootstore.TrustedCertificateStoreImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//class CertificateTrustedStoreModule {
//    @Provides
//    @Singleton
//    fun trustedCertificateStore(
//        letsEncryptCertificateProvider: LetsEncryptCertificateProvider
//    ): TrustedCertificateStore = TrustedCertificateStoreImpl(letsEncryptCertificateProvider)
//
//    @Provides
//    @Singleton
//    fun letsEncryptCertificateProvider(
//        @ApplicationContext context: Context
//    ): LetsEncryptCertificateProvider = LetsEncryptCertificateProviderImpl(
//        setOf(
//            IsrgRootX1(context),
//            IsrgRootX2(context),
//            LetsEncryptR3(context),
//            LetsEncryptE1(context)
//        )
//    )
//}


val certificateTrustedStoreModule = module {
    single {
        LetsEncryptCertificateProviderImpl(setOf(IsrgRootX1(androidContext()), IsrgRootX2(androidContext()), LetsEncryptR3(androidContext()), LetsEncryptE1(androidContext())))
                as LetsEncryptCertificateProvider
    }
    single { TrustedCertificateStoreImpl(get()) as TrustedCertificateStore }
}
