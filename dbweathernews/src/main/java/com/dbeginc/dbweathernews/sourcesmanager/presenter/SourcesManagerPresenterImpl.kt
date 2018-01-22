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

package com.dbeginc.dbweathernews.sourcesmanager.presenter

import com.dbeginc.dbweathercommon.ThreadProvider
import com.dbeginc.dbweathercommon.addTo
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.usecases.news.GetAllSources
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerPresenter
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerView
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 18.11.17.
 *
 * Sources Manager Presenter Implementation
 */
class SourcesManagerPresenterImpl(private val getAllSources: GetAllSources, private val threads: ThreadProvider) : SourcesManagerPresenter {
    private var view: SourcesManagerView? = null
    private val subscriptions = CompositeDisposable()

    override fun bind(view: SourcesManagerView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        getAllSources.clean()
        view = null
    }

    override fun loadSources() {
        getAllSources.execute(Unit)
                .doOnSubscribe { view?.showLoading() }
                .doAfterTerminate{ view?.hideLoading() }
                .observeOn(threads.computation)
                .map { sources -> sources.map { source -> source.toViewModel() } }
                .observeOn(threads.ui)
                .subscribe(this::onValue, this::onError)
                .addTo(subscriptions)
    }

    override fun findSource(query: String) {
        getAllSources.execute(Unit)
                .doOnSubscribe { view?.showLoading() }
                .doAfterTerminate { view?.hideLoading() }
                .observeOn(threads.computation)
                .flatMapIterable { sources -> sources }
                .filter { source -> source.name.toLowerCase().contains(query.toLowerCase())  }
                .map { source -> source.toViewModel() }
                .collectInto(mutableListOf<SourceModel>(), { list, source -> list.add(source) } )
                .observeOn(threads.ui)
                .subscribe(this::onValue, this::onError)
                .addTo(subscriptions)
    }

    override fun onValue(newValue: List<SourceModel>) {
        view?.displaySources(newValue)
    }

    override fun onError(error: Throwable) {
        Logger.error(SourcesManagerPresenterImpl::class.java.simpleName, error.localizedMessage, error)
        view?.showError(error.localizedMessage)
    }

    override fun onExitAction() {
        view?.close()
    }
}