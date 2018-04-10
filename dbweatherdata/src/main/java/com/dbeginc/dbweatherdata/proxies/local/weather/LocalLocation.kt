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

import android.arch.persistence.room.ColumnInfo
import android.support.annotation.RestrictTo

/**
 * Created by darel on 16.09.17.
 *
 * Local Location Info
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
data class LocalLocation(
        @ColumnInfo(name = "location_name") val locationName: String,
        @ColumnInfo(name = "location_latitude") val locationLatitude: Double,
        @ColumnInfo(name = "location_longitude") val locationLongitude: Double,
        @ColumnInfo(name = "country_code") val countryCode: String,
        @ColumnInfo(name = "country_name") val countryName: String
)