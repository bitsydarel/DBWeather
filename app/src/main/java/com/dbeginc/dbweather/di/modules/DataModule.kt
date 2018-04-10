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

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.dbeginc.dbweather.utils.preferences.ApplicationPreferences
import com.dbeginc.dbweather.utils.utility.PREFS_NAME
import com.dbeginc.dbweathercommon.utils.AppScope
import com.dbeginc.dbweatherdata.CrashlyticsLogger
import com.dbeginc.dbweatherdata.RxThreadProvider
import com.dbeginc.dbweatherdata.implementations.repositories.LivesRepositoryImpl
import com.dbeginc.dbweatherdata.implementations.repositories.NewsRepositoryImpl
import com.dbeginc.dbweatherdata.implementations.repositories.WeatherRepositoryImpl
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.repositories.LivesRepository
import com.dbeginc.dbweatherdomain.repositories.NewsRepository
import com.dbeginc.dbweatherdomain.repositories.WeatherRepository
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 18.09.17.
 *
 * Application Data Module
 */
@Module
class DataModule {
    @Provides
    @AppScope
    fun providesAppResource(context: Context): Resources = context.resources

    @Provides
    @AppScope
    fun providesSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @AppScope
    @Provides
    fun provideThreadProvider(): ThreadProvider = RxThreadProvider

    @AppScope
    @Provides
    fun provideLogger(): Logger = CrashlyticsLogger

    @AppScope
    @Provides
    fun provideWeatherRepository(context: Context): WeatherRepository =
            WeatherRepositoryImpl.create(context)

    @AppScope
    @Provides
    fun provideNewsRepository(context: Context): NewsRepository =
            NewsRepositoryImpl.create(context)

    @AppScope
    @Provides
    fun provideLivesRepository(context: Context): LivesRepository =
            LivesRepositoryImpl.create(context)

    @Provides
    @AppScope
    fun provideApplicationPreferences(sharedPreferences: SharedPreferences): ApplicationPreferences =
            ApplicationPreferences(sharedPreferences)

}