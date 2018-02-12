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

package com.dbeginc.dbweathernews.newspapers

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.annotation.VisibleForTesting
import com.dbeginc.dbweathercommon.utils.LogDispatcher
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.newspapers.contract.NewsPapersPresenter
import com.dbeginc.dbweathernews.newspapers.contract.NewsPapersView
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.threeten.bp.ZonedDateTime
import javax.inject.Inject

/**
 * Created by darel on 10.02.18.
 *
 * News Papers ViewModel
 *
 * This view model also act as a bridge between
 * View -> ViewModel -> Presenter
 *
 * The view model process any long related task that the view want to do
 * presenter take care of validation or processing of synchronous task
 *
 * the view model don't have reference to view neither the presenter
 * view model use observable data example (liveData or BehaviorSubject)
 * presenter don't save the view instance so we dont have to deal with leaks
 * presenter only receive the view as method parameter (method scope variable), execute his task and then return void or result
 */
class NewsPapersViewModel @Inject constructor(private val model: NewsRepository) : ViewModel() {
    private val subscriptions = CompositeDisposable()
    // RX Stream that notify request stream if user want to retry
    private val retryRequest = PublishSubject.create<Unit>()
    // Rx Behavior relay that subscribe to request and notify view of new data
    private val newsPapersResponseListener = BehaviorRelay.create<List<NewsPaperModel>>()
    // Observable data
    private val _newsPapersModels: MutableLiveData<List<NewsPaperModel>> = MutableLiveData()
    // View Presenter
    val presenter: NewsPapersViewPresenter = NewsPapersViewPresenter()

    init {
        // We subscribe to the relay on creation
        newsPapersResponseListener.subscribe(_newsPapersModels::postValue).addTo(subscriptions)
    }

    // public getter for observable data, it's as LiveData not Mutable because we only want
    // the view model to post new data
    fun getNewsPapers(): LiveData<List<NewsPaperModel>> = _newsPapersModels


    /**
     * Load newspapers articles
     *
     * we don't pass the view to the method but a proxy object (behavior subject)
     *
     * @param requestStateListener that notify the view of current state of the request
     * @param defaultAuthorName article default author if no specify
     */
    fun loadArticles(requestStateListener: BehaviorSubject<RequestState>, defaultAuthorName: String) {
        model.getSubscribedSources()
                .doOnSubscribe { requestStateListener.onNext(RequestState.LOADING) }
                .doAfterNext { requestStateListener.onNext(RequestState.COMPLETED) }
                .doOnError {
                    requestStateListener.onNext(RequestState.ERROR)
                    LogDispatcher.logError(it)
                }
                .observeOn(ThreadProvider.computation)
                .flatMap { sources -> model.getArticles(NewsRequest(sources, Unit)) }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .map { articles -> convertArticlesToUiModel(articles, defaultAuthorName) }
                .map { newsPapers -> newsPapers.sortedBy { (name) -> name } }
                .observeOn(ThreadProvider.ui)
                .subscribe(newsPapersResponseListener)
                .addTo(subscriptions)
    }

    override fun onCleared() = subscriptions.clear()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun convertArticlesToUiModel(articles: List<Article>, defaultAuthorName: String): List<NewsPaperModel> {
        val currentTime = ZonedDateTime.now().toInstant()

        return articles.groupBy { (sourceId) -> sourceId }
                .entries.map { entry -> NewsPaperModel(name = entry.key, children = entry.value.map { article -> article.toViewModel(defaultAuthorName, currentTime) }) }
    }

    inner class NewsPapersViewPresenter : NewsPapersPresenter {
        override fun bind(view: NewsPapersView) = view.setupView()

        override fun retryNewsRequest() = retryRequest.onNext(Unit)
    }

}