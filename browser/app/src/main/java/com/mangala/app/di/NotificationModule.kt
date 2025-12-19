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

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.WorkManager
import com.mangala.app.email.db.EmailDataStore
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.notification.*
import com.mangala.app.notification.db.NotificationDao
import com.mangala.app.notification.model.AppTPWaitlistCodeNotification
import com.mangala.app.notification.model.ClearDataNotification
import com.mangala.app.notification.model.EmailWaitlistCodeNotification
import com.mangala.app.notification.model.PrivacyProtectionNotification
import com.mangala.app.notification.AndroidNotificationScheduler
import com.mangala.app.notification.AppNotificationSender
import com.mangala.app.notification.NotificationFactory
import com.mangala.app.notification.NotificationScheduler
import com.mangala.app.notification.model.SchedulableNotificationPlugin
import com.mangala.app.privacy.db.PrivacyProtectionCountDao
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.notification.NotificationSender
import com.mangala.app.notification.model.SchedulableNotification
import com.mangala.app.statistics.pixels.Pixel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//object NotificationModule {
//
//    @Provides
//    @Singleton
//    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
//        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//    }
//
//    @Provides
//    @Singleton
//    fun provideNotificationManagerCompat(@ApplicationContext context: Context): NotificationManagerCompat {
//        return NotificationManagerCompat.from(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideLocalBroadcastManager(@ApplicationContext context: Context): LocalBroadcastManager {
//        return LocalBroadcastManager.getInstance(context)
//    }
//
//    @Provides
//    fun provideClearDataNotification(
//        @ApplicationContext context: Context,
//        notificationDao: NotificationDao,
//        settingsDataStore: SettingsDataStore
//    ): ClearDataNotification {
//        return ClearDataNotification(context, notificationDao, settingsDataStore)
//    }
//
//    @Provides
//    fun providePrivacyProtectionNotification(
//        @ApplicationContext context: Context,
//        notificationDao: NotificationDao,
//        privacyProtectionCountDao: PrivacyProtectionCountDao
//    ): PrivacyProtectionNotification {
//        return PrivacyProtectionNotification(context, notificationDao, privacyProtectionCountDao)
//    }
//
//    @Provides
//    fun provideWaitlistCodeNotification(
//        @ApplicationContext context: Context,
//        notificationDao: NotificationDao,
//        emailDataStore: EmailDataStore
//    ): EmailWaitlistCodeNotification {
//        return EmailWaitlistCodeNotification(context, notificationDao, emailDataStore)
//    }
//
//    @Provides
//    @Singleton
//    fun providesNotificationScheduler(
//        workManager: WorkManager,
//        clearDataNotification: ClearDataNotification,
//        privacyProtectionNotification: PrivacyProtectionNotification
//    ): AndroidNotificationScheduler {
//        return NotificationScheduler(
//            workManager,
//            clearDataNotification,
//            privacyProtectionNotification
//        )
//    }
//
//    @Provides
//    @Singleton
//    fun providesNotificationFactory(
//        @ApplicationContext context: Context,
//        manager: NotificationManagerCompat
//    ): NotificationFactory {
//        return NotificationFactory(context, manager)
//    }
//
//    @Provides
//    @Singleton
//    fun providesNotificationSender(
//        @ApplicationContext context: Context,
//        pixel: Pixel,
//        manager: NotificationManagerCompat,
//        factory: NotificationFactory,
//        notificationDao: NotificationDao,
//        pluginPoint: PluginPoint<SchedulableNotificationPlugin>
//    ): NotificationSender {
//        return AppNotificationSender(context, pixel, manager, factory, notificationDao, pluginPoint)
//    }
//
//    @Provides
//    fun provideAppTpWaitlistCodeNotification(
//        @ApplicationContext context: Context,
//        notificationDao: NotificationDao,
//    ): AppTPWaitlistCodeNotification {
//        return AppTPWaitlistCodeNotification(context, notificationDao)
//    }
//}

val notificationModule = module {
    single {
        androidContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    single {
        NotificationManagerCompat.from(androidContext())
    }

    single {
        LocalBroadcastManager.getInstance(androidContext())
    }

    factory {
        ClearDataNotification(
            androidContext(),
            get<NotificationDao>(),
            get<SettingsDataStore>()
        )
    }

    factory {
        PrivacyProtectionNotification(
            androidContext(),
            get<NotificationDao>(),
            get<PrivacyProtectionCountDao>()
        )
    }

    factory {
        EmailWaitlistCodeNotification(
            androidContext(),
            get<NotificationDao>(),
            get<EmailDataStore>()
        )
    }

    single<AndroidNotificationScheduler> {
        NotificationScheduler(
            get<WorkManager>(),
            get<ClearDataNotification>(),
            get<PrivacyProtectionNotification>()
        )
    }

    single {
        NotificationFactory(
            androidContext(),
            get()
        )
    }

    single<NotificationSender> {
        AppNotificationSender(
            androidContext(),
            get<Pixel>(),
            get<NotificationManagerCompat>(),
            get<NotificationFactory>(),
            get<NotificationDao>(),
            get<PluginPoint<SchedulableNotificationPlugin>>()
        )
    }

    factory< SchedulableNotification> {
        AppTPWaitlistCodeNotification(
            androidContext(),
            get<NotificationDao>()
        )
    }
}
