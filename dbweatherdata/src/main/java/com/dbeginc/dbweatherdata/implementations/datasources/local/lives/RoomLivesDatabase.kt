/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.implementations.datasources.local.lives

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalFavoriteLive
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvLive
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvPlaylist
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalYoutubeLive

@RestrictTo(RestrictTo.Scope.LIBRARY)
@Database(entities = [LocalYoutubeLive::class, LocalFavoriteLive::class, LocalIpTvPlaylist::class, LocalIpTvLive::class], version = 1)
abstract class RoomLivesDatabase : RoomDatabase() {

    abstract fun livesDao(): RoomLivesDao

    companion object {
        fun createDb(appContext: Context) = Room
                .databaseBuilder(appContext, RoomLivesDatabase::class.java, "livesdb")
                .build()
    }
}