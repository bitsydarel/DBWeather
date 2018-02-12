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

package com.dbeginc.dbweatherdata.implementations.datasources.local.weather.room

import android.arch.persistence.room.*
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.ConstantHolder
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalLocation
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by darel on 16.09.17.
 *
 * Local Weather Dao
 */
@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface LocalWeatherDao {

    /**
     * Get Weather From database
     * @param location name
     * Returning single not flowable because flowable will hang on if no value in database
     * {link https://medium.com/google-developers/room-rxjava-acb0cd4f3757}
     * @return [Single] of [LocalWeather]
     */
    @Query(value = "SELECT * FROM ${ConstantHolder.WEATHER_TABLE} WHERE location_name LIKE :location")
    fun getWeatherByLocation(location: String): Flowable<LocalWeather>

    @Query(value = "SELECT location_name, location_latitude, location_longitude, country_code, country_name FROM ${ConstantHolder.WEATHER_TABLE} WHERE location_name LIKE :name")
    fun getLocations(name: String): Maybe<List<LocalLocation>>

    @Query(value="SELECT location_name, location_latitude, location_longitude, country_code, country_name FROM ${ConstantHolder.WEATHER_TABLE}")
    fun getUserLocations() : Maybe<List<LocalLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putWeather(weather: LocalWeather)

    @Delete
    fun deleteWeather(weather: LocalWeather)

    @Query("DELETE FROM ${ConstantHolder.WEATHER_TABLE}")
    fun deleteAll()
}