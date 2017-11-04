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

package com.dbeginc.dbweatherdata

import com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.retrofit.WeatherRestAdapter
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit


/**
 * Created by darel on 01.10.17.
 *
 * Weather Remote Api Test
 */
@RunWith(JUnit4::class)
class WeatherRemoteApiTest {
    private lateinit var adapter: WeatherRestAdapter

    @Before
    fun setup() {
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
    fun shouldReturnWeather() {
        adapter.getWeather(50.45,30.5233).subscribe {
            weather -> assert(weather != null)
        }
    }

    @Test
    fun shouldReturnError() {
        adapter.getWeather(0.0, 0.0).subscribe({
            weather -> weather == null
        }, {
            error -> assert(error != null)
        })
    }
}