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

package com.dbeginc.dbweathernews.sourcesmanager

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dbeginc.dbweathercommon.utils.LogDispatcher
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.entities.news.Source
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerPresenter
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerView
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by darel on 12.02.18.
 *
 * Sources Manager ViewModel
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
class SourcesManagerViewModel @Inject constructor(private val model: NewsRepository) : ViewModel() {
    private val subscriptions = CompositeDisposable()
    private val retryRequest = PublishSubject.create<Unit>()
    private val sourcesResponseListener = BehaviorRelay.create<List<SourceModel>>()
    private val _sourcesModels: MutableLiveData<List<SourceModel>> = MutableLiveData()
    val presenter = SourcesManagerViewPresenter()

    init {

        sourcesResponseListener.subscribe(_sourcesModels::postValue).addTo(subscriptions)
    }

    // public getter for observable data, it's as LiveData not Mutable because we only want
    // the view model to post new data
    fun getSources(): LiveData<List<SourceModel>> = _sourcesModels

    fun loadSources(requestStateListener: BehaviorSubject<RequestState>) {
        model.getAllSources()
                .doOnSubscribe { requestStateListener.onNext(RequestState.LOADING) }
                .doAfterNext { requestStateListener.onNext(RequestState.COMPLETED) }
                .doOnError {
                    requestStateListener.onNext(RequestState.ERROR)
                    LogDispatcher.logError(it)
                }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .observeOn(ThreadProvider.computation)
                .map { sources -> sources.map { source -> source.toViewModel() } }
                .observeOn(ThreadProvider.ui)
                .subscribe(sourcesResponseListener)
                .addTo(subscriptions)
    }

    fun findSource(requestStateListener: BehaviorSubject<RequestState>, query: String) {
        model.getAllSources()
                .doOnSubscribe { requestStateListener.onNext(RequestState.LOADING) }
                .doAfterNext { requestStateListener.onNext(RequestState.COMPLETED) }
                .doOnError {
                    requestStateListener.onNext(RequestState.ERROR)
                    LogDispatcher.logError(it)
                }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .observeOn(ThreadProvider.computation)
                .map { sources -> sources.findSimilar(query) }
                .map { sources -> sources.map { it.toViewModel() } }
                .observeOn(ThreadProvider.ui)
                .subscribe(sourcesResponseListener)
                .addTo(subscriptions)
    }

    override fun onCleared() = subscriptions.clear()

    inner class SourcesManagerViewPresenter : SourcesManagerPresenter {
        override fun bind(view: SourcesManagerView) = view.setupView()

        override fun retrySourcesRequest() = retryRequest.onNext(Unit)

        override fun onExitAction(view: SourcesManagerView) = view.close()
    }

    private fun List<Source>.findSimilar(query: String): List<Source> {
        return filter { it -> it.name.toLowerCase().contains(query.toLowerCase()) }
    }

}