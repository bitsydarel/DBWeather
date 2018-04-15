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

package com.dbeginc.dbweatherdata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.dbeginc.dbweatherdata.implementations.datasources.local.weather.LocalCurrentWeatherDatabase
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by darel on 02.10.17.
 *
 * Local Weather Dao Test with Room Persistence library
 */
@RunWith(AndroidJUnit4::class)
class LocalWeatherDaoTest {
    @Rule @JvmField val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var db: LocalCurrentWeatherDatabase
    private lateinit var weather : LocalWeather
    private lateinit var fullWeather : LocalWeather

    @Before
    fun initDb() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), LocalCurrentWeatherDatabase::class.java).allowMainThreadQueries().build()
//        weather = getWeatherAndroid()
        fullWeather = getFullWeatherAndroid()
    }

    @After
    fun closeDB() { db.close() }


    @Test
    fun should_insert_weather_and_get_current_weather() {
        // Inserting the current weather
        db.weatherDao().putWeather(weather)

        // get the weather by location
        db.weatherDao()
                .getWeatherByLocation(weather.location.locationName)
                .test()
                .assertValue(weather)
    }

    @Test
    fun should_update_weather() {
        db.weatherDao().putWeather(weather)

        db.weatherDao().getWeatherByLocation(weather.location.locationName)
                .test()
                .assertValue(weather)

        val newWeather = weather.copy(timezone = "Paris/France")

        db.weatherDao().putWeather(newWeather)

        db.weatherDao().getWeatherByLocation(weather.location.locationName)
                .test()
                .assertValue { dbweather -> dbweather.timezone == newWeather.timezone }
    }

    @Test
    fun should_delete_all_weather() {
        db.weatherDao().putWeather(weather)
        db.weatherDao().putWeather(fullWeather)

        db.weatherDao().deleteAll()

        db.weatherDao()
                .getWeatherByLocation(weather.location.locationName)
                .test()
                .assertComplete()

        db.weatherDao().getWeatherByLocation(fullWeather.location.locationName)
                .test()
                .assertComplete()

        db.weatherDao().getUserLocations()
                .test()
                .assertValue(emptyList())
    }

    @Test
    fun should_delete_specific_weather() {
        // Put Two weather object in the db
        db.weatherDao().putWeather(weather)
        db.weatherDao().putWeather(fullWeather)

        // Checking if weather successfully added
        db.weatherDao().getWeatherByLocation(weather.location.locationName)
                .test()
                .assertValue(weather)

        db.weatherDao().getWeatherByLocation(fullWeather.location.locationName)
                .test()
                .assertValue(fullWeather)

        // delete one weather location
        db.weatherDao().deleteWeather(weather.location.locationName)

        // check that weather and location has been successfully deleted
        db.weatherDao().getWeatherByLocation(weather.location.locationName)
                .test()
                .assertComplete()

        // checking if other location still in the db
        db.weatherDao().getWeatherByLocation(fullWeather.location.locationName)
                .test()
                .assertValue(fullWeather)
    }


    @Test
    fun should_get_user_locations() {
        db.weatherDao().putWeather(weather)
        db.weatherDao().putWeather(fullWeather)

        db.weatherDao().getUserLocations()
                .test()
                .assertValueAt(0, { locations -> locations.size == 2 && locations.contains(weather.location) && locations.contains(fullWeather.location) })
    }

    @Test
    fun should_insert_weather_if_primary_key_different() {
        db.weatherDao().putWeather(weather)

        db.weatherDao().getUserLocations()
                .test()
                .assertValue {
                    locations -> locations.size == 1
                }

        db.weatherDao().putWeather(weather.copy(location=weather.location.copy(locationLatitude= 52.26, locationLongitude=86.5)))

        db.weatherDao()
                .getUserLocations()
                .test()
                .assertValue {
                    locations -> locations.size == 1
                }

        db.weatherDao().putWeather(weather.copy(location=weather.location.copy(locationName="Lviv")))

        db.weatherDao()
                .getUserLocations()
                .test()
                .assertValue {
                    locations -> locations.size == 2
                }

        db.weatherDao().putWeather(weather.copy(location=weather.location.copy(countryCode="FR")))

        db.weatherDao()
                .getUserLocations()
                .test()
                .assertValue {
                    locations -> locations.size == 3
                }

    }
}