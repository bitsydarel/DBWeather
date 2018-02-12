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

package com.dbeginc.dbweather.di.modules.main.news

import com.dbeginc.dbweather.di.modules.main.news.presentations.ArticlesPresentations
import com.dbeginc.dbweather.di.modules.main.news.presentations.LivesPresentations
import com.dbeginc.dbweather.news.NewsTabFragment
import com.dbeginc.dbweather.news.lives.page.alllives.AllLivesTabPageFragment
import com.dbeginc.dbweather.news.lives.page.favorite.FavoriteLivesTabFragment
import com.dbeginc.dbweather.news.newspaper.NewsPaperTabFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by darel on 02.02.18.
 *
 * Dagger News Module that provide news feature
 * dependencies
 */
@Module(includes = [
    LivesPresentations::class,
    ArticlesPresentations::class
])
abstract class NewsModule {
    @ContributesAndroidInjector
    abstract fun contributeNewsTabFragment(): NewsTabFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsNewsPaperFragment(): NewsPaperTabFragment

    @ContributesAndroidInjector
    abstract fun contributeAllLivesTabPageFragment(): AllLivesTabPageFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteLivesTabFragment(): FavoriteLivesTabFragment
}