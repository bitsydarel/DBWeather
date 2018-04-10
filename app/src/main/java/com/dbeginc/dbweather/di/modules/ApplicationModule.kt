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

package com.dbeginc.dbweather.di.modules

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import com.dbeginc.dbweather.utils.contentprovider.LocationSuggestionProvider
import com.dbeginc.dbweathercommon.DBWeatherViewModelFactory
import com.dbeginc.dbweathercommon.utils.AppScope
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerApplication

/**
 * Created by Darel Bitsy on 24/04/17.
 *
 * Application Module
 *
 */
@Module
abstract class ApplicationModule {
    @AppScope
    @Binds
    abstract fun provideApplicationContext(application: DaggerApplication): Context

    @ContributesAndroidInjector
    abstract fun provideContentProvider(): LocationSuggestionProvider

    @AppScope
    @Binds
    abstract fun provideDBWeatherViewModelFactory(factory: DBWeatherViewModelFactory): ViewModelProvider.Factory
}
