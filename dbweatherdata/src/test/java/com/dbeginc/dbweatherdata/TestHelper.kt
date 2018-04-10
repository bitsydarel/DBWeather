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

import com.dbeginc.dbweatherdata.proxies.local.weather.LocalLocation
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.*

/**
 * Created by darel on 02.10.17.
 *
 * Local Test Helper
 */

fun getWeatherJVM() : LocalWeather {
    val builder = StringBuilder()

    Scanner(Thread.currentThread().contextClassLoader.getResourceAsStream("weather.json"))
            .forEach { line -> builder.append(line) }

    val weather: LocalWeather = Gson().fromJson(builder.toString(), object : TypeToken<List<LocalWeather>>() {}.type)

    weather.location = LocalLocation("Ternopil", weather.latitude, weather.longitude, "UA", "Ukraine")
    return weather
}

fun getFullWeatherJVM() : LocalWeather {
    val builder = StringBuilder()

    Scanner(Thread.currentThread().contextClassLoader.getResourceAsStream("full_weather.json"))
            .forEach { line -> builder.append(line) }


    val weather: LocalWeather = Gson().fromJson(builder.toString(), object : TypeToken<List<LocalWeather>>() {}.type)

    weather.location = LocalLocation("Miami Beach", weather.latitude, weather.longitude, "US", "United States")

    return weather
}

fun getFile(fileName: String): File = File(Thread.currentThread().contextClassLoader.getResource(fileName).toURI())

fun getFileAsStringJVM(fileName: String) : String {
    val builder = StringBuilder()

    val data = Scanner(Thread.currentThread().contextClassLoader.getResourceAsStream(fileName))

    while (data.hasNextLine()) builder.append(data.nextLine())

    return builder.toString()
}