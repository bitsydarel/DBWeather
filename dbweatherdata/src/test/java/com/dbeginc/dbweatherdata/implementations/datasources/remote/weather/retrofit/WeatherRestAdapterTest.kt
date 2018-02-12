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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.retrofit

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 08.02.18.
 *
 * Weather rest adapter test
 */
class WeatherRestAdapterTest {
    private lateinit var adapter: WeatherRestAdapter

    @Before
    fun setUp() {
        val client = OkHttpClient.Builder()
                .connectTimeout(35, TimeUnit.SECONDS)
                .writeTimeout(35, TimeUnit.SECONDS)
                .readTimeout(55, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

        adapter = WeatherRestAdapter.create(client)

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun getWeather() {
        adapter.getWeather(50.45, 30.5233)
                .test()
                .assertValue { weather -> weather.daily.data.isNotEmpty() && weather.timezone.isNotEmpty() }
    }

    @Test
    fun getLocationsFor() {
        adapter.getLocationsFor("paris")
                .test()
                .assertValue { location ->
                    location.locations.isNotEmpty() &&
                            location.locations[0].name.contains("paris", ignoreCase = true)
                }
    }

}