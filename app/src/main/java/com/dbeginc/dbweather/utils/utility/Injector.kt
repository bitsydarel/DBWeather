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

package com.dbeginc.dbweather.utils.utility

import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.config.managelocations.view.ManageLocationsActivity
import com.dbeginc.dbweather.config.managesources.view.ManageSourcesActivity
import com.dbeginc.dbweather.config.view.ConfigurationTabFragment
import com.dbeginc.dbweather.di.components.DBWeatherApplicationComponent
import com.dbeginc.dbweather.intro.chooselocation.view.ChooseLocationFragment
import com.dbeginc.dbweather.intro.gpslocationfinder.view.GpsLocationFinderFragment
import com.dbeginc.dbweather.intro.view.IntroActivity
import com.dbeginc.dbweather.news.lives.LivesTabFragment
import com.dbeginc.dbweather.news.lives.livedetail.view.LiveDetailActivity
import com.dbeginc.dbweather.news.lives.page.alllives.view.AllLivesTabPageFragment
import com.dbeginc.dbweather.news.lives.page.favorite.view.FavoriteLivesTabFragment
import com.dbeginc.dbweather.news.newspaper.articledetail.view.ArticleDetailActivity
import com.dbeginc.dbweather.news.newspaper.view.NewsPaperTabFragment
import com.dbeginc.dbweather.news.view.NewsTabFragment
import com.dbeginc.dbweather.splash.view.SplashActivity
import com.dbeginc.dbweather.utils.contentprovider.LocationSuggestionProvider
import com.dbeginc.dbweather.weather.WeatherTabFragment

/**
 * Created by darel on 27.09.17.
 *
 * Dependency Injector
 */
object Injector {
    lateinit var appComponent: DBWeatherApplicationComponent

    fun injectLocationProviderDep(provider: LocationSuggestionProvider) = appComponent.inject(provider)

    fun injectWeatherTabDep(weatherTabFragment: WeatherTabFragment) = appComponent.inject(weatherTabFragment)

    fun injectIntroDep(introActivity: IntroActivity) = appComponent.inject(introActivity)

    fun injectChooseLocation(chooseLocationFragment: ChooseLocationFragment) = appComponent.inject(chooseLocationFragment)

    fun injectSplashDep(splashActivity: SplashActivity) = appComponent.inject(splashActivity)

    fun injectGpsLocationFinder(gpsLocationFinderFragment: GpsLocationFinderFragment) = appComponent.inject(gpsLocationFinderFragment)

    fun injectBaseFragmentDep(baseFragment: BaseFragment) = appComponent.inject(baseFragment)

    fun injectArticlesTabDep(articlesTabFragment: NewsPaperTabFragment) = appComponent.inject(articlesTabFragment)

    fun injectNewsTabDep(newsTabFragment: NewsTabFragment) = appComponent.inject(newsTabFragment)

    fun injectLivesPageDep(livesPageFragment: AllLivesTabPageFragment) = appComponent.inject(livesPageFragment)

    fun injectLivesTabDep(livesTabFragment: LivesTabFragment) = appComponent.inject(livesTabFragment)

    fun injectFavoriteLivesPageDep(favoriteLivesTabFragment: FavoriteLivesTabFragment) = appComponent.inject(favoriteLivesTabFragment)

    fun injectLiveDetailDep(liveDetailActivity: LiveDetailActivity) = appComponent.inject(liveDetailActivity)

    fun injectConfigurationDep(configurationTabFragment: ConfigurationTabFragment) = appComponent.inject(configurationTabFragment)

    fun injectManageLocationsDep(manageLocationsActivity: ManageLocationsActivity) = appComponent.inject(manageLocationsActivity)

    fun injectManageSourcesDep(manageSourcesActivity: ManageSourcesActivity) = appComponent.inject(manageSourcesActivity)

    fun injectArticleDetailDep(articleDetailActivity: ArticleDetailActivity) = appComponent.inject(articleDetailActivity)
}