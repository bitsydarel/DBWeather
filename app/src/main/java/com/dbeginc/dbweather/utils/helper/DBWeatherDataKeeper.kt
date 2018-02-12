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

package com.dbeginc.dbweather.utils.helper

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.SparseIntArray
import com.dbeginc.dbweather.R
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import com.dbeginc.dbweatherweather.fullweather.contract.WeatherPresenter
import com.dbeginc.dbweatherweather.viewmodels.WeatherModel
import io.reactivex.disposables.CompositeDisposable
import java.util.*

/**
 * Created by darel on 09.02.18.
 *
 * DBWeather App Data Keeper
 */
class DBWeatherDataKeeper : ViewModel() {
    private val subscriptions = CompositeDisposable()
    private lateinit var weatherPresenter: WeatherPresenter

    private val mColors = SparseIntArray().apply {
        put(R.drawable.clear_day, R.color.clear_day_background)
        put(R.drawable.clear_night, R.drawable.clear_night_background)
        put(R.drawable.partly_cloudy, R.drawable.partly_cloudy_background)
        put(R.drawable.cloudy_night, R.drawable.cloudy_night_background)
        put(R.drawable.cloudy, R.drawable.cloudy_background)
        put(R.drawable.fog, R.color.fog_background)
        put(R.drawable.sleet, R.color.clear_day_background)
        put(R.drawable.snow, R.color.snow_background)
        put(R.drawable.wind, R.color.wind_background)
        put(R.drawable.rain, R.color.rain_background)
    }

    private val weather: MutableLiveData<WeatherModel> = MutableLiveData()

    /*var weather: WeatherModel? = null
        set(value) {
            when {
                value == null -> return
                field == null -> field = value
                else -> {
                    field?.apply {
                        current = value.current
                        daily = value.daily
                        hourly = value.hourly
                        alerts = value.alerts
                        location = value.location
                    }
                }
            }
        }*/

    val newsPapers: LinkedList<NewsPaperModel> = LinkedList()

    fun getWeatherBackground(type: Int): Int = mColors[type]

    override fun onCleared() = subscriptions.clear()
}