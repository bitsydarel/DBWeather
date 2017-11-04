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

package com.dbeginc.dbweatherdomain.usecases.weather

import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import com.dbeginc.dbweatherdomain.usecases.UseCase
import io.reactivex.Flowable

/**
 * Created by darel on 27.09.17.
 *
 * Get Weather By Location
 */
class GetWeatherByLocation(private val repo: WeatherRepository) : UseCase<Weather, WeatherRequest<String>>() {

    override fun buildUseCase(params: WeatherRequest<String>): Flowable<Weather> = repo.getWeatherForLocation(params)
    override fun clean() = repo.clean()

}