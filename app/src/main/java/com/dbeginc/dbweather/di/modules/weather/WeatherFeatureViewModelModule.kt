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

package com.dbeginc.dbweather.di.modules.weather

import android.arch.lifecycle.ViewModel
import com.dbeginc.dbweather.di.ViewModelKey
import com.dbeginc.dbweatherweather.chooselocations.ChooseLocationsViewModel
import com.dbeginc.dbweatherweather.fullweather.WeatherViewModel
import com.dbeginc.dbweatherweather.managelocations.ManageLocationsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WeatherFeatureViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    abstract fun bindWeatherViewModel(weatherViewModel: WeatherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ManageLocationsViewModel::class)
    abstract fun bindManageLocationsViewModel(manageLocationsViewModel: ManageLocationsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChooseLocationsViewModel::class)
    abstract fun bindChooseLocationsViewModel(chooseLocationsViewModel: ChooseLocationsViewModel): ViewModel
}