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

package com.dbeginc.dbweatherdomain.repositories.weather

import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import com.dbeginc.dbweatherdomain.repositories.Cleanable
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by darel on 15.09.17.
 *
 * Weather Repository
 *
 * Interface following repository pattern.
 *
 * Each method in this interface are threaded as Use Cases (Interactor in terms of Clean Architecture).
 */
interface WeatherRepository : Cleanable {

    /**
     * Created by darel on 18.09.17.
     *
     * Get weather
     *
     * @param request containing information about where weather is requested
     *
     * @return [Flowable] reactive stream of [Weather] that provide the requested weather information
     */
    fun getWeather(request: WeatherRequest<String>) : Flowable<Weather>


    /**
     * Created by darel on 27.09.17.
     *
     * Get Weather for location
     *
     * @param request containing information about where weather is requested
     *
     * @return [Flowable] reactive stream of [Weather] that provide the requested weather information
     */
    fun getWeatherForLocation(request: WeatherRequest<String>) : Flowable<Weather>


    /**
     * Created by darel on 21.09.17.
     *
     * Get locations for a specific name
     *
     * @param name of the location to found
     *
     * @return [Flowable] reactive stream of list of locations that provide locations that match with the provided name
     */
    fun getLocations(name: String): Flowable<List<Location>>


    /**
     * Created by darel on 19.09.17.
     *
     * Get all user Locations
     *
     * @return [Flowable] reactive stream of list of locations that provide all locations currently available
     */
    fun getAllUserLocations() : Flowable<List<Location>>


    /**
     * Created by darel on 26.10.17.
     *
     * delete [Weather] for location
     *
     * @param name of the weather location
     *
     * @return [Completable] reactive stream that notify completion of the task
     */
    fun deleteWeatherForLocation(name: String): Completable
}