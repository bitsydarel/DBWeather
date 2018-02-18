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
import com.dbeginc.dbweatherdomain.entities.weather.HourlyData
import com.google.gson.annotations.SerializedName

/**
 * Created by darel on 16.09.17.
 *
 * Remote Hourly Weather Data
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class RemoteHourlyData(@SerializedName("time") val time: Long, @SerializedName("summary") val summary: String,
                            @SerializedName("icon") val icon: String, @SerializedName("temperature") val temperature: Double,
                            @SerializedName("apparentTemperature") val apparentTemperature: Double, @SerializedName("dewPoint") val dewPoint: Double,
                            @SerializedName("humidity") val humidity: Double, @SerializedName("pressure") val pressure: Double,
                            @SerializedName("windSpeed") val windSpeed: Double, @SerializedName("windGust") val windGust: Double?,
                            @SerializedName("windBearing") val windBearing: Long?, @SerializedName("cloudCover") val cloudCover: Double,
                            @SerializedName("precipIntensity") val precipIntensity: Double, @SerializedName("precipProbability") val precipProbability: Double,
                            @SerializedName("precipType") val precipType: String?, @SerializedName("uvIndex") val uvIndex: Long,
                            @SerializedName("ozone") val ozone: Double) {

    fun toDomain() : HourlyData {
        return HourlyData(time, summary, icon, temperature, apparentTemperature, dewPoint, humidity, pressure,
                windSpeed, windGust, windBearing, cloudCover, precipIntensity, precipProbability, precipType, uvIndex, ozone
        )
    }
}