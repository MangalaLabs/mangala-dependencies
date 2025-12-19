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


package com.mangala.app.pixels

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import com.mangala.app.browser.WebViewVersionProvider
import com.mangala.app.fire.UnsentForgetAllPixelStore
import com.mangala.app.statistics.pixels.Pixel
import com.mangala.app.statistics.pixels.Pixel.PixelParameter.WEBVIEW_VERSION
import timber.log.Timber
import java.util.concurrent.TimeUnit

class EnqueuedPixelWorker (
    private val workManager: WorkManager,
    private val pixel: Pixel,
    private val unsentForgetAllPixelStore: UnsentForgetAllPixelStore,
    private val webViewVersionProvider: WebViewVersionProvider
) : LifecycleEventObserver {

    private var launchedByFireAction: Boolean = false

    override fun onStateChanged(
        source: LifecycleOwner,
        event: Lifecycle.Event
    ) {
        if (event == Lifecycle.Event.ON_CREATE) {
            scheduleWorker(workManager)
            launchedByFireAction = isLaunchByFireAction()
        } else if (event == Lifecycle.Event.ON_START) {
            if (launchedByFireAction) {
                // skip the next on_start if branch
                Timber.i("Suppressing app launch pixel")
                launchedByFireAction = false
                return
            }
            Timber.i("Sending app launch pixel")
            pixel.fire(
                pixel = AppPixelName.APP_LAUNCH,
                parameters = mapOf(WEBVIEW_VERSION to webViewVersionProvider.getMajorVersion())
            )
        }
    }

    private fun isLaunchByFireAction(): Boolean {
        val timeDifferenceMillis = System.currentTimeMillis() - unsentForgetAllPixelStore.lastClearTimestamp
        if (timeDifferenceMillis <= APP_RESTART_CAUSED_BY_FIRE_GRACE_PERIOD) {
            Timber.i("The app was re-launched as a result of the fire action being triggered (happened ${timeDifferenceMillis}ms ago)")
            return true
        }
        return false
    }

    fun submitUnsentFirePixels() {
        val count = unsentForgetAllPixelStore.pendingPixelCountClearData
        Timber.i("Found $count unsent clear data pixels")
        if (count > 0) {
            for (i in 1..count) {
                pixel.fire(AppPixelName.FORGET_ALL_EXECUTED)
            }
            unsentForgetAllPixelStore.resetCount()
        }
    }

    companion object {
        private const val APP_RESTART_CAUSED_BY_FIRE_GRACE_PERIOD: Long = 10_000L
        private const val WORKER_SEND_ENQUEUED_PIXELS = "com.mangala.pixels.enqueued.worker"

        private fun scheduleWorker(workManager: WorkManager) {
            Timber.v("Scheduling the EnqueuedPixelWorker")

            val request = PeriodicWorkRequestBuilder<RealEnqueuedPixelWorker>(2, TimeUnit.HOURS)
                .addTag(WORKER_SEND_ENQUEUED_PIXELS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                .build()

            workManager.enqueueUniquePeriodicWork(WORKER_SEND_ENQUEUED_PIXELS, ExistingPeriodicWorkPolicy.KEEP, request)
        }
    }
}

class RealEnqueuedPixelWorker (
    val context: Context,
    parameters: WorkerParameters,
    private val enqueuedPixelWorker: EnqueuedPixelWorker
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        Timber.v("Sending enqueued pixels")

        enqueuedPixelWorker.submitUnsentFirePixels()

        return Result.success()
    }
}
