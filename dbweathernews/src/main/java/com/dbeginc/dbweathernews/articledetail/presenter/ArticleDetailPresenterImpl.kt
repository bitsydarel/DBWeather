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

package com.dbeginc.dbweathernews.articledetail.presenter

import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailPresenter
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailView
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import io.reactivex.disposables.Disposable
import org.threeten.bp.ZonedDateTime

/**
 * Created by darel on 17.11.17.
 *
 * Article Detail Presenter Implementation
 */
class ArticleDetailPresenterImpl(private val model: NewsRepository) : ArticleDetailPresenter {
    private var subscription: Disposable? = null

    override fun bind(view: ArticleDetailView) = view.setupView()

    override fun unBind() {
        subscription?.dispose()
    }

    override fun loadArticle(view: ArticleDetailView) {
        model.getArticle(ArticleRequest(view.getSourceName(), view.getArticleUrl(), Unit))
                .map { article -> article.toViewModel(view.getDefaultAuthorName(), ZonedDateTime.now().toInstant()) }
                .subscribe(view::displayArticle, view::onError)
                .also { subscription = it }
    }

    override fun onShareAction(view: ArticleDetailView) = view.shareArticle()

    override fun onAction(view: ArticleDetailView) = view.openFullArticle()

    override fun onExitAction(view: ArticleDetailView) = view.close()
}