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

package com.mangala.app.global

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.fire.FireActivity
import com.mangala.app.global.plugins.PluginPoint
import com.mangala.app.process.ProcessDetector
import com.mangala.app.process.ProcessDetector.MangalaProcess
import com.mangala.app.process.ProcessDetector.MangalaProcess.VpnProcess
import com.mangala.app.referral.AppInstallationReferrerStateListener
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.realm.Realm
import kotlinx.coroutines.*
import org.threeten.bp.zone.ZoneRulesProvider
import timber.log.Timber
import java.io.File
import androidx.work.Configuration
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.mangala.app.di.pluginpoint.api.LifecycleObserverPluginPoint
import com.mangala.app.di.pluginpoint.global.ActivityLifecycleCallbacksPluginPoint
import com.mangala.app.email.EmailManager
import com.mangala.app.fire.DataClearingWorker
import com.mangala.app.global.job.AppConfigurationWorker
import com.mangala.app.global.view.ClearDataAction
import com.mangala.app.job.ConfigurationDownloader
import com.mangala.app.notification.ClearDataNotificationWorker
import com.mangala.app.notification.NotificationSender
import com.mangala.app.notification.PrivacyNotificationWorker
import com.mangala.app.notification.model.ClearDataNotification
import com.mangala.app.notification.model.EmailWaitlistCodeNotification
import com.mangala.app.notification.model.PrivacyProtectionNotification
import com.mangala.app.notification.model.SchedulableNotification
import com.mangala.app.pixels.EnqueuedPixelWorker
import com.mangala.app.pixels.RealEnqueuedPixelWorker
import com.mangala.app.privacy.cleanup.TrackersDbCleanerWorker
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.app.statistics.api.OfflinePixelSender
import com.mangala.app.statistics.api.OfflinePixelWorker
import com.mangala.app.trackerdetection.db.WebTrackersBlockedDao
import com.mangala.app.trackerdetection.model.Adapters
import com.mangala.app.waitlist.email.EmailWaitlistWorkRequestBuilder
import com.mangala.app.waitlist.email.EmailWaitlistWorker
import com.mangala.bandwidth.impl.BandwidthWorker
import com.mangala.di.browserModulesList
import com.mangala.macos_impl.waitlist.MacOsWaitlistManager
import com.mangala.macos_impl.waitlist.ui.MacOsWaitlistWorkRequestBuilder
import com.mangala.macos_impl.waitlist.ui.MacOsWaitlistWorker
import com.mangala.privacy.config.impl.PrivacyConfigDownloader
import com.mangala.privacy.config.impl.workers.PrivacyConfigDownloadWorker
import com.mangala.remote.messaging.impl.RemoteMessagingConfigDownloadWorker
import com.mangala.remote.messaging.impl.RemoteMessagingConfigDownloader
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

abstract class MangalaBaseApplication : Application() {


    val alertingUncaughtExceptionHandler: AlertingUncaughtExceptionHandler by inject()

    val referralStateListener: AppInstallationReferrerStateListener by inject()

    val lifecycleObserverPluginPoint: LifecycleObserverPluginPoint by inject()

    val activityLifecycleCallbacks: ActivityLifecycleCallbacksPluginPoint by inject()


    @AppCoroutineScope
    val appCoroutineScope: CoroutineScope by inject(named("AppCoroutineScope"))

    private val processDetector = ProcessDetector()

    override fun onCreate() {
        super.onCreate()

    }

    fun initBrowserApplication(){
        Realm.init(this)
        configureLogging()

        val currentProcess = processDetector.detectProcess(this)
        Timber.i("Creating MangalaApplication. Process %s", currentProcess)

        if (appIsRestarting()) return

        setupActivityLifecycleCallbacks()
        configureUncaughtExceptionHandler(currentProcess)
        initializeDateLibrary()

        if (currentProcess is VpnProcess) {
            Timber.i("VPN process, no further logic executed in application onCreate()")
            return
        }

        // Deprecated, we need to move all these into AppLifecycleEventObserver
        ProcessLifecycleOwner.get().lifecycle.apply {
            lifecycleObserverPluginPoint.getPlugins().forEach {
                Timber.d("1991 Registering application lifecycle observer: ${it.javaClass.canonicalName}")
                addObserver(it)
            }
        }

        appCoroutineScope.launch {
            referralStateListener.initialiseReferralRetrieval()
        }

//        val configuration = Configuration.Builder()
//            .setWorkerFactory(KoinWorkerFactory())
//            .build()
//
//        WorkManager.initialize(this, configuration)

        //startKoin
        Adapters.initialize(get())
    }

    fun getDiModule() = browserModulesList

    private fun setupActivityLifecycleCallbacks() {
        activityLifecycleCallbacks.getPlugins().forEach {
            registerActivityLifecycleCallbacks(it)
            Timber.d("1991 registerActivityLifecycleCallbacks")
        }
    }

    private fun configureUncaughtExceptionHandler(currentProcess: MangalaProcess) {
        when (currentProcess) {
            is MangalaProcess.BrowserProcess -> configureUncaughtExceptionHandlerBrowser()
            else -> Timber.w(
                "Unknown process: Not configuring uncaught exception handler for %s",
                currentProcess
            )

        }
    }

