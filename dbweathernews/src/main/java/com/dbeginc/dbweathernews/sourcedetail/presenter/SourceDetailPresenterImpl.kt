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

package com.dbeginc.dbweathernews.sourcedetail.presenter

import com.dbeginc.dbweathercommon.addTo
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.entities.requests.news.SourceRequest
import com.dbeginc.dbweatherdomain.usecases.news.GetSource
import com.dbeginc.dbweatherdomain.usecases.news.SubscribeToSource
import com.dbeginc.dbweatherdomain.usecases.news.UnSubscribeToSource
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailPresenter
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailView
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import com.dbeginc.dbweathernews.viewmodels.toDomain
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 18.11.17.
 *
 * Source Detail Presenter Implementation
 */
class SourceDetailPresenterImpl(private val subscribeToSource: SubscribeToSource,
                                private val unSubscribeToSource: UnSubscribeToSource,
                                private val getSource: GetSource) : SourceDetailPresenter {

    private var view: SourceDetailView? = null
    private val subscriptions = CompositeDisposable()

    override fun bind(view: SourceDetailView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscribeToSource.clean()
        unSubscribeToSource.clean()
        subscriptions.clear()
        view = null
    }

    override fun loadSourceDetail() {
        getSource.execute(SourceRequest(view?.getSource()!!.id, Unit))
                .doOnSubscribe { view?.showLoading() }
                .doAfterTerminate { view?.hideLoading() }
                .map { source -> source.toViewModel() }
                .subscribe(this::onValue, this::onError)
    }

    override fun onValue(newValue: SourceModel) {
        view?.displaySourceDetail(newValue)
    }

    override fun onError(error: Throwable) {
        Logger.error(SourceDetailPresenterImpl::class.java.simpleName, error.localizedMessage, error)
        view?.showError(error.localizedMessage)
    }

    override fun onSubscribeAction() {
        if (view?.getSource()?.subscribed!!) {
            unSubscribeToSource.execute(SourceRequest(view!!.getSource().id, view!!.getSource().apply {  subscribed = false}.toDomain()))
                    .doOnSubscribe {
                        view?.showUnSubscribedToSource()
                        view?.showLoading()
                    }.doAfterTerminate { view?.hideLoading() }
                    .subscribe({ view?.showUnSubscribedToSource() }, this::onError)
                    .addTo(subscriptions)

        } else {
            subscribeToSource.execute(SourceRequest(view!!.getSource().id, view!!.getSource().apply {  subscribed = true}.toDomain()))
                    .doOnSubscribe { view?.showLoading() }
                    .doAfterTerminate { view?.hideLoading() }
                    .subscribe({ view?.showSubscribedToSource() }, this::onError)
                    .addTo(subscriptions)
        }
    }

    override fun onExitAction() {
        view?.close()
    }
}