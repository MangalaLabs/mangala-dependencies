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



package com.mangala.app.trackerdetection.db

import androidx.room.*
import com.mangala.app.trackerdetection.model.TdsEntity

@Dao
abstract class TdsEntityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(entities: List<TdsEntity>)

    @Query("select * from tds_entity")
    abstract fun getAll(): List<TdsEntity>

    @Query("select * from tds_entity where name=:name")
    abstract fun get(name: String): TdsEntity?

    @Query("delete from tds_entity")
    abstract fun deleteAll()

    @Query("select count(*) from tds_entity")
    abstract fun count(): Int

    @Transaction
    open fun updateAll(entities: List<TdsEntity>) {
        deleteAll()
        insertAll(entities)
    }
}
