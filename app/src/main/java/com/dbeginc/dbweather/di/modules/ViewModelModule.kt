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

package com.dbeginc.dbweather.di.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.dbeginc.dbweather.di.ViewModelKey
import com.dbeginc.dbweather.viewmodels.DBWeatherViewModelFactory
import com.dbeginc.dbweathercommon.utils.AppScope
import com.dbeginc.dbweathernews.lives.LivesViewModel
import com.dbeginc.dbweathernews.newspapers.NewsPapersViewModel
import com.dbeginc.dbweathernews.sourcesmanager.SourcesManagerViewModel
import com.dbeginc.dbweatherweather.chooselocations.ChooseLocationsViewModel
import com.dbeginc.dbweatherweather.fullweather.WeatherViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


/**
 * Created by darel on 09.02.18.
 *
 * ViewModel Module
 */

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(WeatherViewModel::class)
    abstract fun bindWeatherViewModel(weatherViewModel: WeatherViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsPapersViewModel::class)
    abstract fun bindNewsPapersViewModel(newsPapersViewModel: NewsPapersViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LivesViewModel::class)
    abstract fun bindLivesViewModel(livesViewModel: LivesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SourcesManagerViewModel::class)
    abstract fun bindSourcesManagerViewModel(sourcesManagerViewModel: SourcesManagerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ChooseLocationsViewModel::class)
    abstract fun bindChooseLocationsViewModel(chooseLocationsViewModel: ChooseLocationsViewModel): ViewModel

    @AppScope
    @Binds
    abstract fun bindViewModelFactory(factory: DBWeatherViewModelFactory): ViewModelProvider.Factory
}