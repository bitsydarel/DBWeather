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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.weather

import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.retrofit.WeatherRestAdapter
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdomain.entities.requests.weather.LocationRequest
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import io.reactivex.Flowable

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Data Provider
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class RemoteWeatherDataSourceImpl private constructor(private val apiManager: WeatherRestAdapter) : RemoteWeatherDataSource {

    companion object {
        fun create(context: Context): RemoteWeatherDataSource {
            return RemoteWeatherDataSourceImpl(WeatherRestAdapter.create(context))
        }
    }

    override fun getWeather(request: WeatherRequest<Unit>): Flowable<Weather> {
        return apiManager.getWeather(latitude=request.latitude, longitude=request.longitude)
                .map { proxy -> proxy.toDomain() }
                .toFlowable()
    }

    override fun getWeatherForLocation(request: WeatherRequest<Unit>): Flowable<Weather> {
        return apiManager.getWeather(latitude=request.latitude, longitude=request.longitude)
                .map { proxy -> proxy.toDomain() }
                .toFlowable()
    }

    override fun getLocations(request: LocationRequest): Flowable<List<Location>> {
        return apiManager.getLocationsFor(request.query)
                .flatMapPublisher { locations ->
                    Flowable.just(locations.locations.map { location -> location.toDomain() })
                }
    }
}