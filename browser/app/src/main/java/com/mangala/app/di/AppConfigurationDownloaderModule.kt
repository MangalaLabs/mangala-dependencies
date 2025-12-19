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

package com.mangala.app.di

import com.mangala.app.global.store.BinaryDataStore
import com.mangala.app.httpsupgrade.api.HttpsUpgradeDataDownloader
import com.mangala.app.job.AppConfigurationDownloader
import com.mangala.app.job.ConfigurationDownloader
import com.mangala.app.surrogates.api.ResourceSurrogateListDownloader
import com.mangala.app.surrogates.store.ResourceSurrogateDataStore
import com.mangala.app.survey.api.SurveyDownloader
import com.mangala.app.trackerdetection.api.TrackerDataDownloader
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


//@Module
//@InstallIn(SingletonComponent::class)
//open class AppConfigurationDownloaderModule {
//
//    @Provides
//    open fun appConfigurationDownloader(
//        trackerDataDownloader: TrackerDataDownloader,
//        httpsUpgradeDataDownloader: HttpsUpgradeDataDownloader,
//        resourceSurrogateDownloader: ResourceSurrogateListDownloader,
//        surveyDownloader: SurveyDownloader
//    ): ConfigurationDownloader {
//
//        return AppConfigurationDownloader(
//            trackerDataDownloader,
//            httpsUpgradeDataDownloader,
//            resourceSurrogateDownloader,
//            surveyDownloader
//        )
//    }
//}

val appConfigurationDownloaderModule = module {
    // Define the AppConfigurationDownloaderModule
    single<ConfigurationDownloader> {
        AppConfigurationDownloader(
            get(),
            get(),
            get(),
            get()
        )
    }

    single { BinaryDataStore(androidContext()) }

    // Declare the dependencies for AppConfigurationDownloaderModule
    single { TrackerDataDownloader(get(), get(), get(), get(), get()) }
    single { HttpsUpgradeDataDownloader(get(), get(), get(), get()) }
    single { ResourceSurrogateDataStore(androidContext()) }
    single { ResourceSurrogateListDownloader(get(), get(), get()) }
    single { SurveyDownloader(get(), get(), get()) }
}
