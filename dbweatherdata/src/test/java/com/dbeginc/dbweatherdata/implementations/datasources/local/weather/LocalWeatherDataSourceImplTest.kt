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

package com.dbeginc.dbweatherdata.implementations.datasources.local.weather

import com.dbeginc.dbweatherdata.getWeatherJVM
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.local.weather.room.LocalCurrentWeatherDatabase
import com.dbeginc.dbweatherdata.implementations.datasources.local.weather.room.LocalLocationWeatherDatabase
import com.dbeginc.dbweatherdata.implementations.datasources.local.weather.room.LocalWeatherDao
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalLocation
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdomain.entities.requests.weather.LocationRequest
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import io.reactivex.Maybe
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by darel on 02.10.17.
 *
 * Local Unit Test Of Weather Data Source
 */
@RunWith(MockitoJUnitRunner::class)
class LocalWeatherDataSourceImplTest {
    @Mock lateinit var currentDB: LocalCurrentWeatherDatabase
    @Mock lateinit var locationDB: LocalLocationWeatherDatabase
    @Mock lateinit var weatherDao: LocalWeatherDao
    private lateinit var localWeatherDataSource: LocalWeatherDataSource
    private lateinit var weather: LocalWeather
    private val paris = LocalLocation("Paris", 50.3, 0.0, "FR", "France")

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }

        localWeatherDataSource = LocalWeatherDataSourceImpl.create(currentDB, locationDB)
        weather = getWeatherJVM()

        Mockito.`when`(currentDB.weatherDao()).thenReturn(weatherDao)
        Mockito.`when`(locationDB.weatherDao()).thenReturn(weatherDao)
    }

    @Test
    fun getWeather() {
        Mockito.`when`(currentDB.weatherDao().getWeatherByLocation(weather.location.locationName)).thenReturn(Maybe.just(weather))

        localWeatherDataSource.getWeather(WeatherRequest(weather.latitude, weather.longitude, weather.location.locationName))
                .test()
                .assertValue(weather.toDomain())
    }

    @Test
    fun getWeatherForLocation() {
        Mockito.`when`(locationDB.weatherDao().getWeatherByLocation(weather.location.locationName)).thenReturn(Maybe.just(weather))

        localWeatherDataSource.getWeatherForLocation(weather.location.locationName)
                .test()
                .assertValue(weather.toDomain())
    }

    @Test
    fun getLocations() {
        Mockito.`when`(locationDB.weatherDao().getLocations(weather.location.locationName)).thenReturn(Maybe.just(listOf(weather.location)))

        localWeatherDataSource.getLocations(LocationRequest(weather.location.locationName))
                .test()
                .assertValue(listOf(weather.location.toDomain()))
    }

    @Test
    fun getUserLocations() {
        Mockito.`when`(locationDB.weatherDao().getUserLocations()).thenReturn(Maybe.just(listOf(weather.location, paris)))

        localWeatherDataSource.getUserLocations()
                .test()
                .assertValue(listOf(weather.location.toDomain(), paris.toDomain()))
    }

    @Test
    fun updateWeather() {
        localWeatherDataSource.updateWeather(weather.toDomain())
                .test()
                .assertComplete()

        // Check that locationDB was not call
        Mockito.verify(locationDB, Mockito.times(0)).weatherDao()
        // Check that currentDB was not call
        Mockito.verify(currentDB, Mockito.times(2)).weatherDao()
        // check that the proper method was call
        Mockito.verify(weatherDao, Mockito.times(1)).deleteAll()
        Mockito.verify(weatherDao, Mockito.times(1)).putWeather(weather)
    }

    @Test
    fun updateWeatherLocation() {
        localWeatherDataSource.updateWeatherLocation(weather.toDomain())
                .test()
                .assertComplete()

        Mockito.verify(locationDB, Mockito.times(1)).weatherDao()
        Mockito.verify(weatherDao, Mockito.times(1)).putWeather(weather)

        // Check that delete all was not call
        Mockito.verify(weatherDao, Mockito.times(0)).deleteAll()
    }

    @Test
    fun deleteWeatherForLocation() {
        localWeatherDataSource.deleteWeatherForLocation(weather.toDomain())
                .test()
                .assertComplete()

        Mockito.verify(locationDB, Mockito.times(1)).weatherDao()
        Mockito.verify(locationDB, Mockito.times(1)).weatherDao()
        Mockito.verify(weatherDao, Mockito.times(1)).deleteWeather(weather)
    }

}