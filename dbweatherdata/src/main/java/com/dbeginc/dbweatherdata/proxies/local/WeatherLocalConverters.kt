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

package com.dbeginc.dbweatherdata.proxies.local

import android.arch.persistence.room.TypeConverter
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalAlert
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalDailyData
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalHourlyData
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalMinutelyData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type


/**
 * Created by darel on 16.09.17.
 *
 * Local Converters
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class WeatherLocalConverters {

    @TypeConverter
    fun jsonStringToMinutelyData(json: String): List<LocalMinutelyData>? {
        return if (json.isNotEmpty()) {
            val listOfMinutelyData: Type = Types.newParameterizedType(List::class.java, LocalMinutelyData::class.java)

            Moshi.Builder()
                    .build()
                    .adapter<List<LocalMinutelyData>>(listOfMinutelyData)
                    .fromJson(json)!!

        } else null
    }

    @TypeConverter
    fun minutelyDataToJson(list: List<LocalMinutelyData>?): String {
        return if (list != null) {
            val listOfMinutelyData = Types.newParameterizedType(List::class.java, LocalMinutelyData::class.java)

            Moshi.Builder()
                    .build()
                    .adapter<List<LocalMinutelyData>>(listOfMinutelyData)
                    .toJson(list)
        } else ""
    }

    @TypeConverter
    fun jsonStringToHourlyData(json: String): List<LocalHourlyData> {
        val listOfHourlyData = Types.newParameterizedType(List::class.java, LocalHourlyData::class.java)

        return Moshi.Builder()
                .build()
                .adapter<List<LocalHourlyData>>(listOfHourlyData)
                .fromJson(json)!!
    }

    @TypeConverter
    fun hourlyDataToJson(list: List<LocalHourlyData>): String {
        val listOfHourlyData = Types.newParameterizedType(List::class.java, LocalHourlyData::class.java)

        return Moshi.Builder()
                .build()
                .adapter<List<LocalHourlyData>>(listOfHourlyData)
                .toJson(list)
    }

    @TypeConverter
    fun jsonStringToDailyData(json: String): List<LocalDailyData> {
        val listOfDailyData = Types.newParameterizedType(List::class.java, LocalDailyData::class.java)

        return Moshi.Builder()
                .build()
                .adapter<List<LocalDailyData>>(listOfDailyData)
                .fromJson(json)!!
    }

    @TypeConverter
    fun dailyDataToJson(list: List<LocalDailyData>): String {
        val listOfDailyData = Types.newParameterizedType(List::class.java, LocalDailyData::class.java)

        return Moshi.Builder()
                .build()
                .adapter<List<LocalDailyData>>(listOfDailyData)
                .toJson(list)
    }

    @TypeConverter
    fun jsonStringToAlerts(json: String): List<LocalAlert>? {
        return if (json.isNotEmpty()) {
            val listOfAlerts = Types.newParameterizedType(List::class.java, LocalAlert::class.java)

            Moshi.Builder()
                    .build()
                    .adapter<List<LocalAlert>>(listOfAlerts)
                    .fromJson(json)
        } else null
    }

    @TypeConverter
    fun alertsToJson(alerts: List<LocalAlert>?): String {
        return if (alerts != null) {
            val listOfAlerts = Types.newParameterizedType(List::class.java, LocalAlert::class.java)
            Moshi.Builder()
                    .build()
                    .adapter<List<LocalAlert>>(listOfAlerts)
                    .toJson(alerts)
        } else ""
    }
}