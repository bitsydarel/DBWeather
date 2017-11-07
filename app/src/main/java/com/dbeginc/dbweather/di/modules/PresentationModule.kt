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
import com.dbeginc.dbweather.config.managesources.ManageSourcesContract
import com.dbeginc.dbweather.config.managesources.presenter.ManageSourcesPresenterImpl
import com.dbeginc.dbweather.config.presenter.ConfigurationTabPresenterImpl
import com.dbeginc.dbweather.intro.IntroContract
import com.dbeginc.dbweather.intro.chooselocation.ChooseLocationContract
import com.dbeginc.dbweather.intro.chooselocation.presenter.ChooseLocationPresenterImpl
import com.dbeginc.dbweather.intro.gpslocationfinder.GpsLocationFinderContract
import com.dbeginc.dbweather.intro.gpslocationfinder.presenter.GpsLocationFinderPresenterImpl
import com.dbeginc.dbweather.intro.presenter.IntroPresenterImpl
import com.dbeginc.dbweather.news.NewsTabContract
import com.dbeginc.dbweather.news.lives.livedetail.LiveDetailContract
import com.dbeginc.dbweather.news.lives.livedetail.presenter.LiveDetailPresenterImpl
import com.dbeginc.dbweather.news.lives.page.alllives.AllLivesTabContract
import com.dbeginc.dbweather.news.lives.page.alllives.presenter.AllLivesTabPresenterImpl
import com.dbeginc.dbweather.news.lives.page.favorite.FavoriteLivesTabContract
import com.dbeginc.dbweather.news.lives.page.favorite.presenter.FavoriteLivesTabPresenterImpl
import com.dbeginc.dbweather.news.newspaper.NewsPapersTabContract
import com.dbeginc.dbweather.news.newspaper.articledetail.ArticleDetailContract
import com.dbeginc.dbweather.news.newspaper.articledetail.presenter.ArticleDetailPresenterImpl
import com.dbeginc.dbweather.news.newspaper.presenter.NewsPapersTabPresenterImpl
import com.dbeginc.dbweather.news.presenter.NewsTabPresenterImpl
import com.dbeginc.dbweather.splash.SplashContract
import com.dbeginc.dbweather.splash.presenter.SplashPresenterImpl
import com.dbeginc.dbweather.weather.WeatherTabContract
import com.dbeginc.dbweather.weather.WeatherTabPresenterImpl
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeWeatherNotificationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetWeatherNotificationStatus
import com.dbeginc.dbweatherdomain.usecases.news.*
import com.dbeginc.dbweatherdomain.usecases.weather.*
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
    fun provideArticleDetailPresenter(getArticle: GetArticle): ArticleDetailContract.ArticleDetailPresenter {
        return ArticleDetailPresenterImpl(getArticle)
    }

    @Provides
    fun provideManageSourcesPresenter(getAllSources: GetAllSources): ManageSourcesContract.ManageSourcesPresenter {
        return ManageSourcesPresenterImpl(getAllSources)
    }

    @Provides
    fun provideManageLocationsPresenter(getAllUserList: GetAllUserLocations, removeLocation: RemoveLocation) : ManageLocationsContract.ManageLocationsPresenter {
        return ManageLocationsPresenterImpl(getAllUserList, removeLocation)
    }

    @Provides
    fun provideConfigurationTabPresenter(changeNewsPaperTranslationStatus: ChangeNewsPaperTranslationStatus,
                                         changeWeatherNotificationStatus: ChangeWeatherNotificationStatus,
                                         getNewsPaperTranslationStatus: GetNewsPaperTranslationStatus,
                                         getWeatherNotificationStatus: GetWeatherNotificationStatus) : ConfigurationTabContract.ConfigurationTabPresenter {

        return ConfigurationTabPresenterImpl(getWeatherNotificationStatus, getNewsPaperTranslationStatus, changeWeatherNotificationStatus, changeNewsPaperTranslationStatus)
    }

    @Provides
    fun provideLiveDetailPresenter(getLive: GetLive, removeLiveToFavorite: RemoveLiveToFavorite, addLiveToFavorite: AddLiveToFavorite, getFavoriteLives: GetFavoriteLives): LiveDetailContract.LiveDetailPresenter {
        return LiveDetailPresenterImpl(getLive, addLiveToFavorite, removeLiveToFavorite, getFavoriteLives)
    }

    @Provides
    fun provideAllLivesTabPresenter(getAllLives: GetAllLives, getFavoriteLives: GetFavoriteLives): AllLivesTabContract.AllLivesTabPresenter {
        return AllLivesTabPresenterImpl(getAllLives, getFavoriteLives)
    }

    @Provides
    fun provideFavoriteLivesTabPresenter(getFavoriteLives: GetFavoriteLives, getLives: GetLives) : FavoriteLivesTabContract.FavoriteLivesTabPresenter {
        return FavoriteLivesTabPresenterImpl(getFavoriteLives, getLives)
    }

    @Provides
    fun provideWeatherTabPresenter(getLocations: GetLocations, getWeather: GetWeather, getAllUserList: GetAllUserLocations, locationSearchEvent: BehaviorSubject<List<Location>>, getWeatherByLocation: GetWeatherByLocation) : WeatherTabContract.WeatherTabPresenter {
        return WeatherTabPresenterImpl(getLocations, getWeather, getWeatherByLocation, getAllUserList, locationSearchEvent)
    }

    @Provides
    fun provideNewsTabPresenter() : NewsTabContract.NewsTabPresenter = NewsTabPresenterImpl()

    @Provides
    fun provideArticlesTabPresenter(getArticles: GetArticles, getSubscribedSources: GetSubscribedSources) : NewsPapersTabContract.NewsPapersTabPresenter = NewsPapersTabPresenterImpl(getArticles, getSubscribedSources)

    @Provides
    fun provideIntroPresenter() : IntroContract.IntroPresenter = IntroPresenterImpl()

    @Provides
    fun provideSplashPresenter(defineDefaultSubscribedSources: DefineDefaultSubscribedSources) : SplashContract.SplashPresenter = SplashPresenterImpl(defineDefaultSubscribedSources)

    @Provides
    fun provideChooseLocationPresenter(getLocations: GetLocations) : ChooseLocationContract.ChooseLocationPresenter = ChooseLocationPresenterImpl(getLocations)

    @Provides
    fun provideGpsLocationFinderPresenter() : GpsLocationFinderContract.GpsLocationFinderPresenter = GpsLocationFinderPresenterImpl()
}