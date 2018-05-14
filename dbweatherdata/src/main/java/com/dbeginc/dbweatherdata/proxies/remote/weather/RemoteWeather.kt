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

package com.dbeginc.dbweatherdata.proxies.remote.weather

import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.GeonamesItem
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocation
import com.google.gson.annotations.SerializedName

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Info
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class RemoteWeather(
        @Transient var location: GeonamesItem?,
        @SerializedName("latitude") val latitude: Double,
        @SerializedName("longitude") val longitude: Double,
        @SerializedName("timezone") val timezone: String,
        @SerializedName("currently") val currently: RemoteCurrently,
        @SerializedName("minutely") val minutely: RemoteMinutely?,
        @SerializedName("hourly") val hourly: RemoteHourly,
        @SerializedName("daily") val daily: RemoteDaily,
        @SerializedName("alerts") val alerts: List<RemoteAlert>?,
        @SerializedName("flags") val flags: RemoteFlags
)