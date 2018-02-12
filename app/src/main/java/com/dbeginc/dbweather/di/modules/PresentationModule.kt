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

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.dbeginc.dbweather.config.managelocations.presenter.ManageLocationsPresenter
import com.dbeginc.dbweather.config.managelocations.presenter.ManageLocationsPresenterImpl
import com.dbeginc.dbweather.config.presenter.ConfigurationTabPresenter
import com.dbeginc.dbweather.config.presenter.ConfigurationTabPresenterImpl
import com.dbeginc.dbweather.splash.presenter.SplashPresenter
import com.dbeginc.dbweather.splash.presenter.SplashPresenterImpl
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.dbeginc.dbweathercommon.utils.AppScope
import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweatherdomain.repositories.configurations.ConfigurationsRepository
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 18.09.17.
 *
 * Application Presentation Module
 */
@Module
class PresentationModule {
    @Provides
    fun provideManageLocationsPresenter(model: WeatherRepository): ManageLocationsPresenter = ManageLocationsPresenterImpl(model, ThreadProvider)

    @Provides
    fun provideConfigurationTabPresenter(model: ConfigurationsRepository): ConfigurationTabPresenter = ConfigurationTabPresenterImpl(model)

    @Provides
    fun provideSplashPresenter(model: NewsRepository): SplashPresenter = SplashPresenterImpl(model)

    @Provides
    @AppScope
    fun providesAppResource(context: Context): Resources = context.resources

    @Provides
    @AppScope
    fun providesSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(ConstantHolder.PREFS_NAME, Context.MODE_PRIVATE)

}