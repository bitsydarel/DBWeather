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
import com.google.gson.annotations.SerializedName

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Daily Data
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class RemoteDailyData(@SerializedName("time") val time: Long, @SerializedName("summary") val summary: String,
                           @SerializedName("icon") val icon: String, @SerializedName("temperatureHigh") val temperatureHigh: Double,
                           @SerializedName("temperatureHighTime") val temperatureHighTime: Long, @SerializedName("temperatureLow") val temperatureLow: Double,
                           @SerializedName("temperatureLowTime") val temperatureLowTime: Long, @SerializedName("apparentTemperatureHigh") val apparentTemperatureHigh: Double,
                           @SerializedName("apparentTemperatureHighTime") val apparentTemperatureHighTime: Long, @SerializedName("apparentTemperatureLow") val apparentTemperatureLow: Double,
                           @SerializedName("apparentTemperatureLowTime") val apparentTemperatureLowTime: Long, @SerializedName("dewPoint") val dewPoint: Double,
                           @SerializedName("humidity") val humidity: Double, @SerializedName("pressure") val pressure: Double,
                           @SerializedName("windSpeed") val windSpeed: Double, @SerializedName("windGust") val windGust: Double,
                           @SerializedName("windGustTime") val windGustTime: Long, @SerializedName("windBearing") val windBearing: Long,
                           @SerializedName("cloudCover") val cloudCover: Double, @SerializedName("moonPhase") val moonPhase: Double,
                           @SerializedName("visibility") val visibility: Double, @SerializedName("uvIndex") val uvIndex: Long,
                           @SerializedName("uvIndexTime") val uvIndexTime: Long, @SerializedName("sunsetTime") val sunsetTime: Long,
                           @SerializedName("sunriseTime") val sunriseTime: Long, @SerializedName("precipIntensity") val precipIntensity: Double,
                           @SerializedName("precipIntensityMax") val precipIntensityMax: Double, @SerializedName("precipProbability") val precipProbability: Double,
                           @SerializedName("precipType") val precipType: String?
)