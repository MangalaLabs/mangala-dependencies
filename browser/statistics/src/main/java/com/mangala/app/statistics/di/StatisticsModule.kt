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

package com.mangala.app.statistics.di

import com.mangala.app.statistics.api.OfflinePixelSender
import com.mangala.app.statistics.store.OfflinePixelCountDataStore
import com.mangala.app.statistics.store.OfflinePixelCountSharedPreferences
import com.mangala.app.statistics.store.StatisticsDataStore
import com.mangala.app.statistics.store.StatisticsSharedPreferences
import org.koin.android.ext.koin.androidContext
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.definition.Kind
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent
import kotlin.reflect.full.isSubclassOf

val statisticsModule = module {
    single { OfflinePixelSender(get(), get(), get()) }
    single<OfflinePixelCountDataStore> { OfflinePixelCountSharedPreferences(androidContext()) }
    single<StatisticsDataStore> { StatisticsSharedPreferences(androidContext()) }
}

@OptIn(KoinInternalApi::class)
inline fun <reified T : Any> getAll(): List<T> =
    KoinJavaComponent.getKoin().let { koin ->
        koin.instanceRegistry.instances.values.map { it.beanDefinition }
            .filter { it.kind == Kind.Singleton }
            .filter { it.primaryType.isSubclassOf(T::class) }
            .map { koin.get(clazz = it.primaryType, qualifier = null, parameters = null) }
    }
