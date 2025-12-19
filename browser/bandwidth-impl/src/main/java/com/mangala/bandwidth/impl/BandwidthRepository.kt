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

import com.mangala.bandwidth.store.BandwidthBucketEntity
import com.mangala.bandwidth.store.BandwidthDatabase
import com.mangala.bandwidth.store.BandwidthEntity

interface BandwidthRepository {

    fun getCurrentBandwidthData(): BandwidthData
    fun persistBandwidthData(bandwidthData: BandwidthData)
    fun getStoredBandwidthData(): BandwidthData?
    fun persistBucket(bucket: BandwidthData)
    fun getBuckets(): List<BandwidthData>
    fun deleteAllBuckets()
}

class RealBandwidthRepository(
    val trafficStatsProvider: TrafficStatsProvider,
    val database: BandwidthDatabase
) : BandwidthRepository {

    override fun getCurrentBandwidthData(): BandwidthData {
        return BandwidthData(
            appBytes = trafficStatsProvider.getAppRxBytes() + trafficStatsProvider.getAppTxBytes(),
            totalBytes = trafficStatsProvider.getTotalRxBytes() + trafficStatsProvider.getTotalTxBytes()
        )
    }

    override fun persistBandwidthData(bandwidthData: BandwidthData) {
        database.bandwidthDao().insert(
            BandwidthEntity(
                timestamp = bandwidthData.timestamp,
                appBytes = bandwidthData.appBytes,
                totalBytes = bandwidthData.totalBytes
            )
        )
    }

    override fun getStoredBandwidthData(): BandwidthData? {
        val bandwidthEntity = database.bandwidthDao().getBandwidth() ?: return null

        return BandwidthData(
            timestamp = bandwidthEntity.timestamp,
            appBytes = bandwidthEntity.appBytes,
            totalBytes = bandwidthEntity.totalBytes
        )
    }

    override fun persistBucket(bucket: BandwidthData) {
        database.bandwidthDao().insertBucket(
            BandwidthBucketEntity(
                timestamp = bucket.timestamp,
                appBytes = bucket.appBytes,
                totalBytes = bucket.totalBytes
            )
        )
    }

    override fun getBuckets(): List<BandwidthData> {
        return database.bandwidthDao().getBuckets().map {
            BandwidthData(it.timestamp, it.appBytes, it.totalBytes)
        }
    }

    override fun deleteAllBuckets() {
        database.bandwidthDao().deleteAllBuckets()
    }
}
