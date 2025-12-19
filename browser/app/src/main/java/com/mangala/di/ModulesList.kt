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

package com.mangala.di

import com.alphawallet.app.di.viewModelModule
import com.mangala.app.accessibility.di.accessibilityModule
import com.mangala.app.anr.di.anrModule
import com.mangala.app.bookmarks.di.bookmarksModule
import com.mangala.app.browser.certificates.certificateTrustedStoreModule
import com.mangala.app.browser.di.browserModule
import com.mangala.app.browser.favicon.faviconModule
import com.mangala.app.browser.rating.di.ratingModule
import com.mangala.app.browser.useragent.defaultUserAgentModule
import com.mangala.app.di.appConfigurationDownloaderModule
import com.mangala.app.di.bindingModule
import com.mangala.app.di.coroutinesModule
import com.mangala.app.di.daoModule
import com.mangala.app.di.databaseModule
import com.mangala.app.di.devicePropertiesModule
import com.mangala.app.di.fileModule
import com.mangala.app.di.formatterModule
import com.mangala.app.di.httpsPersisterModule
import com.mangala.app.di.jobsModule
import com.mangala.app.di.jsonModule
import com.mangala.app.di.networkModule
import com.mangala.app.di.notificationModule
import com.mangala.app.di.pluginpoint.anr.offlinePixelModule
import com.mangala.app.di.pluginpoint.api.browserLifecycleObserverModule
import com.mangala.app.di.pluginpoint.api.lifecycleObserverPluginPointModule
import com.mangala.app.di.pluginpoint.featuretoggles.featureTogglesPluginPointModule
import com.mangala.app.di.pluginpoint.flipper.flipperPluginPluginPointModule
import com.mangala.app.di.pluginpoint.global.activityLifecycleCallbacksPluginPointModule
import com.mangala.app.di.pluginpoint.global.apiInterceptorPluginPluginPointModule
import com.mangala.app.di.pluginpoint.migration.migrationPluginPluginPointModule
import com.mangala.app.di.pluginpoint.notification.schedulableNotificationPluginPluginPointModule
import com.mangala.app.di.pluginpoint.pixel.pixelInterceptorPluginPluginPointModule
import com.mangala.app.di.pluginpoint.privacyfeature.privacyFeaturePluginPluginPointModule
import com.mangala.app.di.pluginpoint.refreshretention.refreshRetentionAtbPluginPluginPointModule
import com.mangala.app.di.pluginpoint.settings.internalFeaturePluginPluginPointModule
import com.mangala.app.di.pluginpoint.settings.userAgentInterceptorPluginPointModule
import com.mangala.app.di.privacyModule
import com.mangala.app.di.statisticsAppModule
import com.mangala.app.di.statisticsLibraryConfigModule
import com.mangala.app.di.storeModule
import com.mangala.app.di.storeReferralModule
import com.mangala.app.di.systemComponentsModule
import com.mangala.app.di.systemComponentsModuleBindings
import com.mangala.app.di.variantModule
import com.mangala.app.di.widgetModule
import com.mangala.app.di.workerModule
import com.mangala.app.email.di.emailModule
import com.mangala.app.global.di.commonModule
import com.mangala.app.global.exception.uncaughtExceptionModule
import com.mangala.app.global.shortcut.appShortcutCreatorModule
import com.mangala.app.job.appConfigurationSyncerModule
import com.mangala.app.onboarding.di.onboardingModule
import com.mangala.app.onboarding.di.welcomePageModule
import com.mangala.app.privacy.cleanup.trackersDbCleanerSchedulerModule
import com.mangala.app.statistics.di.statisticsModule
import com.mangala.app.surrogates.di.resourceSurrogateModule
import com.mangala.app.traces.di.tracesModule
import com.mangala.app.usage.di.appUsageModule
import com.mangala.autofill.di.autofillActivityModule
import com.mangala.autofill.di.autofillModule
import com.mangala.autofill.di.autofillSettingsModule
import com.mangala.bandwidth.di.bandwidthModule
import com.mangala.bandwidth.impl.bandwidthSchedulerModule
import com.mangala.deviceauth.impl.di.deviceAuthModule
import com.mangala.downloads.impl.di.downloadsModule
import com.mangala.feature.toggles.impl.di.featureTogglesModule
import com.mangala.macos_impl.di.macosBindingModule
import com.mangala.macos_impl.di.macosDatabaseModule
import com.mangala.macos_impl.di.macosNetworkModule
import com.mangala.privacy.config.impl.di.privacyBindingModule
import com.mangala.privacy.config.impl.di.privacyDatabaseModule
import com.mangala.privacy.config.impl.di.privacyNetworkModule
import com.mangala.remote.messaging.impl.di.dataSourceModule
import com.mangala.remote.messaging.impl.di.remoteDomainModule
import com.mangala.remote.messaging.impl.di.remoteNetworkModule
import com.mangala.securestorage.impl.di.secureStorageModule
import com.mangala.web3.web3Module
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.definition.Kind
import org.koin.java.KoinJavaComponent.getKoin
import com.mangala.web3.web3Module
import kotlin.reflect.full.isSubclassOf

public val browserModulesList = listOf(
    anrModule,
    accessibilityModule,
    bookmarksModule,
    certificateTrustedStoreModule,
    browserModule,
    faviconModule,
    ratingModule,
    offlinePixelModule,
    browserLifecycleObserverModule,
    lifecycleObserverPluginPointModule,
    featureTogglesPluginPointModule,
    flipperPluginPluginPointModule,
    activityLifecycleCallbacksPluginPointModule,
    apiInterceptorPluginPluginPointModule,
    migrationPluginPluginPointModule,
    schedulableNotificationPluginPluginPointModule,
    pixelInterceptorPluginPluginPointModule,
    privacyFeaturePluginPluginPointModule,
    refreshRetentionAtbPluginPluginPointModule,
    internalFeaturePluginPluginPointModule,
    userAgentInterceptorPluginPointModule,
    appConfigurationDownloaderModule,
    bindingModule,
    coroutinesModule,
    daoModule,
    databaseModule,
    devicePropertiesModule,
    fileModule,
    formatterModule,
    httpsPersisterModule,
    jobsModule,
    jsonModule,
    networkModule,
    notificationModule,
    privacyModule,
    statisticsLibraryConfigModule,
    statisticsModule,
    storeModule,
    storeReferralModule,
    systemComponentsModule,
    systemComponentsModuleBindings,
    variantModule,
    widgetModule,
    workerModule,
    uncaughtExceptionModule,
    appConfigurationSyncerModule,
    onboardingModule,
    welcomePageModule,
    trackersDbCleanerSchedulerModule,
    resourceSurrogateModule,
    appUsageModule,
    bandwidthSchedulerModule,
    defaultUserAgentModule,
    emailModule,
    appShortcutCreatorModule,
    tracesModule,
    statisticsModule,
    statisticsAppModule,
    secureStorageModule,
    dataSourceModule,
    remoteDomainModule,
    remoteNetworkModule,
    privacyNetworkModule,
    privacyDatabaseModule,
    privacyBindingModule,
    macosNetworkModule,
    macosDatabaseModule,
    macosBindingModule,
    featureTogglesModule,
    downloadsModule,
    deviceAuthModule,
    commonModule,
    bandwidthModule,
    autofillActivityModule,
    autofillModule,
    autofillSettingsModule,
    viewModelModule,
    web3Module
)
