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
 * Local Hourly Weather Data
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class LocalHourlyData(
        val time: Long,
        val summary: String,
        val icon: String,
        val temperature: Double,
        val apparentTemperature: Double,
        val dewPoint: Double,
        val humidity: Double,
        val pressure: Double,
        val windSpeed: Double,
        val windGust: Double?,
        val windBearing: Long?,
        val cloudCover: Double,
        val precipIntensity: Double,
        val precipProbability: Double,
        val precipType: String?,
        val uvIndex: Long,
        val ozone: Double
)