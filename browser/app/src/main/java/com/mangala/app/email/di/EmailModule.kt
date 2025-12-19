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

package com.mangala.app.email.di

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.work.WorkManager
import com.mangala.app.autofill.JavascriptInjector
import com.mangala.app.browser.MangalaUrlDetector
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.email.AppEmailManager
import com.mangala.app.email.EmailInjector
import com.mangala.app.email.EmailInjectorJs
import com.mangala.app.email.EmailManager
import com.mangala.app.email.api.EmailService
import com.mangala.app.email.db.EmailDataStore
import com.mangala.app.email.db.EmailEncryptedSharedPreferences
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.notification.NotificationSender
import com.mangala.app.notification.model.EmailWaitlistCodeNotification
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.waitlist.email.AppEmailWaitlistCodeFetcher
import com.mangala.app.waitlist.email.EmailWaitlistCodeFetcher
import com.mangala.feature.toggles.api.FeatureToggle
import com.mangala.privacy.config.api.Autofill
import kotlinx.coroutines.CoroutineScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//class EmailModule {
//
//    @Singleton
//    @Provides
//    fun providesEmailManager(
//        emailService: EmailService,
//        emailDataStore: EmailDataStore,
//        dispatcherProvider: DispatcherProvider,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope
//    ): EmailManager {
//        return AppEmailManager(emailService, emailDataStore, dispatcherProvider, appCoroutineScope)
//    }
//
//    @Provides
//    fun providesEmailInjector(
//        emailManager: EmailManager,
//        mangalaUrlDetector: MangalaUrlDetector,
//        dispatcherProvider: DispatcherProvider,
//        featureToggle: FeatureToggle,
//        javascriptInjector: JavascriptInjector,
//        autofill: Autofill
//    ): EmailInjector {
//        return EmailInjectorJs(emailManager, mangalaUrlDetector, dispatcherProvider, featureToggle, javascriptInjector, autofill)
//    }
//
//    @Provides
//    fun providesEmailDataStore(
//        @ApplicationContext context: Context,
//        pixel: Pixel,
//    ): EmailDataStore {
//        return EmailEncryptedSharedPreferences(context, pixel)
//    }
//
//    @Singleton
//    @Provides
//    fun providesWaitlistCodeFetcher(
//        workManager: WorkManager,
//        emailManager: EmailManager,
//        notification: EmailWaitlistCodeNotification,
//        notificationSender: NotificationSender,
//        dispatcherProvider: DispatcherProvider,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope
//    ): EmailWaitlistCodeFetcher {
//        return AppEmailWaitlistCodeFetcher(workManager, emailManager, notification, notificationSender, dispatcherProvider, appCoroutineScope)
//    }
//
//    @Provides
//    @Singleton
//    @IntoSet
//    fun providesWaitlistCodeFetcherObserver(emailWaitlistCodeFetcher: EmailWaitlistCodeFetcher): LifecycleObserver = emailWaitlistCodeFetcher
//}


val emailModule = module {
    single {
        AppEmailManager(
            get(),
            get(),
            get(),
            get(named("AppCoroutineScope"))
        ) as EmailManager
    }

    single {MangalaUrlDetector()}

    factory {
        EmailInjectorJs(
            get() as EmailManager,
            get() as MangalaUrlDetector,
            get() as DispatcherProvider,
            get() as FeatureToggle,
            get() as JavascriptInjector,
            get() as Autofill
        ) as EmailInjector
    }

    factory {
        EmailEncryptedSharedPreferences(
            androidContext(),
            get() as Pixel
        ) as EmailDataStore
    }

    single {
        AppEmailWaitlistCodeFetcher(
            get() as WorkManager,
            get() as EmailManager,
            get() as EmailWaitlistCodeNotification,
            get() as NotificationSender,
            get() as DispatcherProvider,
            get(named("AppCoroutineScope"))
        )
    }
}
