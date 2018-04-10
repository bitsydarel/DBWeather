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

package com.dbeginc.dbweatherweather.viewmodels

/**
 * Created by darel on 30.09.17.
 *
 * Location Weather Model
 */
data class WeatherLocationModel(
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val countryCode: String,
        val countryName: String
) : Comparable<WeatherLocationModel> {

    override fun compareTo(other: WeatherLocationModel): Int = if (name != other.name) name.compareTo(other.name) else countryCode.compareTo(other.countryCode)
}