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

package com.mangala.app.browser.rating.di

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import com.mangala.app.browser.rating.db.AppEnjoymentDao
import com.mangala.app.browser.rating.db.AppEnjoymentDatabaseRepository
import com.mangala.app.browser.rating.db.AppEnjoymentRepository
import com.mangala.app.browser.rating.di.RatingModule.Companion.INITIAL_PROMPT_DECIDER_NAME
import com.mangala.app.browser.rating.di.RatingModule.Companion.SECONDARY_PROMPT_DECIDER_NAME
import com.mangala.app.di.AppCoroutineScope
import com.mangala.app.global.db.AppDatabase
import com.mangala.app.global.rating.*
import com.mangala.app.global.rating.AppEnjoymentAppCreationObserver
import com.mangala.app.global.rating.AppEnjoymentLiveDataEmitter
import com.mangala.app.global.rating.AppEnjoymentPromptEmitter
import com.mangala.app.global.rating.AppEnjoymentUserEventDatabaseRecorder
import com.mangala.app.global.rating.AppEnjoymentUserEventRecorder
import com.mangala.app.global.rating.InitialPromptDecider
import com.mangala.app.global.rating.InitialPromptTypeDecider
import com.mangala.app.global.rating.PromptTypeDecider
import com.mangala.app.global.rating.SecondaryPromptDecider
import com.mangala.app.global.rating.ShowPromptDecider
import com.mangala.app.playstore.PlayStoreAndroidUtils
import com.mangala.app.playstore.PlayStoreUtils
import com.mangala.app.usage.app.AppDaysUsedRepository
import com.mangala.app.usage.search.SearchCountDao
import com.mangala.appbuildconfig.api.AppBuildConfig
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

//@Module
//@InstallIn(SingletonComponent::class)
//class RatingModule {
//
//    @Singleton
//    @Provides
//    @IntoSet
//    fun appEnjoymentManagerObserver(
//        appEnjoymentPromptEmitter: AppEnjoymentPromptEmitter,
//        promptTypeDecider: PromptTypeDecider,
//        @AppCoroutineScope appCoroutineScope: CoroutineScope
//    ): LifecycleObserver {
//        return AppEnjoymentAppCreationObserver(appEnjoymentPromptEmitter, promptTypeDecider, appCoroutineScope)
//    }
//
//    @Provides
//    @Singleton
//    fun appEnjoymentPromptEmitter(): AppEnjoymentPromptEmitter {
//        return AppEnjoymentLiveDataEmitter()
//    }
//
//    @Singleton
//    @Provides
//    fun appEnjoymentUserEventRecorder(
//        appEnjoymentRepository: AppEnjoymentRepository,
//        appEnjoymentPromptEmitter: AppEnjoymentPromptEmitter
//    ): AppEnjoymentUserEventRecorder {
//        return AppEnjoymentUserEventDatabaseRecorder(appEnjoymentRepository, appEnjoymentPromptEmitter)
//    }
//
//    @Provides
//    fun promptTypeDecider(
//        playStoreUtils: PlayStoreUtils,
//        searchCountDao: SearchCountDao,
//        @Named(INITIAL_PROMPT_DECIDER_NAME) initialPromptDecider: ShowPromptDecider,
//        @Named(SECONDARY_PROMPT_DECIDER_NAME) secondaryPromptDecider: ShowPromptDecider,
//        @ApplicationContext context: Context,
//        appBuildConfig: AppBuildConfig
//    ): PromptTypeDecider {
//        return InitialPromptTypeDecider(
//            playStoreUtils,
//            searchCountDao,
//            initialPromptDecider,
//            secondaryPromptDecider,
//            context,
//            appBuildConfig
//        )
//    }
//
//    @Provides
//    fun playStoreUtils(@ApplicationContext context: Context): PlayStoreUtils {
//        return PlayStoreAndroidUtils(context)
//    }
//
//    @Singleton
//    @Provides
//    fun appEnjoymentDao(database: AppDatabase): AppEnjoymentDao {
//        return database.appEnjoymentDao()
//    }
//
//    @Singleton
//    @Provides
//    fun appEnjoymentRepository(appEnjoymentDao: AppEnjoymentDao): AppEnjoymentRepository {
//        return AppEnjoymentDatabaseRepository(appEnjoymentDao)
//    }
//
//    @Named(INITIAL_PROMPT_DECIDER_NAME)
//    @Provides
//    fun initialPromptDecider(
//        appDaysUsedRepository: AppDaysUsedRepository,
//        appEnjoymentRepository: AppEnjoymentRepository
//    ): ShowPromptDecider {
//        return InitialPromptDecider(appDaysUsedRepository, appEnjoymentRepository)
//    }
//
//    @Named(SECONDARY_PROMPT_DECIDER_NAME)
//    @Provides
//    fun secondaryPromptDecider(
//        appDaysUsedRepository: AppDaysUsedRepository,
//        appEnjoymentRepository: AppEnjoymentRepository
//    ): ShowPromptDecider {
//        return SecondaryPromptDecider(appDaysUsedRepository, appEnjoymentRepository)
//    }
//
//    companion object {
//        private const val INITIAL_PROMPT_DECIDER_NAME = "initial-prompt-decider"
//        private const val SECONDARY_PROMPT_DECIDER_NAME = "secondary-prompt-decider"
//    }
//}


val ratingModule = module {

    single {
        AppEnjoymentLiveDataEmitter() as AppEnjoymentPromptEmitter
    }

    single {
        AppEnjoymentAppCreationObserver(get(), get(), get(named("AppCoroutineScope")))
    }

    single {
        AppEnjoymentUserEventDatabaseRecorder(get(), get()) as AppEnjoymentUserEventRecorder
    }

    single(named(INITIAL_PROMPT_DECIDER_NAME)) {
        InitialPromptDecider(get(), get()) as ShowPromptDecider
    }

    single(named(SECONDARY_PROMPT_DECIDER_NAME)) {
        SecondaryPromptDecider(get(), get()) as ShowPromptDecider
    }

    single {
        InitialPromptTypeDecider(get(), get(), get(named(INITIAL_PROMPT_DECIDER_NAME)), get(named(SECONDARY_PROMPT_DECIDER_NAME)), get(), get()) as PromptTypeDecider
    }

    single {
        PlayStoreAndroidUtils(get()) as PlayStoreUtils
    }

    single {
        get<AppDatabase>().appEnjoymentDao()
    }

    single {
        AppEnjoymentDatabaseRepository(get()) as AppEnjoymentRepository
    }


}

class RatingModule {
    companion object {
        const val INITIAL_PROMPT_DECIDER_NAME = "initial-prompt-decider"
        const val SECONDARY_PROMPT_DECIDER_NAME = "secondary-prompt-decider"
    }
}
