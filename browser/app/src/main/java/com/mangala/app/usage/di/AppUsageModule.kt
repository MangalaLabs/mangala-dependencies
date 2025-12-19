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



package com.mangala.app.usage.di

import androidx.lifecycle.LifecycleObserver
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.usage.app.AppDaysUsedDao
import com.mangala.app.usage.app.AppDaysUsedDatabaseRepository
import com.mangala.app.usage.app.AppDaysUsedRecorder
import com.mangala.app.usage.app.AppDaysUsedRepository
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appUsageModule = module {
    single { AppDaysUsedDatabaseRepository(get()) as AppDaysUsedRepository }
    single { AppDaysUsedRecorder(get(), get(named("AppCoroutineScope")))  }
}
