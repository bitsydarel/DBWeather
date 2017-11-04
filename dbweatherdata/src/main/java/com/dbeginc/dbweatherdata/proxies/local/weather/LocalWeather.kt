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

package com.dbeginc.dbweatherdata.proxies.local.weather

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.ConstantHolder.WEATHER_TABLE

/**
 * Created by darel on 16.09.17.
 *
 * Local Weather Implementation
 * Primary keys are two fields from [LocalLocation] pojo
 * because setting the full pojo as primary key
 * will lead to bug like the coordinates may be different for the same location name
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@Entity(
        tableName = WEATHER_TABLE,
        primaryKeys = arrayOf("location_name", "country_code")
)
data class LocalWeather(@Embedded() var location: LocalLocation, val latitude: Double, val longitude: Double,
                        val timezone: String, @Embedded(prefix= "current") val currently: LocalCurrently, @Embedded(prefix = "minutely") val minutely: LocalMinutely?,
                        @Embedded(prefix = "hourly") val hourly: LocalHourly, @Embedded(prefix = "daily") var daily: LocalDaily,
                        val alerts: List<LocalAlert>?, @Embedded(prefix = "flags") val flags: LocalFlags
)