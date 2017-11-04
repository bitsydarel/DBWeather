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

package com.dbeginc.dbweather.weather

import com.dbeginc.dbweather.base.IPresenter
import com.dbeginc.dbweather.base.IView
import com.dbeginc.dbweather.viewmodels.weather.LocationWeatherModel
import com.dbeginc.dbweather.viewmodels.weather.WeatherModel

/**
 * Created by darel on 18.09.17.
 *
 * Weather Screen Contract
 */
interface WeatherTabContract {

    interface WeatherTabView: IView {
        fun getLatitude() : Double
        fun getLongitude() : Double
        fun getCurrentLocation() : String
        fun displayWeather(weather: WeatherModel)
        fun defineUserLocations(locations: List<LocationWeatherModel>)
        fun defineGpsLocation(location: LocationWeatherModel)
        fun defineUserLocation(location: LocationWeatherModel)
        fun defineGpsLatitude(latitude: Double)
        fun defineGpsLongitude(longitude: Double)
        fun isCurrentLocation(isCurrent: Boolean)
        fun displayLoadingWeatherStatus()
        fun hideLoadingWeatherStatus()
        fun displayUpdatingStatus()
        fun hideUpdatingStatus()
        fun showWeatherError(message: String)
        fun showError(message: String)
    }

    interface WeatherTabPresenter: IPresenter<WeatherTabView> {
        fun getWeatherForCity(location: LocationWeatherModel)
        fun getWeather()
        fun onGpsLocationChange(latitude: Double, longitude: Double)
        fun loadUserCities()
        fun onLocationSelected(position: Int)
        fun retryWeatherRequest()
    }
}