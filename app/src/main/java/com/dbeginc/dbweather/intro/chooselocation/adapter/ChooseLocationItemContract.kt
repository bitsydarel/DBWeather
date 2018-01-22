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

package com.dbeginc.dbweather.intro.chooselocation.adapter

import com.dbeginc.dbweather.base.IPresenter
import com.dbeginc.dbweather.base.IView
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel

/**
 * Created by darel on 30.09.17.
 *
 * Choose Location Item Contract
 */
interface ChooseLocationItemContract {
    interface ChooseLocationItemView : IView {
        fun displayLocation(location: LocationWeatherModel)

        fun setupClickForwarding(presenter: ChooseLocationItemPresenter)

        fun defineUserLocation(location: LocationWeatherModel)
    }

    interface ChooseLocationItemPresenter : IPresenter<ChooseLocationItemView> {
        fun getModel() : LocationWeatherModel

        fun loadLocation()

        fun onClick()
    }
}