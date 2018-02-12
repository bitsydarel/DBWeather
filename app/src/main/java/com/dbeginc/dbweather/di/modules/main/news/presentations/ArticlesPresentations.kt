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

import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailPresenter
import com.dbeginc.dbweathernews.articledetail.presenter.ArticleDetailPresenterImpl
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailPresenter
import com.dbeginc.dbweathernews.sourcedetail.presenter.SourceDetailPresenterImpl
import dagger.Module
import dagger.Provides

/**
 * Created by darel on 02.02.18.
 *
 * Articles Presentations dependencies
 */
@Module
class ArticlesPresentations {

    @Provides
    fun provideArticleDetailPresenter(model: NewsRepository): ArticleDetailPresenter = ArticleDetailPresenterImpl(model)

    @Provides
    fun provideSourceDetailPresenter(model: NewsRepository): SourceDetailPresenter = SourceDetailPresenterImpl(model)

}