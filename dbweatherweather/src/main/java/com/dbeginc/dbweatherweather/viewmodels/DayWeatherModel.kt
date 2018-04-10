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

package com.dbeginc.dbweatherweather.viewmodels

import com.dbeginc.dbweathercommon.utils.toDate

/**
 * Created by darel on 19.09.17.
 *
 *
 */
data class DayWeatherModel(val dayName: String, val time: Long,
                           val summary: String, val icon: Int,
                           val temperatureUnit: String,
                           val temperatureHigh: Int, val temperatureHighTime: Long,
                           val temperatureLow: Int, val temperatureLowTime: Long,
                           val apparentTemperatureHigh: Int, val apparentTemperatureLow: Int,
                           val dewPoint: Double, val humidity: String,
                           val pressure: Double, val windSpeed: String,
                           val windGust: Double, val windGustTime: Long,
                           val windBearing: Long, val moonPhase: Double,
                           val uvIndex: Long, val uvIndexTime: Long,
                           val sunsetTime: String, val sunriseTime: String,
                           val precipIntensity: Double, val precipIntensityMax: Double,
                           val precipProbability: Double, val precipType: String?
) : Comparable<DayWeatherModel> {

    override fun compareTo(other: DayWeatherModel): Int = time.toDate().compareTo(other.time.toDate())

}