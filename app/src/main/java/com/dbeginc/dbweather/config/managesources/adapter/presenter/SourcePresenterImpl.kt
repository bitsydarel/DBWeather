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

package com.dbeginc.dbweather.config.managesources.adapter.presenter

import com.dbeginc.dbweather.config.managesources.adapter.SourceContract
import com.dbeginc.dbweather.viewmodels.news.SourceModel
import com.dbeginc.dbweather.viewmodels.news.toDomain
import com.dbeginc.dbweatherdomain.entities.requests.news.SourceRequest
import com.dbeginc.dbweatherdomain.usecases.news.SubscribeToSource
import com.dbeginc.dbweatherdomain.usecases.news.UnSubscribeToSource

/**
 * Created by darel on 27.10.17.
 *
 * Source Presenter Implementation
 */
class SourcePresenterImpl(private val source: SourceModel,
                          private val subscribeToSource: SubscribeToSource,
                          private val unSubscribeToSource: UnSubscribeToSource) : SourceContract.SourcePresenter {

    private var view: SourceContract.SourceView? = null

    override fun bind(view: SourceContract.SourceView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscribeToSource.clean()
        unSubscribeToSource.clean()
        view = null
    }

    override fun loadSource() {
        view?.displaySource(source)
    }

    override fun onAction() {
        view?.goToSourceDetail()
    }

    override fun onSubscribe() {
        if (source.subscribed) {
            unSubscribeToSource.execute(SourceRequest(source.id, source.apply { subscribed = false }.toDomain()))
                    .doOnSubscribe { view?.showUpdateStatus() }
                    .doOnTerminate { view?.hideUpdateStatus() }
                    .subscribe(
                            { view?.showUnSubscribed() },
                            { error -> view?.showError(error.localizedMessage) }
                    )

        } else {
            subscribeToSource.execute(SourceRequest(source.id, source.apply { subscribed = true }.toDomain()))
                    .doOnSubscribe { view?.showUpdateStatus() }
                    .doOnTerminate { view?.hideUpdateStatus() }
                    .subscribe(
                            { view?.showSubscribed() },
                            { error -> view?.showError(error.localizedMessage) }
                    )
        }
    }

    override fun getData(): SourceModel = source

}