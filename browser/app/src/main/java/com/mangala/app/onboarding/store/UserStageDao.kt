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

package com.mangala.app.onboarding.store

import androidx.room.*
import com.mangala.app.onboarding.store.AppStage
import com.mangala.app.onboarding.store.USER_STAGE_TABLE_NAME
import com.mangala.app.onboarding.store.UserStage

@Dao
interface UserStageDao {

    @Query("select * from $USER_STAGE_TABLE_NAME limit 1")
    suspend fun currentUserAppStage(): UserStage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userStage: UserStage)

    @Transaction
    fun updateUserStage(appStage: AppStage) {
        insert(UserStage(appStage = appStage))
    }
}
