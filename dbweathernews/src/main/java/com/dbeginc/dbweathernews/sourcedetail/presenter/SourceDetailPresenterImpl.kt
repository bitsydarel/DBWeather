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

import android.support.annotation.VisibleForTesting
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.entities.requests.news.SourceRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailPresenter
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailView
import com.dbeginc.dbweathernews.viewmodels.toDomain
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 18.11.17.
 *
 * Source Detail Presenter Implementation
 */
class SourceDetailPresenterImpl(private val model: NewsRepository) : SourceDetailPresenter {

    private val subscriptions = CompositeDisposable()

    override fun bind(view: SourceDetailView) = view.setupView()

    override fun unBind() = subscriptions.clear()

    override fun loadSourceDetail(view: SourceDetailView) {
        model.getSource(SourceRequest(view.getSource().id, Unit))
                .doOnSubscribe { view.showLoading() }
                .doAfterTerminate { view.hideLoading() }
                .map { source -> source.toViewModel() }
                .subscribe(view::displaySourceDetail, view::onError)
    }

    override fun onSubscribeAction(view: SourceDetailView) = if (view.getSource().subscribed) unSubscribeToSource(view) else subscribeToSource(view)

    override fun onExitAction(view: SourceDetailView) = view.close()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun unSubscribeToSource(view: SourceDetailView) {
        model.unSubscribeToSource(SourceRequest(view.getSource().id, view.getSource().apply { subscribed = false }.toDomain()))
                .doOnSubscribe { view.showLoading() }
                .doAfterTerminate { view.hideLoading() }
                .subscribe(view::showUnSubscribedToSource, view::onError)
                .addTo(subscriptions)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun subscribeToSource(view: SourceDetailView) {
        model.subscribeToSource(SourceRequest(view.getSource().id, view.getSource().apply { subscribed = true }.toDomain()))
                .doOnSubscribe { view.showLoading() }
                .doAfterTerminate(view::hideLoading)
                .subscribe(view::showSubscribedToSource, view::onError)
                .addTo(subscriptions)
    }
}