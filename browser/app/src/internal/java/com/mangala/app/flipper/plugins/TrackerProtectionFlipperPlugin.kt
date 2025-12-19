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

package com.mangala.app.flipper.plugins

import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.DispatcherProvider
import com.mangala.app.utils.ConflatedJob
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.random.Random

class TrackerProtectionFlipperPlugin @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    @AppCoroutineScope private val appCoroutineScope: CoroutineScope
) : FlipperPlugin {

    private var job = ConflatedJob()
    private var connection: FlipperConnection? = null
    private val rows = CopyOnWriteArrayList<FlipperObject>()
    private val periodicSenderJob = ConflatedJob()
    private val senderDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    override fun getId(): String {
        return "ddg-apptp-trackers"
    }

    override fun onConnect(connection: FlipperConnection?) {
        Timber.v("$id: connected")
        this.connection = connection

        periodicSenderJob += appCoroutineScope.launch(senderDispatcher) {
            while (isActive) {
                delay(Random.nextLong(PERIODIC_SEND_FREQUENCY_MS))
                sendRows()
            }
        }
    }

    override fun onDisconnect() {
        Timber.v("$id: disconnected")
        connection = null
        job.cancel()
        periodicSenderJob.cancel()
    }

    override fun runInBackground(): Boolean {
        Timber.v("$id: running")
        return false
    }

    private fun enqueueRow(row: FlipperObject) {
        rows.add(row)
    }

    private fun sendRows() {
        while (rows.isNotEmpty()) {
            connection?.send("newData", rows.removeAt(0))
        }
        rows.clear()
    }

    companion object {
        private const val PERIODIC_SEND_FREQUENCY_MS: Long = 1_000
    }
}
