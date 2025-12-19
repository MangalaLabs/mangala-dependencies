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

package com.mangala.app.fire

import android.os.Handler
import android.os.SystemClock
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.core.os.postDelayed
import androidx.lifecycle.*
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mangala.app.global.ApplicationClearDataState
import com.mangala.app.global.ApplicationClearDataState.FINISHED
import com.mangala.app.global.ApplicationClearDataState.INITIALIZING
import com.mangala.app.global.view.ClearDataAction
import com.mangala.app.settings.clear.ClearWhatOption
import com.mangala.app.settings.clear.ClearWhenOption
import com.mangala.app.settings.db.SettingsDataStore
import com.mangala.browser.api.BrowserLifecycleObserver
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

interface DataClearer {
    val dataClearerState: LiveData<ApplicationClearDataState>
    var isFreshAppLaunch: Boolean
}


class AutomaticDataClearer(
    private val workManager: WorkManager,
    private val settingsDataStore: SettingsDataStore,
    private val clearDataAction: ClearDataAction,
    private val dataClearerTimeKeeper: BackgroundTimeKeeper,
    private val dataClearerForegroundAppRestartPixel: DataClearerForegroundAppRestartPixel
) : DataClearer, BrowserLifecycleObserver, CoroutineScope {

    private val clearJob: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + clearJob

    override val dataClearerState: MutableLiveData<ApplicationClearDataState> = MutableLiveData<ApplicationClearDataState>().also {
        it.postValue(INITIALIZING)
    }

    override var isFreshAppLaunch = true

    override fun onOpen(isFreshLaunch: Boolean) {
        isFreshAppLaunch = isFreshLaunch
        Timber.d("1991 onOpen $isFreshAppLaunch")
        launch {
            onAppForegroundedAsync()
        }
    }

    @UiThread
    @VisibleForTesting
    suspend fun onAppForegroundedAsync() {
        dataClearerState.value = INITIALIZING

        Timber.i("1991 onAppForegrounded; is from fresh app launch? $isFreshAppLaunch")

        workManager.cancelAllWorkByTag(DataClearingWorker.WORK_REQUEST_TAG)

        val appUsedSinceLastClear = settingsDataStore.appUsedSinceLastClear
        settingsDataStore.appUsedSinceLastClear = true

        val appIconChanged = settingsDataStore.appIconChanged
        settingsDataStore.appIconChanged = false

        val clearWhat = settingsDataStore.automaticallyClearWhatOption
        val clearWhen = settingsDataStore.automaticallyClearWhenOption
        Timber.i("1991 Currently configured to automatically clear $clearWhat / $clearWhen")

        if (clearWhat == ClearWhatOption.CLEAR_NONE) {
            Timber.i("1991 No data will be cleared as it's configured to clear nothing automatically")
            dataClearerState.value = FINISHED
        } else {
            if (shouldClearData(clearWhen, appUsedSinceLastClear, appIconChanged)) {
                Timber.i("1991 Decided data should be cleared")
                clearDataWhenAppInForeground(clearWhat)
            } else {
                Timber.i("1991 Decided not to clear data at this time")
                dataClearerState.value = FINISHED
            }
        }

        isFreshAppLaunch = false
        settingsDataStore.clearAppBackgroundTimestamp()
    }

    override fun onClose() {
        val timeNow = SystemClock.elapsedRealtime()
        Timber.i("1991 Recording when app backgrounded ($timeNow)")

        dataClearerState.value = INITIALIZING

        settingsDataStore.appBackgroundedTimestamp = timeNow

        val clearWhenOption = settingsDataStore.automaticallyClearWhenOption
        val clearWhatOption = settingsDataStore.automaticallyClearWhatOption

        if (clearWhatOption == ClearWhatOption.CLEAR_NONE || clearWhenOption == ClearWhenOption.APP_EXIT_ONLY) {
            Timber.d("1991 No background timer required for current configuration: $clearWhatOption / $clearWhenOption")
        } else {
            scheduleBackgroundTimerToTriggerClear(clearWhenOption.durationMilliseconds())
        }
    }

    override fun onExit() {
        // the app does not have any activity in CREATED state we kill the process
        if (settingsDataStore.automaticallyClearWhatOption != ClearWhatOption.CLEAR_NONE) {
            clearDataAction.killProcess()
        }
    }

    private fun scheduleBackgroundTimerToTriggerClear(durationMillis: Long) {
        workManager.also {
            val workRequest = OneTimeWorkRequestBuilder<DataClearingWorker>()
                .setInitialDelay(durationMillis, TimeUnit.MILLISECONDS)
                .addTag(DataClearingWorker.WORK_REQUEST_TAG)
                .build()
            it.enqueue(workRequest)
            Timber.i(
                "1991 Work request scheduled, ${durationMillis}ms from now, " +
                    "to clear data if the user hasn't returned to the app. job id: ${workRequest.id}"
            )
        }
    }

    @UiThread
    @Suppress("NON_EXHAUSTIVE_WHEN")
    private suspend fun clearDataWhenAppInForeground(clearWhat: ClearWhatOption) {
        Timber.i("1991 Clearing data when app is in the foreground: $clearWhat")

        when (clearWhat) {
            ClearWhatOption.CLEAR_TABS_ONLY -> {

                withContext(Dispatchers.IO) {
                    clearDataAction.clearTabsAsync(true)
                }

                withContext(Dispatchers.Main) {
                    Timber.i("1991 Notifying listener that clearing has finished")
                    dataClearerState.value = FINISHED
                }
            }

            ClearWhatOption.CLEAR_TABS_AND_DATA -> {
                val processNeedsRestarted = !isFreshAppLaunch
                Timber.i("1991 App is in foreground; restart needed? $processNeedsRestarted")

                clearDataAction.clearTabsAndAllDataAsync(appInForeground = true, shouldFireDataClearPixel = false)

                Timber.i("1991 All data now cleared, will restart process? $processNeedsRestarted")
                if (processNeedsRestarted) {
                    clearDataAction.setAppUsedSinceLastClearFlag(false)
                    dataClearerForegroundAppRestartPixel.incrementCount()
                    // need a moment to draw background color (reduces flickering UX)
                    Handler().postDelayed(100) {
                        Timber.i("1991 Will now restart process")
                        clearDataAction.killAndRestartProcess(notifyDataCleared = true)
                    }
                } else {
                    Timber.i("1991 Will not restart process")
                    dataClearerState.value = FINISHED
                }
            }
            else -> {}
        }
    }

    private fun shouldClearData(
        cleanWhenOption: ClearWhenOption,
        appUsedSinceLastClear: Boolean,
        appIconChanged: Boolean
    ): Boolean {
        Timber.d("1991 Determining if data should be cleared for option $cleanWhenOption")

        if (!appUsedSinceLastClear) {
            Timber.d("1991 App hasn't been used since last clear; no need to clear again")
            return false
        }

        Timber.d("1991 App has been used since last clear")

        if (isFreshAppLaunch) {
            Timber.d("1991 This is a fresh app launch, so will clear the data")
            return true
        }

        if (appIconChanged) {
            Timber.i("1991 No data will be cleared as the app icon was just changed")
            return false
        }

        if (cleanWhenOption == ClearWhenOption.APP_EXIT_ONLY) {
            Timber.d("1991 This is NOT a fresh app launch, and the configuration is for app exit only. Not clearing the data")
            return false
        }
        if (!settingsDataStore.hasBackgroundTimestampRecorded()) {
            Timber.w("1991 No background timestamp recorded; will not clear the data")
            return false
        }

        val enoughTimePassed = dataClearerTimeKeeper.hasEnoughTimeElapsed(
            backgroundedTimestamp = settingsDataStore.appBackgroundedTimestamp,
            clearWhenOption = cleanWhenOption
        )
        Timber.d("1991 Has enough time passed to trigger the data clear? $enoughTimePassed")

        return enoughTimePassed
    }
}
