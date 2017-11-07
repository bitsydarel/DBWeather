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
    internal fun provideRemoveLocationUseCase(weatherRepo: WeatherRepository): RemoveLocation {
        return RemoveLocation(weatherRepo)
    }

    @Provides
    internal fun provideUnSubscribeToSourceUseCase(newsRepository: NewsRepository): UnSubscribeToSource {
        return UnSubscribeToSource(newsRepository)
    }

    @Provides
    internal fun provideSubscribeToSourceUseCase(newsRepository: NewsRepository): SubscribeToSource {
        return SubscribeToSource(newsRepository)
    }

    @Provides
    internal fun provideGetAllSources(newsRepository: NewsRepository): GetAllSources {
        return GetAllSources(newsRepository)
    }

    @Provides
    internal fun provideGetWeatherNotificationStatus(configurationsRepository: ConfigurationsRepository): GetWeatherNotificationStatus {
        return GetWeatherNotificationStatus(configurationsRepository)
    }

    @Provides
    internal fun provideGetNewsPaperTranslationStatus(configurationsRepository: ConfigurationsRepository): GetNewsPaperTranslationStatus {
        return GetNewsPaperTranslationStatus(configurationsRepository)
    }

    @Provides
    internal fun provideChangeWeatherNotification(configurationsRepository: ConfigurationsRepository): ChangeWeatherNotificationStatus {
        return ChangeWeatherNotificationStatus(configurationsRepository)
    }

    @Provides
    internal fun provideChangeNewsPaperTranslation(configurationsRepository: ConfigurationsRepository): ChangeNewsPaperTranslationStatus {
        return ChangeNewsPaperTranslationStatus(configurationsRepository)
    }

    @Provides
    internal fun provideGetLive(newsRepository: NewsRepository): GetLive {
        return GetLive(newsRepository)
    }

    @Provides
    internal fun provideGetLives(newsRepository: NewsRepository): GetLives {
        return GetLives(newsRepository)
    }

    @Provides
    internal fun provideGetAllLives(newsRepository: NewsRepository) : GetAllLives {
        return GetAllLives(newsRepository)
    }

    @Provides
    internal fun provideGetFavoriteLives(newsRepository: NewsRepository) : GetFavoriteLives {
        return GetFavoriteLives(newsRepository)
    }

    @Provides
    internal fun provideGetLocationsUseCase(weatherRepo: WeatherRepository) : GetLocations {
        return GetLocations(weatherRepo)
    }

    @Provides
    internal fun provideGetWeatherUseCase(weatherRepo: WeatherRepository) : GetWeather {
        return GetWeather(weatherRepo)
    }

    @Provides
    internal fun provideGetAllUserListUseCase(weatherRepo: WeatherRepository) : GetAllUserLocations {
        return GetAllUserLocations(weatherRepo)
    }

    @Provides
    internal fun provideGetWeatherByLocationUseCase(weatherRepo: WeatherRepository) : GetWeatherByLocation {
        return GetWeatherByLocation(weatherRepo)
    }

    @Provides
    internal fun provideGetSubscribedSource(newsRepository: NewsRepository) : GetSubscribedSources {
        return GetSubscribedSources(newsRepository)
    }

    @Provides
    internal fun provideGetArticles(newsRepository: NewsRepository, configurationsRepository: ConfigurationsRepository): GetArticles {
        return GetArticles(newsRepository, configurationsRepository)
    }

    @Provides
    internal fun provideDefineDefaultSubscribedSources(newsRepository: NewsRepository) : DefineDefaultSubscribedSources {
        return DefineDefaultSubscribedSources(newsRepository)
    }

    @Provides
    internal fun provideAddLiveToFavorite(newsRepository: NewsRepository): AddLiveToFavorite {
        return AddLiveToFavorite(newsRepository)
    }

    @Provides
    internal fun provideRemoveLiveFromFavorite(newsRepository: NewsRepository): RemoveLiveToFavorite {
        return RemoveLiveToFavorite(newsRepository)
    }
}