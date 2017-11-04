/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.implementations.datasources.local.news.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.proxies.local.CommonLocalConverters
import com.dbeginc.dbweatherdata.proxies.local.news.LocalArticle
import com.dbeginc.dbweatherdata.proxies.local.news.LocalFavoriteLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalSource

/**
 * Created by darel on 04.10.17.
 *
 * Local Weather Database
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Database(entities = arrayOf(LocalArticle::class, LocalSource::class, LocalLive::class, LocalFavoriteLive::class), version=1)
@TypeConverters(CommonLocalConverters::class)
abstract class LocalNewsDatabase : RoomDatabase() {

    abstract fun newsDao() : LocalNewsDao

    companion object {
        private val news_database_name = "newsdb"

        fun createDb(appContext: Context) = Room.databaseBuilder(appContext, LocalNewsDatabase::class.java, news_database_name)
                .build()
    }
}