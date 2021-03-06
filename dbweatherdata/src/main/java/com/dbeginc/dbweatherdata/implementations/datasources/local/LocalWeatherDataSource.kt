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

package com.dbeginc.dbweatherdata.implementations.datasources.local

import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by darel on 15.09.17.
 *
 * Local Weather Data Provider
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface LocalWeatherDataSource {
    fun getWeather(request: WeatherRequest<String>): Flowable<Weather>

    fun getWeatherForLocation(locationName: String, countryCode: String): Flowable<Weather>

    fun getUserLocations(): Flowable<List<Location>>

    fun updateWeather(weather: Weather) : Completable

    fun updateWeatherLocation(weather: Weather) : Completable

    fun deleteWeatherForLocation(locationName: String): Completable
}