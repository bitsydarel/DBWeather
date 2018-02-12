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

import android.support.annotation.VisibleForTesting
import com.dbeginc.dbweather.config.managesources.adapter.contract.SourcePresenter
import com.dbeginc.dbweather.config.managesources.adapter.contract.SourceView
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.entities.requests.news.SourceRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import com.dbeginc.dbweathernews.viewmodels.toDomain
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 27.10.17.
 *
 * Source Presenter Implementation
 */
class SourcePresenterImpl(private val source: SourceModel,
                          private val model: NewsRepository) : SourcePresenter {

    private val subscriptions = CompositeDisposable()

    override fun bind(view: SourceView) = view.setupView()

    override fun unBind() = subscriptions.clear()

    override fun loadSource(view: SourceView) = view.displaySource(source)

    override fun onAction(view: SourceView) = view.goToSourceDetail()

    override fun onSubscribe(view: SourceView) = if (source.subscribed) unSubscribeFromSource(view) else subscribeToSource(view)

    override fun getData(): SourceModel = source

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun unSubscribeFromSource(view: SourceView) {
        model.unSubscribeToSource(SourceRequest(source.id, source.apply { subscribed = false }.toDomain()))
                .doOnSubscribe { view.showLoading() }
                .doOnTerminate { view.hideLoading() }
                .subscribe(view::showUnSubscribed, view::onError)
                .addTo(subscriptions)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun subscribeToSource(view: SourceView) {
        model.subscribeToSource(SourceRequest(source.id, source.apply { subscribed = true }.toDomain()))
                .doOnSubscribe { view.showLoading() }
                .doOnTerminate(view::hideLoading)
                .subscribe(view::showSubscribed, view::onError)
                .addTo(subscriptions)
    }
}