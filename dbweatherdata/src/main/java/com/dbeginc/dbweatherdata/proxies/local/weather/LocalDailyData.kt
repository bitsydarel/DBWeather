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

package com.dbeginc.dbweatherdata.proxies.local.weather

import android.support.annotation.RestrictTo

/**
 * Created by darel on 16.09.17.
 *
 * Local Daily Data
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class LocalDailyData(
        val time: Long,
        val summary: String,
        val icon: String,
        val temperatureHigh: Double,
        val temperatureHighTime: Long,
        val temperatureLow: Double,
        val temperatureLowTime: Long,
        val apparentTemperatureHigh: Double,
        val apparentTemperatureHighTime: Long,
        val apparentTemperatureLow: Double,
        val apparentTemperatureLowTime: Long,
        val dewPoint: Double,
        val humidity: Double,
        val pressure: Double,
        val windSpeed: Double,
        val windGust: Double,
        val windGustTime: Long,
        val windBearing: Long,
        val cloudCover: Double,
        val moonPhase: Double,
        val visibility: Double,
        val uvIndex: Long,
        val uvIndexTime: Long,
        val sunsetTime: Long,
        val sunriseTime: Long,
        val precipIntensity: Double,
        val precipIntensityMax: Double,
        val precipProbability: Double,
        val precipType: String?
)