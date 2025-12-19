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

import java.io.Serializable

data class CrashReport(
    val application: String,
    private val throwable: Throwable
): Serializable {
    val manufacturer: String
    val model: String
    val brand: String
    val device: String
    val androidVersion: String
    val stackTrace: String

    init {
        manufacturer = android.os.Build.MANUFACTURER
        model = android.os.Build.MODEL
        brand = android.os.Build.BRAND
        device = android.os.Build.DEVICE
        androidVersion = android.os.Build.VERSION.RELEASE
        stackTrace = throwable.stackTraceToString()
    }

    val headerText = """
        Manufacturer: $manufacturer
        Model: $model
        Brand: $brand
        Device: $device
        Android Version: $androidVersion
    """.trimIndent()

    fun rethrowThrowable() {
        throw WrappedThrowable(throwable)
    }
}

class WrappedThrowable(val throwable: Throwable): Throwable(throwable) // Wrap throwable so that we can distinguish that we've shown it in the UI and we want it to crash it for real so we can see it in logcat