    private fun configureUncaughtExceptionHandlerBrowser() {
        Thread.setDefaultUncaughtExceptionHandler(alertingUncaughtExceptionHandler)
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException) {
                Timber.w(
                    throwable,
                    "An exception happened inside RxJava code but no subscriber was still around to handle it"
                )
            } else {
                alertingUncaughtExceptionHandler.uncaughtException(
                    Thread.currentThread(),
                    throwable
                )
            }
        }
    }

    private fun appIsRestarting(): Boolean {
        if (FireActivity.appRestarting(this)) {
            Timber.i("App restarting")
            return true
        }
        return false
    }

    private fun configureLogging() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    // vtodo - Work around for https://crbug.com/558377
    // AndroidInjection.inject(this) creates a new instance of the MangalaApplication (because we are in a new process)
    // This has several disadvantages:
    //   1. our app is of massive size, because we are duplicating our Dagger graph
    //   2. we are hitting this bug in https://crbug.com/558377, because some of the injected dependencies may eventually
    //      depend in something webview-related
    //
    // We need to override getDir and getCacheDir so that the webview does not share the same data dir across processes
    // This is hacky hacky but should be OK for now as we don't use the webview in the VPN, it is just an issue with
    // injecting/creating dependencies
    //
    // A proper fix should be to create a VpnServiceComponent that just provide the dependencies needed by the VPN, which would
    // also help with memory
    override fun getDir(
        name: String?,
        mode: Int
    ): File {
        val dir = super.getDir(name, mode)
        if (name == "webview" && processDetector.detectProcess(this) is VpnProcess) {
            return File("${dir.absolutePath}/vpn").apply {
                Timber.d(":vpn process getDir = $absolutePath")
                if (!exists()) {
                    mkdirs()
                }
            }
        }
        return dir
    }

    override fun getCacheDir(): File {
        val dir = super.getCacheDir()
        if (processDetector.detectProcess(this) is VpnProcess) {
            return File("${dir.absolutePath}/vpn").apply {
                Timber.d(":vpn process getCacheDir = $absolutePath")
                if (!exists()) {
                    mkdirs()
                }
            }
        }
        return dir
    }

    private fun initializeDateLibrary() {
        AndroidThreeTen.init(this)
        // Query the ZoneRulesProvider so that it is loaded on a background coroutine
        GlobalScope.launch(Dispatchers.IO) {
            ZoneRulesProvider.getAvailableZoneIds()
        }
    }
}

class KoinWorkerFactory : WorkerFactory(), KoinComponent {
    private val offlinePixelSender: OfflinePixelSender by inject()

    private val downloader: RemoteMessagingConfigDownloader by inject()
    private val dispatcherProvider: DispatcherProvider by inject()
    private val privacyConfigDownloader: PrivacyConfigDownloader by inject()
    private val macOsWaitlistManager: MacOsWaitlistManager by inject()
    private val notificationSender: NotificationSender by inject()
    private val notification: SchedulableNotification by inject()
    private val workRequestBuilder: MacOsWaitlistWorkRequestBuilder by inject()
    private val settingsDataStore: SettingsDataStore by inject()
    private val clearDataAction: ClearDataAction by inject()
    private val appConfigurationDownloader: ConfigurationDownloader by inject()
    private val enqueuedPixelWorker: EnqueuedPixelWorker by inject()
    private val webTrackersBlockedDao: WebTrackersBlockedDao by inject()
    private val emailManager: EmailManager by inject()
    private val emailWaitlistCodeNotification: EmailWaitlistCodeNotification by inject()
    private val emailWaitlistWorkRequestBuilder: EmailWaitlistWorkRequestBuilder by inject()
    private val clearDataNotification: ClearDataNotification by inject()
    private val privacyProtectionNotification: PrivacyProtectionNotification by inject()

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): CoroutineWorker? {
        return when (workerClassName) {
            OfflinePixelWorker::class.simpleName -> {
                OfflinePixelWorker(appContext, workerParameters, offlinePixelSender)
            }
            RemoteMessagingConfigDownloadWorker::class.simpleName -> {
                RemoteMessagingConfigDownloadWorker(appContext, workerParameters, downloader, dispatcherProvider)
            }
            PrivacyConfigDownloadWorker::class.simpleName ->{
                PrivacyConfigDownloadWorker(appContext, workerParameters, privacyConfigDownloader, dispatcherProvider)
            }
            MacOsWaitlistWorker::class.simpleName -> {
                MacOsWaitlistWorker(appContext, workerParameters, macOsWaitlistManager, notificationSender, notification, workRequestBuilder )
            }
            DataClearingWorker::class.simpleName -> {
                DataClearingWorker(appContext, workerParameters, settingsDataStore, clearDataAction)
            }
            RealEnqueuedPixelWorker::class.simpleName -> {
                RealEnqueuedPixelWorker(appContext, workerParameters, enqueuedPixelWorker)
            }
            TrackersDbCleanerWorker::class.simpleName -> {
                TrackersDbCleanerWorker(appContext, workerParameters, webTrackersBlockedDao)
            }
            EmailWaitlistWorker::class.simpleName -> {
                EmailWaitlistWorker(appContext, workerParameters, emailManager, notificationSender, emailWaitlistCodeNotification, emailWaitlistWorkRequestBuilder)
            }
            BandwidthWorker::class.simpleName -> {
                BandwidthWorker(appContext, workerParameters)
            }
            ClearDataNotificationWorker::class.simpleName -> {
                ClearDataNotificationWorker(appContext, workerParameters, notificationSender, clearDataNotification)
            }
            PrivacyNotificationWorker::class.simpleName -> {
                PrivacyNotificationWorker(appContext, workerParameters, notificationSender, privacyProtectionNotification)
            }
            else -> {
                null
            }
        }
    }
}
