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

import com.dbeginc.dbweathernews.viewmodels.toViewModel
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.usecases.news.GetArticle
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailPresenter
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailView
import com.dbeginc.dbweathernews.viewmodels.ArticleModel
import io.reactivex.disposables.Disposable
import org.threeten.bp.ZonedDateTime

/**
 * Created by darel on 17.11.17.
 *
 * Article Detail Presenter Implementation
 */
class ArticleDetailPresenterImpl(private val getArticle: GetArticle) : ArticleDetailPresenter {
    private var view: ArticleDetailView? = null
    private var subscription: Disposable? = null

    override fun bind(view: ArticleDetailView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscription?.dispose()
        this.view = null
    }

    override fun loadArticle() {
        subscription = getArticle.execute(ArticleRequest(view!!.getSourceName(), view!!.getArticleUrl(), Unit))
                .map { article -> article.toViewModel(view!!.getDefaultAuthorName(), ZonedDateTime.now().toInstant()) }
                .subscribe(this::onValue, this::onError)
    }

    override fun onShareAction() {
        view?.shareArticle()
    }

    override fun onAction() {
        view?.openFullArticle()
    }

    override fun onExitAction() {
        view?.close()
    }

    override fun onValue(newValue: ArticleModel) {
        view?.displayArticle(newValue)
    }

    override fun onError(error: Throwable) {
        Logger.error(ArticleDetailPresenterImpl::class.java.canonicalName, error.localizedMessage, error)
        view?.showError(error.localizedMessage)
    }
}