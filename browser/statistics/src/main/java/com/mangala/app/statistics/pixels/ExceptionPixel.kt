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



package com.mangala.app.statistics.pixels

import com.mangala.app.global.exception.RootExceptionFinder
import com.mangala.app.global.exception.extractExceptionCause

/**
 * This is a temporary class: At some point we will introduce a new class to log handled exception
 * or illegal states to be stored and send as offline pixels
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ExceptionPixel(
    private val pixel: Pixel,
    private val rootExceptionFinder: RootExceptionFinder
) {

    fun sendExceptionPixel(
        pixelName: Pixel.PixelName,
        throwable: Throwable
    ) {
        val params = getParams(throwable)
        pixel.fire(pixelName, params)
    }

    fun sendExceptionPixel(
        pixelName: String,
        throwable: Throwable
    ) {
        val params = getParams(throwable)
        pixel.fire(pixelName, params)
    }

    private fun getParams(throwable: Throwable): Map<String, String> {
        val rootCause = rootExceptionFinder.findRootException(throwable)
        val exceptionCause = rootCause.extractExceptionCause()
        return mapOf(Pixel.PixelParameter.EXCEPTION_MESSAGE to exceptionCause)
    }
}
