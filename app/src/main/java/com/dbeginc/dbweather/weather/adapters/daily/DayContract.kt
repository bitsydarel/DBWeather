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

package com.dbeginc.dbweather.weather.adapters.daily

import com.dbeginc.dbweather.base.IPresenter
import com.dbeginc.dbweather.base.IView
import com.dbeginc.dbweatherweather.viewmodels.DayWeatherModel

/**
 * Created by darel on 23.09.17.
 *
 * Day Contract
 */
interface DayContract {

    interface DayView : IView {
        fun displayDay(day: DayWeatherModel)

        fun showDetail()

        fun setupActionListener(presenter: DayPresenter)
    }

    interface DayPresenter : IPresenter<DayView>, Comparable<DayPresenter> {
        fun getModel(): DayWeatherModel

        fun setModel(dayWeatherModel: DayWeatherModel)

        fun loadDay()

        fun goToDayDetail()

        override fun compareTo(other: DayPresenter): Int = getModel().compareTo(other.getModel())
    }
}