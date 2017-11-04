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
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocation
import com.squareup.moshi.Json

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Info
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
data class RemoteWeather(@Transient var location: RemoteLocation?, @Json(name = "locationLatitude") val latitude: Double, @Json(name = "locationLongitude") val longitude: Double,
                         @Json(name = "timezone") val timezone: String, @Json(name = "currently") val currently: RemoteCurrently,
                         @Json(name = "minutely") val minutely: RemoteMinutely?, @Json(name = "hourly") val hourly: RemoteHourly,
                         @Json(name = "daily") val daily: RemoteDaily, @Json(name = "alerts") val alerts: List<RemoteAlert>?,
                         @Json(name = "flags") val flags: RemoteFlags
)