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

package com.dbeginc.dbweather.di.modules.main.news.presentations

import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesPresenter
import com.dbeginc.dbweathernews.favoritelives.presenter.FavoriteLivesPresenterImpl
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailPresenter
import com.dbeginc.dbweathernews.livedetail.presenter.LiveDetailPresenterImpl
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 02.02.18.
 *
 * Lives presentations dependencies
 */
@Module
class LivesPresentations {
    @Provides
    fun provideLiveDetailPresenter(model: NewsRepository): LiveDetailPresenter = LiveDetailPresenterImpl(model, ThreadProvider)

    @Provides
    fun provideFavoriteLivesTabPresenter(model: NewsRepository): FavoriteLivesPresenter = FavoriteLivesPresenterImpl(model, ThreadProvider)
}