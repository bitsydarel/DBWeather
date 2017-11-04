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

package com.dbeginc.dbweather.config.managesources.presenter

import com.dbeginc.dbweather.config.managesources.ManageSourcesContract
import com.dbeginc.dbweather.viewmodels.news.SourceModel
import com.dbeginc.dbweather.viewmodels.news.toViewModel
import com.dbeginc.dbweatherdomain.usecases.news.GetAllSources

/**
 * Created by darel on 28.10.17.
 *
 * Manage Sources Presenter Implementation
 */
class ManageSourcesPresenterImpl(private val getAllSources: GetAllSources) : ManageSourcesContract.ManageSourcesPresenter{
    private var view: ManageSourcesContract.ManageSourcesView? = null

    override fun bind(view: ManageSourcesContract.ManageSourcesView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        getAllSources.clean()
        view = null
    }

    override fun loadSources() {
        getAllSources.execute(Unit)
                .doOnSubscribe { view?.showUpdateStatus() }
                .doOnTerminate { view?.hideUpdateStatus() }
                .map { sources -> sources.map { source -> source.toViewModel() } }
                .subscribe(
                        { sources -> view?.displaySources(sources) },
                        { error -> view?.showError(error.localizedMessage) }
                )
    }

    override fun findSource(query: String) {
        getAllSources.execute(Unit)
                .doOnSubscribe { view?.showUpdateStatus() }
                .doOnTerminate { view?.hideUpdateStatus() }
                .flatMapIterable { sources -> sources }
                .filter { source -> source.name.toLowerCase().contains(query)  }
                .map { source -> source.toViewModel() }
                .collectInto(mutableListOf<SourceModel>(), { list, source -> list.add(source) } )
                .subscribe(
                        { sources -> view?.displaySources(sources) },
                        { error -> view?.showError(error.localizedMessage) }
                )
    }

    override fun goBack() {
        view?.goBackToConfigurations()
    }

}