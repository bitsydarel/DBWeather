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

package com.dbeginc.dbweathernews.newspapers.presenter

import com.dbeginc.dbweathernews.viewmodels.toViewModel
import com.dbeginc.dbweathercommon.ThreadProvider
import com.dbeginc.dbweathercommon.addTo
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsRequest
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.usecases.news.GetArticles
import com.dbeginc.dbweatherdomain.usecases.news.GetSubscribedSources
import com.dbeginc.dbweathernews.newspapers.contract.NewsPapersPresenter
import com.dbeginc.dbweathernews.newspapers.contract.NewsPapersView
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.ZonedDateTime

/**
 * Created by darel on 17.11.17.
 *
 * News Papers Presenter Implementation
 */
class NewsPapersPresenterImpl(private val getArticles: GetArticles, private val getSubscribedSources: GetSubscribedSources, private val threads: ThreadProvider) : NewsPapersPresenter {
    private var view: NewsPapersView? = null
    private val subscriptions = CompositeDisposable()

    override fun bind(view: NewsPapersView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscriptions.clear()
        getArticles.clean()
        getSubscribedSources.clean()
        view = null
    }

    override fun loadArticles() {
        getSubscribedSources.execute(Unit)
                .doOnSubscribe { view?.showLoading() }
                .doAfterTerminate { view?.hideLoading() }
                .flatMap { sources -> getArticles.execute(NewsRequest(sources, Unit)) }
                .observeOn(threads.computation)
                .map { articles ->
                    val currentTime = ZonedDateTime.now().toInstant()

                    articles.groupBy { article -> article.sourceId }.entries
                            .map { entry ->  NewsPaperModel(name=entry.key, children=entry.value.map{ article -> article.toViewModel(view!!.getDefaultAuthorName(), currentTime) }) }

                }
                .map { newsPapers -> newsPapers.sortedBy { newsPaper -> newsPaper.name } }
                .observeOn(threads.ui)
                .subscribe(this::onValue, this::onError)
                .addTo(subscriptions)
    }

    override fun onValue(newValue: List<NewsPaperModel>) {
        view?.displayNewsPapers(newValue)
    }

    override fun onError(error: Throwable) {
        Logger.error(NewsPapersPresenterImpl::class.java.canonicalName, error.localizedMessage, error)
        view?.showError(error.localizedMessage)
    }
}