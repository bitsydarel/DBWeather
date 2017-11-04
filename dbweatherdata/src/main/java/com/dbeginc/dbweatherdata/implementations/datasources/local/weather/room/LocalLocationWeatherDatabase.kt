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

package com.dbeginc.dbweatherdata.implementations.datasources.local.weather.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.proxies.local.CommonLocalConverters
import com.dbeginc.dbweatherdata.proxies.local.WeatherLocalConverters
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather

/**
 * Created by darel on 16.09.17.
 * Local Weather Database For User Locations
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Database(entities = arrayOf(LocalWeather::class), version = 1)
@TypeConverters(WeatherLocalConverters::class, CommonLocalConverters::class)
abstract class LocalLocationWeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): LocalWeatherDao

    companion object {
        private val LOCATION_WEATHER_Db = "location_weather"

        fun createDb(appContext: Context) = Room.databaseBuilder(appContext, LocalLocationWeatherDatabase::class.java, LOCATION_WEATHER_Db)
                .build()
    }
}