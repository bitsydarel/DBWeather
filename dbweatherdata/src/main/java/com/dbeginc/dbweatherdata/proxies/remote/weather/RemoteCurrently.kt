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
 * Remote Currently Weather
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class RemoteCurrently(@SerializedName("time") val time: Long, @SerializedName("summary") val summary: String, @SerializedName("icon") val icon: String,
                           @SerializedName("temperature") val temperature: Double, @SerializedName("apparentTemperature") val apparentTemperature: Double?,
                           @SerializedName("precipIntensity") val precipIntensity: Double, @SerializedName("precipIntensityError") val precipIntensityError: Double?,
                           @SerializedName("precipProbability") val precipProbability: Double, @SerializedName("precipType") val precipType: String?,
                           @SerializedName("nearestStormDistance") val nearestStormDistance: Long?, @SerializedName("nearestStormBearing") val nearestStormBearing: Long?,
                           @SerializedName("humidity") val humidity: Double, @SerializedName("windSpeed") val windSpeed: Double,
                           @SerializedName("cloudCover") val cloudCover: Double, @SerializedName("windBearing") val windBearing: Long?,
                           @SerializedName("visibility") val visibility: Double, @SerializedName("dewPoint") val dewPoint: Double?,
                           @SerializedName("pressure") val pressure: Double
)