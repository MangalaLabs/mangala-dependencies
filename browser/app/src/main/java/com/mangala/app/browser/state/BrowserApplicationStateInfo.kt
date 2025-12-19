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



package com.mangala.app.browser.state

import android.app.Activity
import android.os.Bundle
import com.mangala.app.anr.AnrSupervisor
import com.mangala.app.browser.BrowserActivity
import com.mangala.app.fire.AutomaticDataClearer
import com.mangala.app.global.ActivityLifecycleCallbacks
import com.mangala.browser.api.BrowserLifecycleObserver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class BrowserApplicationStateInfo (
) : ActivityLifecycleCallbacks, KoinComponent {

    private val automaticDataClearer: AutomaticDataClearer by inject()
    private val anrSupervisor: AnrSupervisor by inject()

    val observers: List<BrowserLifecycleObserver> by lazy { listOf(automaticDataClearer, anrSupervisor) }

    private var created = 0
    private var started = 0
    private var resumed = 0

    private var isFreshLaunch: Boolean = false
    private var overrideIsFreshLaunch: Boolean = false

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?
    ) {
        if (created++ == 0 && !overrideIsFreshLaunch) isFreshLaunch = true
    }

    override fun onActivityStarted(activity: Activity) {
        if (started++ == 0) {
            Timber.d("1991 onActivityStarted size " + observers.size)
            observers.forEach { it.onOpen(isFreshLaunch) }
            isFreshLaunch = false
        }
    }

    override fun onActivityResumed(activity: Activity) {
        (++resumed)
        observers.forEach { it.onForeground() }
    }

    override fun onActivityPaused(activity: Activity) {
        if (resumed > 0) (--resumed)
        observers.forEach { it.onBackground() }
    }

    override fun onActivityStopped(activity: Activity) {
        if (started > 0) (--started)
        if (started == 0) observers.forEach { it.onClose() }
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (created > 0) (--created)
        if (created == 0 && (activity is BrowserActivity)) {
            if (activity.destroyedByBackPress || activity.isChangingConfigurations) {
                overrideIsFreshLaunch = true
            } else {
                observers.forEach { it.onExit() }
            }
        }
    }
}
