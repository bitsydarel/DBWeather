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

package com.dbeginc.dbweatherdata.proxies.remote.weather

import android.support.annotation.RestrictTo
import com.squareup.moshi.Json

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Daily Data
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class RemoteDailyData(@Json(name= "time") val time: Long, @Json(name= "summary") val summary: String,
                           @Json(name= "icon") val icon: String, @Json(name= "temperatureHigh") val temperatureHigh: Double,
                           @Json(name= "temperatureHighTime") val temperatureHighTime: Long, @Json(name= "temperatureLow") val temperatureLow: Double,
                           @Json(name= "temperatureLowTime") val temperatureLowTime: Long, @Json(name= "apparentTemperatureHigh") val apparentTemperatureHigh: Double,
                           @Json(name= "apparentTemperatureHighTime") val apparentTemperatureHighTime: Long, @Json(name= "apparentTemperatureLow") val apparentTemperatureLow: Double,
                           @Json(name="apparentTemperatureLowTime") val apparentTemperatureLowTime: Long, @Json(name= "dewPoint") val dewPoint: Double,
                           @Json(name= "humidity") val humidity: Double, @Json(name= "pressure") val pressure: Double,
                           @Json(name= "windSpeed") val windSpeed: Double, @Json(name= "windGust") val windGust: Double,
                           @Json(name= "windGustTime") val windGustTime: Long, @Json(name= "windBearing") val windBearing: Long,
                           @Json(name= "cloudCover") val cloudCover: Double, @Json(name= "moonPhase") val moonPhase: Double,
                           @Json(name= "visibility") val visibility: Double, @Json(name= "uvIndex") val uvIndex: Long,
                           @Json(name= "uvIndexTime") val uvIndexTime: Long, @Json(name= "sunsetTime") val sunsetTime: Long,
                           @Json(name= "sunriseTime") val sunriseTime: Long, @Json(name= "precipIntensity") val precipIntensity: Double,
                           @Json(name= "precipIntensityMax") val precipIntensityMax: Double, @Json(name= "precipProbability") val precipProbability: Double,
                           @Json(name= "precipType") val precipType: String?
)