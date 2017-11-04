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

import android.support.test.InstrumentationRegistry
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalLocation
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather
import com.squareup.moshi.Moshi
import java.util.*

/**
 * Created by darel on 02.10.17.
 *
 * Android Instrumentation Test helper
 */

fun getWeatherAndroid(): LocalWeather {
    val builder = StringBuilder()
    Scanner(InstrumentationRegistry.getInstrumentation().context.assets.open("weather.json"))
            .forEach { line -> builder.append(line) }

    val weatherConverter = Moshi.Builder().build().adapter(LocalWeather::class.java)
    val weather: LocalWeather = weatherConverter.fromJson(builder.toString())!!

    weather.location = LocalLocation("Ternopil", weather.latitude, weather.longitude, "UA", "Ukraine")
    return weather
}

fun getFullWeatherAndroid(): LocalWeather {
    val builder = StringBuilder()
    Scanner(InstrumentationRegistry.getInstrumentation().context.assets.open("full_weather.json"))
            .forEach { line -> builder.append(line) }

    val weatherConverter = Moshi.Builder().build().adapter(LocalWeather::class.java)
    val weather: LocalWeather = weatherConverter.fromJson(builder.toString())!!

    weather.location = LocalLocation("Miami Beach", weather.latitude, weather.longitude, "US", "United States")
    return weather
}