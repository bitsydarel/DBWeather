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

import com.dbeginc.dbweatherdomain.repositories.configurations.ConfigurationsRepository
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeWeatherNotificationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetWeatherNotificationStatus
import com.dbeginc.dbweatherdomain.usecases.news.*
import com.dbeginc.dbweatherdomain.usecases.weather.*
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 24.09.17.
 *
 * Use Case Module
 */
@Module
class UseCaseModule {

    @Provides
    internal fun provideRemoveLocationUseCase(weatherRepo: WeatherRepository): RemoveLocation = RemoveLocation(weatherRepo)

    @Provides
    internal fun provideGetSourceUseCase(newsRepository: NewsRepository): GetSource = GetSource(newsRepository)

    @Provides
    internal fun provideUnSubscribeToSourceUseCase(newsRepository: NewsRepository): UnSubscribeToSource = UnSubscribeToSource(newsRepository)

    @Provides
    internal fun provideSubscribeToSourceUseCase(newsRepository: NewsRepository): SubscribeToSource = SubscribeToSource(newsRepository)

    @Provides
    internal fun provideGetAllSources(newsRepository: NewsRepository): GetAllSources = GetAllSources(newsRepository)

    @Provides
    internal fun provideGetWeatherNotificationStatus(configurationsRepository: ConfigurationsRepository): GetWeatherNotificationStatus = GetWeatherNotificationStatus(configurationsRepository)

    @Provides
    internal fun provideGetNewsPaperTranslationStatus(configurationsRepository: ConfigurationsRepository): GetNewsPaperTranslationStatus = GetNewsPaperTranslationStatus(configurationsRepository)

    @Provides
    internal fun provideChangeWeatherNotification(configurationsRepository: ConfigurationsRepository): ChangeWeatherNotificationStatus = ChangeWeatherNotificationStatus(configurationsRepository)

    @Provides
    internal fun provideChangeNewsPaperTranslation(configurationsRepository: ConfigurationsRepository): ChangeNewsPaperTranslationStatus = ChangeNewsPaperTranslationStatus(configurationsRepository)

    @Provides
    internal fun provideGetLive(newsRepository: NewsRepository): GetLive = GetLive(newsRepository)

    @Provides
    internal fun provideGetLives(newsRepository: NewsRepository): GetLives = GetLives(newsRepository)

    @Provides
    internal fun provideGetAllLives(newsRepository: NewsRepository) : GetAllLives = GetAllLives(newsRepository)

    @Provides
    internal fun provideGetFavoriteLives(newsRepository: NewsRepository) : GetFavoriteLives = GetFavoriteLives(newsRepository)

    @Provides
    internal fun provideGetLocationsUseCase(weatherRepo: WeatherRepository) : GetLocations = GetLocations(weatherRepo)

    @Provides
    internal fun provideGetWeatherUseCase(weatherRepo: WeatherRepository) : GetWeather = GetWeather(weatherRepo)

    @Provides
    internal fun provideGetAllUserListUseCase(weatherRepo: WeatherRepository) : GetAllUserLocations = GetAllUserLocations(weatherRepo)

    @Provides
    internal fun provideGetWeatherByLocationUseCase(weatherRepo: WeatherRepository) : GetWeatherByLocation = GetWeatherByLocation(weatherRepo)

    @Provides
    internal fun provideGetSubscribedSource(newsRepository: NewsRepository) : GetSubscribedSources = GetSubscribedSources(newsRepository)

    @Provides
    internal fun provideGetArticles(newsRepository: NewsRepository, configurationsRepository: ConfigurationsRepository): GetArticles = GetArticles(newsRepository, configurationsRepository)

    @Provides
    internal fun provideDefineDefaultSubscribedSources(newsRepository: NewsRepository) : DefineDefaultSubscribedSources = DefineDefaultSubscribedSources(newsRepository)

    @Provides
    internal fun provideAddLiveToFavorite(newsRepository: NewsRepository): AddLiveToFavorite = AddLiveToFavorite(newsRepository)

    @Provides
    internal fun provideRemoveLiveFromFavorite(newsRepository: NewsRepository): RemoveLiveToFavorite = RemoveLiveToFavorite(newsRepository)

    @Provides
    internal fun provideGetArticle(newsRepository: NewsRepository): GetArticle = GetArticle(newsRepository)
}