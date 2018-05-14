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

package com.dbeginc.dbweather.di.components

import com.dbeginc.dbweather.di.modules.ApplicationModule
import com.dbeginc.dbweather.di.modules.DataModule
import com.dbeginc.dbweather.di.modules.PresentationModule
import com.dbeginc.dbweather.di.modules.lives.LivesFeatureModule
import com.dbeginc.dbweather.di.modules.lives.LivesViewModelModule
import com.dbeginc.dbweather.di.modules.news.NewsFeatureModule
import com.dbeginc.dbweather.di.modules.news.NewsFeatureViewModelModule
import com.dbeginc.dbweather.di.modules.weather.WeatherFeatureModule
import com.dbeginc.dbweather.di.modules.weather.WeatherFeatureViewModelModule
import com.dbeginc.dbweather.utils.services.WeatherSyncJob
import com.dbeginc.dbweathercommon.utils.AppScope
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.android.support.DaggerApplication

/**
 * Created by Darel Bitsy on 24/04/17.
 * DBWeather Application Component
 */
@AppScope
@Component(modules = [
    ApplicationModule::class,
    DataModule::class,
    PresentationModule::class,
    NewsFeatureModule::class,
    NewsFeatureViewModelModule::class,
    LivesFeatureModule::class,
    LivesViewModelModule::class,
    WeatherFeatureModule::class,
    WeatherFeatureViewModelModule::class,
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class])
interface ApplicationComponent : AndroidInjector<DaggerApplication> {
    @dagger.Component.Builder
    abstract class Builder : AndroidInjector.Builder<DaggerApplication>() {
        abstract override fun build() : ApplicationComponent
    }

    fun injectAndroidJob(job: WeatherSyncJob)
}
