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


package com.mangala.bandwidth.impl

import com.mangala.app.statistics.pixels.Pixel
import com.mangala.bandwidth.impl.BandwidthPixelName.BANDWIDTH
import com.mangala.bandwidth.impl.BandwidthPixelParameter.APP_BYTES
import com.mangala.bandwidth.impl.BandwidthPixelParameter.PERIOD
import com.mangala.bandwidth.impl.BandwidthPixelParameter.TOTAL_BYTES


interface BandwidthCollector {
    fun collect()
}

class RealBandwidthCollector (
    val bandwidthRepository: BandwidthRepository,
    val pixel: Pixel
) : BandwidthCollector {
    override fun collect() {

        val lastBandwidthData = bandwidthRepository.getStoredBandwidthData()
        val currentBandwidthData = bandwidthRepository.getCurrentBandwidthData().also { bandwidthRepository.persistBandwidthData(it) }

        if (lastBandwidthData == null) return
        if (lastBandwidthData.totalBytes > currentBandwidthData.totalBytes) return

        val params = getPixelParams(currentBandwidthData, lastBandwidthData)

        pixel.fire(BANDWIDTH, params)
    }

    private fun getPixelParams(
        currentBandwidthData: BandwidthData,
        lastBandwidthData: BandwidthData
    ): Map<String, String> {
        val period = currentBandwidthData.timestamp - lastBandwidthData.timestamp
        val appBytes = currentBandwidthData.appBytes - lastBandwidthData.appBytes
        val totalBytes = currentBandwidthData.totalBytes - lastBandwidthData.totalBytes

        return mapOf(
            PERIOD to period.toString(),
            APP_BYTES to appBytes.toString(),
            TOTAL_BYTES to totalBytes.toString()
        )
    }
}
