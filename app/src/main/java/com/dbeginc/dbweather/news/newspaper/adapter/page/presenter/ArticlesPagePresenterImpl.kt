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

package com.dbeginc.dbweather.news.newspaper.adapter.page.presenter

import com.dbeginc.dbweather.news.newspaper.adapter.page.ArticlesPageContract
import com.dbeginc.dbweather.viewmodels.news.ArticleModel

/**
 * Created by darel on 10.10.17.
 *
 * Articles Page Presenter Implementation
 */
class ArticlesPagePresenterImpl(private var data: List<ArticleModel>) : ArticlesPageContract.ArticlesPagePresenter {
    private var view: ArticlesPageContract.ArticlesPageView? = null

    override fun bind(view: ArticlesPageContract.ArticlesPageView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        view = null
    }

    override fun loadArticles() {
        view?.displayArticles(data)
    }

    override fun updateModel(articles: List<ArticleModel>) {
        data = articles
    }
}