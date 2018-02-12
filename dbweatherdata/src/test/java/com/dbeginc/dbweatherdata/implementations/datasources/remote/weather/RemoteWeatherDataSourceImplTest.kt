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

import com.dbeginc.dbweatherdata.getFileAsStringJVM
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.retrofit.WeatherRestAdapter
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.remote.weather.RemoteWeather
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocation
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocations
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.squareup.moshi.Moshi
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

/**
 * Created by darel on 08.02.18.
 *
 * Remote Weather Data Source Implementation test
 */
class RemoteWeatherDataSourceImplTest {
    private lateinit var remoteWeatherDataSource: RemoteWeatherDataSource
    private lateinit var weather: RemoteWeather
    private val adapter: WeatherRestAdapter = mock<WeatherRestAdapter>(WeatherRestAdapter::class.java)

    @Before
    fun setUp() {

        remoteWeatherDataSource = RemoteWeatherDataSourceImpl.create(adapter)

        val moshi = Moshi.Builder().build()
        val weatherConverter = moshi.adapter(RemoteWeather::class.java)

        weather = weatherConverter.fromJson(getFileAsStringJVM("full_weather.json"))!!

    }

    @Test
    fun getWeather() {
        Mockito.`when`(adapter.getWeather(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(Single.just(weather))

        remoteWeatherDataSource.getWeather(WeatherRequest(0.0, 0.0, Unit))
                .test()
                .assertValue(weather.toDomain())
                .assertNoErrors()
                .assertNoTimeout()
                .assertComplete()

        Mockito.verify(adapter, Mockito.only()).getWeather(Mockito.anyDouble(), Mockito.anyDouble())
    }

    @Test
    fun getWeatherForLocation() {
        val request = WeatherRequest(0.0, 0.0, Unit)

        Mockito.`when`(adapter.getWeather(Mockito.anyDouble(), Mockito.anyDouble())).thenReturn(Single.just(weather))

        remoteWeatherDataSource.getWeatherForLocation(request)
                .test()
                .assertValue(weather.toDomain())
                .assertNoErrors()
                .assertNoTimeout()
                .assertComplete()

        Mockito.verify(adapter, Mockito.only()).getWeather(Mockito.anyDouble(), Mockito.anyDouble())
    }

    @Test
    fun getLocations() {
        val locations = RemoteLocations(
                mutableListOf(
                        RemoteLocation("Kiev", 46.2323, 32.2332, "UA", "Ukraine"),
                        RemoteLocation("Pointe-Noire", 23.2313, 43.422, "CG", "Congo")
                )
        )

        Mockito.`when`(
                adapter.getLocationsFor(
                        Mockito.anyString()
                )
        ).thenReturn(Single.just(locations))

        remoteWeatherDataSource.getLocations(Mockito.anyString())
                .test()
                .assertValue { it.size == 2 }
                .assertNoErrors()
                .assertNoTimeout()
                .assertComplete()

        Mockito.verify(adapter, Mockito.only()).getLocationsFor(Mockito.anyString())
    }

}