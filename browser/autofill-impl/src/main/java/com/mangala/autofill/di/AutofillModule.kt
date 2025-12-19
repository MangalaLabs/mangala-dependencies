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



package com.mangala.autofill.di

import android.content.Context
import android.content.Intent
import com.mangala.autofill.*
import com.mangala.autofill.internal.RealInternalTestUserChecker
import com.mangala.autofill.jsbridge.AutofillMessagePoster
import com.mangala.autofill.jsbridge.AutofillWebViewMessagePoster
import com.mangala.autofill.jsbridge.request.AutofillJsonRequestParser
import com.mangala.autofill.jsbridge.request.AutofillRequestParser
import com.mangala.autofill.jsbridge.response.AutofillJsonResponseWriter
import com.mangala.autofill.jsbridge.response.AutofillResponseWriter
import com.mangala.autofill.store.AutofillStore
import com.mangala.autofill.store.InternalTestUserStore
import com.mangala.autofill.store.RealInternalTestUserStore
import com.mangala.autofill.store.SecureStoreBackedAutofillStore
import com.mangala.autofill.ui.CredentialAutofillDialogAndroidFactory
import com.mangala.autofill.ui.ExistingCredentialMatchDetector
import com.mangala.autofill.ui.ExistingCredentialStoreInterrogatingMatchDetector
import com.mangala.autofill.AutofillJavascriptInterface
import com.mangala.autofill.AutofillStoredBackJavascriptInterface
import com.mangala.autofill.BrowserAutofill
import com.mangala.autofill.CredentialAutofillDialogFactory
import com.mangala.autofill.InlineBrowserAutofill
import com.mangala.autofill.InternalTestUserChecker
import com.mangala.autofill.ui.AutofillSettingsActivityLauncher
import com.mangala.autofill.ui.credential.management.AutofillManagementActivity
import com.mangala.securestorage.api.SecureStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

//import dagger.Binds
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.android.qualifiers.ApplicationContext
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton

//@Module
//@InstallIn(SingletonComponent::class)
//class AutofillModule {
//
//    @Provides
//    fun provideInternalTestUserStore(@ApplicationContext applicationContext: Context): InternalTestUserStore = RealInternalTestUserStore(applicationContext)
//
//    @Provides
//    @Singleton
//    fun autofillStore(
//        secureStorage: SecureStorage,
//        @ApplicationContext context: Context,
//        internalTestUserChecker: InternalTestUserChecker
//    ): AutofillStore {
//        return SecureStoreBackedAutofillStore(secureStorage, context, internalTestUserChecker)
//    }
//
//    @Module
//    @InstallIn(SingletonComponent::class)
//    interface Bindings {
//        @Binds
//        @Singleton
//        fun bindRealInternalTestUserChecker(realInternalTestUserChecker: RealInternalTestUserChecker): InternalTestUserChecker
//
//        @Binds
//        fun bindExistingCredentialStoreInterrogatingMatchDetector(existingCredentialStoreInterrogatingMatchDetector: ExistingCredentialStoreInterrogatingMatchDetector): ExistingCredentialMatchDetector
//
//        @Binds
//        fun bindAutofillJsonResponseWriter(autofillJsonResponseWriter: AutofillJsonResponseWriter): AutofillResponseWriter
//
//        @Binds
//        fun bindAutofillStoredBackJavascriptInterface(autofillStoredBackJavascriptInterface: AutofillStoredBackJavascriptInterface): AutofillJavascriptInterface
//
//        @Binds
//        fun bindWebViewUrlProvider(webViewUrlProvider: AutofillStoredBackJavascriptInterface.WebViewUrlProvider): AutofillStoredBackJavascriptInterface.UrlProvider
//
//        @Binds
//        fun bindInlineBrowserAutofill(inlineBrowserAutofill: InlineBrowserAutofill): BrowserAutofill
//
//        @Binds
//        fun bindAutofillJsonRequestParser(autofillJsonRequestParser: AutofillJsonRequestParser): AutofillRequestParser
//
//        @Binds
//        fun bindAutofillWebViewMessagePoster(autofillWebViewMessagePoster: AutofillWebViewMessagePoster): AutofillMessagePoster
//
//        @Binds
//        fun bindCredentialAutofillDialogAndroidFactory(credentialAutofillDialogAndroidFactory: CredentialAutofillDialogAndroidFactory): CredentialAutofillDialogFactory
//    }
//}


val autofillModule = module {

    single { RealInternalTestUserStore(get()) as InternalTestUserStore }

    single {
        SecureStoreBackedAutofillStore(get(), androidContext(), get()) as AutofillStore
    }

    single { RealInternalTestUserChecker(get(), get()) as InternalTestUserChecker }

    factory { ExistingCredentialStoreInterrogatingMatchDetector(get(), get()) as ExistingCredentialMatchDetector }

    factory { AutofillJsonResponseWriter(get()) as AutofillResponseWriter }

    factory { AutofillStoredBackJavascriptInterface(get(), get(), get(), get(), get(), get(named("AppCoroutineScope")), get(), get()) as AutofillJavascriptInterface }

    factory { AutofillStoredBackJavascriptInterface.WebViewUrlProvider(get()) as AutofillStoredBackJavascriptInterface.UrlProvider }

    factory { InlineBrowserAutofill(get(), get(), get(named("AppCoroutineScope"))) as BrowserAutofill }

    factory { AutofillJsonRequestParser(get()) as AutofillRequestParser }

    factory { AutofillWebViewMessagePoster() as AutofillMessagePoster }

    factory { CredentialAutofillDialogAndroidFactory() as CredentialAutofillDialogFactory }
}

val autofillSettingsModule = module {
    single<AutofillSettingsActivityLauncher> {
        object : AutofillSettingsActivityLauncher {
            override fun intent(context: Context): Intent {
                return AutofillManagementActivity.intent(context)
            }
        }
    }
}
