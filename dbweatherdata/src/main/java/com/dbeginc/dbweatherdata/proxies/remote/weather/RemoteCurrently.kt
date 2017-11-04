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
 * Remote Currently Weather
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class RemoteCurrently(@Json(name= "time") val time: Long, @Json(name= "summary") val summary: String, @Json(name= "icon") val icon: String,
                           @Json(name= "temperature") val temperature: Double, @Json(name= "apparentTemperature") val apparentTemperature: Double?,
                           @Json(name= "precipIntensity") val precipIntensity: Double, @Json(name= "precipIntensityError") val precipIntensityError: Double?,
                           @Json(name= "precipProbability") val precipProbability: Double, @Json(name= "precipType") val precipType: String?,
                           @Json(name= "nearestStormDistance") val nearestStormDistance: Long?, @Json(name= "nearestStormBearing") val nearestStormBearing: Long?,
                           @Json(name = "humidity") val humidity: Double, @Json(name = "windSpeed") val windSpeed: Double,
                           @Json(name = "cloudCover") val cloudCover: Double, @Json(name = "windBearing") val windBearing: Long?,
                           @Json(name = "visibility") val visibility: Double, @Json(name = "dewPoint") val dewPoint: Double?,
                           @Json(name = "pressure") val pressure: Double
)