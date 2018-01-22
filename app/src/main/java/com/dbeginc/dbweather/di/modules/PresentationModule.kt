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

import com.dbeginc.dbweather.config.ConfigurationTabContract
import com.dbeginc.dbweather.config.managelocations.ManageLocationsContract
import com.dbeginc.dbweather.config.managelocations.presenter.ManageLocationsPresenterImpl
import com.dbeginc.dbweather.config.presenter.ConfigurationTabPresenterImpl
import com.dbeginc.dbweather.intro.IntroContract
import com.dbeginc.dbweather.intro.chooselocation.ChooseLocationContract
import com.dbeginc.dbweather.intro.chooselocation.presenter.ChooseLocationPresenterImpl
import com.dbeginc.dbweather.intro.gpslocationfinder.GpsLocationFinderContract
import com.dbeginc.dbweather.intro.gpslocationfinder.presenter.GpsLocationFinderPresenterImpl
import com.dbeginc.dbweather.intro.presenter.IntroPresenterImpl
import com.dbeginc.dbweather.splash.SplashContract
import com.dbeginc.dbweather.splash.presenter.SplashPresenterImpl
import com.dbeginc.dbweather.weather.WeatherTabContract
import com.dbeginc.dbweather.weather.presenter.WeatherTabPresenterImpl
import com.dbeginc.dbweathercommon.ThreadProvider
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeWeatherNotificationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetWeatherNotificationStatus
import com.dbeginc.dbweatherdomain.usecases.news.*
import com.dbeginc.dbweatherdomain.usecases.weather.*
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailPresenter
import com.dbeginc.dbweathernews.articledetail.presenter.ArticleDetailPresenterImpl
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesPresenter
import com.dbeginc.dbweathernews.favoritelives.presenter.FavoriteLivesPresenterImpl
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailPresenter
import com.dbeginc.dbweathernews.livedetail.presenter.LiveDetailPresenterImpl
import com.dbeginc.dbweathernews.lives.contract.LivesPresenter
import com.dbeginc.dbweathernews.lives.presenter.LivesPresenterImpl
import com.dbeginc.dbweathernews.newspapers.contract.NewsPapersPresenter
import com.dbeginc.dbweathernews.newspapers.presenter.NewsPapersPresenterImpl
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailPresenter
import com.dbeginc.dbweathernews.sourcedetail.presenter.SourceDetailPresenterImpl
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerPresenter
import com.dbeginc.dbweathernews.sourcesmanager.presenter.SourcesManagerPresenterImpl
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by darel on 18.09.17.
 *
 * Application Presentation Module
 */
@Module
class PresentationModule {
    @Provides
    fun provideSourceDetailPresenter(subscribeToSource: SubscribeToSource, unSubscribeToSource: UnSubscribeToSource, getSource: GetSource) : SourceDetailPresenter = SourceDetailPresenterImpl(subscribeToSource, unSubscribeToSource, getSource)

    @Provides
    fun provideArticleDetailPresenter(getArticle: GetArticle): ArticleDetailPresenter = ArticleDetailPresenterImpl(getArticle)

    @Provides
    fun provideManageSourcesPresenter(getAllSources: GetAllSources): SourcesManagerPresenter = SourcesManagerPresenterImpl(getAllSources, ThreadProvider)

    @Provides
    fun provideManageLocationsPresenter(getAllUserList: GetAllUserLocations, removeLocation: RemoveLocation) : ManageLocationsContract.ManageLocationsPresenter = ManageLocationsPresenterImpl(getAllUserList, removeLocation, ThreadProvider)

    @Provides
    fun provideConfigurationTabPresenter(changeNewsPaperTranslationStatus: ChangeNewsPaperTranslationStatus,
                                         changeWeatherNotificationStatus: ChangeWeatherNotificationStatus,
                                         getNewsPaperTranslationStatus: GetNewsPaperTranslationStatus,
                                         getWeatherNotificationStatus: GetWeatherNotificationStatus) : ConfigurationTabContract.ConfigurationTabPresenter {

        return ConfigurationTabPresenterImpl(getWeatherNotificationStatus, getNewsPaperTranslationStatus, changeWeatherNotificationStatus, changeNewsPaperTranslationStatus)
    }

    @Provides
    fun provideLiveDetailPresenter(getLive: GetLive, removeLiveToFavorite: RemoveLiveToFavorite, addLiveToFavorite: AddLiveToFavorite, getFavoriteLives: GetFavoriteLives): LiveDetailPresenter =
            LiveDetailPresenterImpl(getLive, addLiveToFavorite, removeLiveToFavorite, getFavoriteLives, ThreadProvider)

    @Provides
    fun provideAllLivesTabPresenter(getAllLives: GetAllLives, getFavoriteLives: GetFavoriteLives): LivesPresenter = LivesPresenterImpl(getAllLives, getFavoriteLives, ThreadProvider)

    @Provides
    fun provideFavoriteLivesTabPresenter(getFavoriteLives: GetFavoriteLives, getLives: GetLives) : FavoriteLivesPresenter =
            FavoriteLivesPresenterImpl(getFavoriteLives, getLives, ThreadProvider)

    @Provides
    fun provideWeatherTabPresenter(getLocations: GetLocations, getWeather: GetWeather, getAllUserList: GetAllUserLocations, locationSearchEvent: BehaviorSubject<List<Location>>, getWeatherByLocation: GetWeatherByLocation) : WeatherTabContract.WeatherTabPresenter =
            WeatherTabPresenterImpl(getLocations, getWeather, getWeatherByLocation, getAllUserList, locationSearchEvent, ThreadProvider)

    @Provides
    fun provideArticlesTabPresenter(getArticles: GetArticles, getSubscribedSources: GetSubscribedSources) : NewsPapersPresenter = NewsPapersPresenterImpl(getArticles, getSubscribedSources, ThreadProvider)

    @Provides
    fun provideIntroPresenter() : IntroContract.IntroPresenter = IntroPresenterImpl()

    @Provides
    fun provideSplashPresenter(defineDefaultSubscribedSources: DefineDefaultSubscribedSources) : SplashContract.SplashPresenter = SplashPresenterImpl(defineDefaultSubscribedSources)

    @Provides
    fun provideChooseLocationPresenter(getLocations: GetLocations) : ChooseLocationContract.ChooseLocationPresenter = ChooseLocationPresenterImpl(getLocations)

    @Provides
    fun provideGpsLocationFinderPresenter() : GpsLocationFinderContract.GpsLocationFinderPresenter = GpsLocationFinderPresenterImpl()
}