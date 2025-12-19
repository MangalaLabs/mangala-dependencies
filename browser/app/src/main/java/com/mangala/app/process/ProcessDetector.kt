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

package com.mangala.app.process

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import com.mangala.app.process.ProcessDetector.MangalaProcess.*
import timber.log.Timber

class ProcessDetector {

    fun detectProcess(context: Context): MangalaProcess {
        val processName = extractProcessName(context)
        Timber.v("Detected process name to be [%s]", processName)
        return detectProcess(processName)
    }

    fun detectProcess(processName: String): MangalaProcess {
        if (!processName.contains(":")) return BrowserProcess(processName)
        if (processName.endsWith(":vpn")) return VpnProcess(processName)
        return UnknownProcess(processName)
    }

    private fun extractProcessName(context: Context): String {
        if (Build.VERSION.SDK_INT >= 28) {
            return Application.getProcessName()
        }

        val am = context.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager?
        return am?.runningAppProcesses?.firstOrNull { it.pid == Process.myPid() }?.processName.orEmpty()
    }

    sealed class MangalaProcess(open val processName: String) {

        override fun toString(): String {
            return "${this::class.java.simpleName} ($processName)"
        }

        data class BrowserProcess(override val processName: String) : MangalaProcess(processName)
        data class VpnProcess(override val processName: String) : MangalaProcess(processName)
        data class UnknownProcess(override val processName: String) : MangalaProcess(processName)
    }
}
