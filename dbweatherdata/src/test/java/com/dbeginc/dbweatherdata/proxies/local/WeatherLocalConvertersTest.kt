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

package com.dbeginc.dbweatherdata.proxies.local

import com.dbeginc.dbweatherdata.getFullWeatherJVM
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather
import org.junit.Before
import org.junit.Test

/**
 * Created by darel on 02.10.17.
 *
 * Testing Local Converters
 */
class WeatherLocalConvertersTest {
    private lateinit var weather: LocalWeather
    private val converters = WeatherLocalConverters()
    @Before
    fun setUp() {
        weather = getFullWeatherJVM()
    }

    @Test
    fun `converting list of minutely weather to json string and json string to list of minutely weather`() {
        val minutelyJson = converters.minutelyDataToJson(weather.minutely?.data)

        assert(weather.minutely?.data == converters.jsonStringToMinutelyData(minutelyJson))
    }

    @Test
    fun `converting list of hourly weather to json string and json string to list hourly weather`() {
        val hourlyJson = converters.hourlyDataToJson(weather.hourly.data)

        assert(weather.hourly.data == converters.jsonStringToHourlyData(hourlyJson))
    }

    @Test
    fun `converting list of daily weather to json string and json string to list of daily weather`() {
        val dailyJson = converters.dailyDataToJson(weather.daily.data)

        assert(weather.daily.data == converters.jsonStringToDailyData(dailyJson))
    }

    @Test
    fun `converting list of weather alerts to json string and json string to list of weather alerts`() {
        val alertsJson = converters.alertsToJson(weather.alerts)

        assert(weather.alerts == converters.jsonStringToAlerts(alertsJson))
    }

    @Test
    fun `should return empty string if value minutely weather list is null`() {
        assert("" == converters.minutelyDataToJson(null))
    }

    @Test
    fun `should return null if minutely json string is empty`() {
        assert(emptyList<String>() == converters.jsonStringToMinutelyData(""))
    }

    @Test
    fun `should return empty string if list of alerts is null`() {
        assert("" == converters.alertsToJson(null))
    }

    @Test
    fun `should return null if json string of alerts is empty`() {
        assert(null == converters.jsonStringToAlerts(""))
    }
}