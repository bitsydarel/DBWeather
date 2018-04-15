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

import com.dbeginc.dbweather.chooselocations.ChooseLocationsFragment
import com.dbeginc.dbweather.findlocationwithgps.GpsLocationFinderFragment
import com.dbeginc.dbweather.managelocations.ManageLocationsFragment
import com.dbeginc.dbweather.weather.WeatherFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by darel on 02.02.18.
 *
 * Dagger Weather Module that provide weather feature
 * dependencies
 */
@Module
abstract class WeatherFeatureModule {
    @ContributesAndroidInjector
    abstract fun contributeWeatherFragment(): WeatherFragment

    @ContributesAndroidInjector
    abstract fun contributeManageLocationsFragment(): ManageLocationsFragment

    @ContributesAndroidInjector()
    abstract fun contributeChooseLocationsFragment(): ChooseLocationsFragment

    @ContributesAndroidInjector()
    abstract fun contributeGpsLocationFinder(): GpsLocationFinderFragment
}