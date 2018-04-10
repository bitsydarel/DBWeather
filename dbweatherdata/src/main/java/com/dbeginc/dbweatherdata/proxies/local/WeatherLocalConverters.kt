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

package com.dbeginc.dbweatherdata.proxies.local

import android.arch.persistence.room.TypeConverter
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalAlert
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalDailyData
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalHourlyData
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalMinutelyData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by darel on 16.09.17.
 *
 * Local Converters
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class WeatherLocalConverters {

    @TypeConverter
    fun jsonStringToMinutelyData(json: String): List<LocalMinutelyData>? {
        return if (json.isNotEmpty()) Gson().fromJson(json, object : TypeToken<List<LocalMinutelyData>>() {}.type)
        else null
    }

    @TypeConverter
    fun minutelyDataToJson(list: List<LocalMinutelyData>?): String = if (list != null) Gson().toJson(list) else ""

    @TypeConverter
    fun jsonStringToHourlyData(json: String): List<LocalHourlyData> =
            Gson().fromJson(json, object : TypeToken<List<LocalHourlyData>>() {}.type)

    @TypeConverter
    fun hourlyDataToJson(list: List<LocalHourlyData>): String = Gson().toJson(list)

    @TypeConverter
    fun jsonStringToDailyData(json: String): List<LocalDailyData> =
            Gson().fromJson(json, object : TypeToken<List<LocalDailyData>>() {}.type)

    @TypeConverter
    fun dailyDataToJson(dailyData: List<LocalDailyData>): String = Gson().toJson(dailyData)

    @TypeConverter
    fun jsonStringToAlerts(json: String): List<LocalAlert>? {
        return if (json.isNotEmpty()) Gson().fromJson(json, object : TypeToken<List<LocalAlert>>() {}.type)
        else null
    }

    @TypeConverter
    fun alertsToJson(alerts: List<LocalAlert>?): String = if (alerts != null) Gson().toJson(alerts) else ""
}