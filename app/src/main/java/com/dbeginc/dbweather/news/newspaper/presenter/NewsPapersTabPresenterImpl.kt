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

package com.dbeginc.dbweather.news.newspaper.presenter

import com.crashlytics.android.Crashlytics
import com.dbeginc.dbweather.news.newspaper.NewsPapersTabContract
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweather.viewmodels.news.NewsPaperModel
import com.dbeginc.dbweather.viewmodels.news.toViewModel
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsRequest
import com.dbeginc.dbweatherdomain.usecases.news.GetArticles
import com.dbeginc.dbweatherdomain.usecases.news.GetSubscribedSources
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.ZonedDateTime

/**
 * Created by darel on 06.10.17.
 *
 * Articles Tab Presenter Implementation
 */
class NewsPapersTabPresenterImpl(private val getArticles: GetArticles, private val getSubscribedSources: GetSubscribedSources) : NewsPapersTabContract.NewsPapersTabPresenter {
    private lateinit var view: NewsPapersTabContract.NewsPapersTabView
    private val subscriptions = CompositeDisposable()

    override fun bind(view: NewsPapersTabContract.NewsPapersTabView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        subscriptions.clear()
        getArticles.clean()
        getSubscribedSources.clean()
    }

    override fun loadArticles() {
        getSubscribedSources.execute(Unit)
                .doOnSubscribe { view.showUpdateStatus() }
                .doAfterTerminate { view.hideUpdateStatus() }
                .flatMap { sources -> getArticles.execute(NewsRequest(sources, Unit)) }
                .map { articles ->
                    val currentTime = ZonedDateTime.now().toInstant()

                    articles.groupBy { article -> article.sourceId }.entries
                        .map { entry ->  NewsPaperModel(name=entry.key, children=entry.value.map{ article -> article.toViewModel(view.getDefaultAuthorName(), currentTime) }) }

                }.subscribe(
                        { newsPapers -> view.displayNewsPapers(newsPapers) },
                        { error -> reportError(error) }
                ).addTo(subscriptions)
    }

    private fun reportError(error: Throwable) {
        Crashlytics.logException(error)
        view.showError(error.localizedMessage)
    }
}