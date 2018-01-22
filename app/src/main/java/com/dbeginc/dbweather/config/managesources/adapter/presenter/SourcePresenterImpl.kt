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

import com.dbeginc.dbweather.config.managesources.adapter.contract.SourcePresenter
import com.dbeginc.dbweather.config.managesources.adapter.contract.SourceView
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.entities.requests.news.SourceRequest
import com.dbeginc.dbweatherdomain.usecases.news.SubscribeToSource
import com.dbeginc.dbweatherdomain.usecases.news.UnSubscribeToSource
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import com.dbeginc.dbweathernews.viewmodels.toDomain
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 27.10.17.
 *
 * Source Presenter Implementation
 */
class SourcePresenterImpl(private val source: SourceModel,
                          private val subscribeToSource: SubscribeToSource,
                          private val unSubscribeToSource: UnSubscribeToSource): SourcePresenter {

    private var view: SourceView? = null
    private val subscriptions = CompositeDisposable()

    override fun bind(view: SourceView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscribeToSource.clean()
        unSubscribeToSource.clean()
        subscriptions.clear()
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
                    .doOnSubscribe { view?.showLoading() }
                    .doOnTerminate { view?.hideLoading() }
                    .subscribe({ view?.showUnSubscribed() }, this::onError)
                    .addTo(subscriptions)

        } else {
            subscribeToSource.execute(SourceRequest(source.id, source.apply { subscribed = true }.toDomain()))
                    .doOnSubscribe { view?.showLoading() }
                    .doOnTerminate { view?.hideLoading() }
                    .subscribe({ view?.showSubscribed() }, this::onError)
                    .addTo(subscriptions)
        }
    }

    override fun getData(): SourceModel = source

    private fun onError(error: Throwable) {
        Logger.error(SourcePresenterImpl::class.java.simpleName, error.localizedMessage, error)
        view?.showError(error.localizedMessage)
    }
}