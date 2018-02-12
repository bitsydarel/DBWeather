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

package com.dbeginc.dbweathernews.livedetail.presenter

import android.support.annotation.VisibleForTesting
import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailPresenter
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailView
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 18.11.17.
 *
 * Live Detail Presenter Implementation
 */
class LiveDetailPresenterImpl(private val model: NewsRepository, private val threads: ThreadProvider) : LiveDetailPresenter {
    private val subscription = CompositeDisposable()
    private var subscribed = true

    override fun bind(view: LiveDetailView) = view.setupView()

    override fun unBind() = subscription.clear()

    override fun loadLive(view: LiveDetailView) {
        model.getLive(view.getLiveName())
                .doOnSubscribe { view.showLoading() }
                .doAfterTerminate { view.hideLoading() }
                .map { live -> live.toViewModel() }
                .subscribe(view::displayLive, view::onError)
                .addTo(subscription)
    }

    override fun onBookmark(view: LiveDetailView) = if (subscribed) removeLiveFromFavorites(view) else addLiveToFavorites(view)

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun removeLiveFromFavorites(view: LiveDetailView) {
        model.removeLiveFromFavorites(LiveRequest(view.getLiveName(), Unit))
                .doOnSubscribe { view.showLoading() }
                .doAfterTerminate { view.hideLoading() }
                .subscribe({ updateSubscription(false, view::showLiveIsNotFavorite) }, view::onError)
                .addTo(subscription)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun addLiveToFavorites(view: LiveDetailView) {
        model.addLiveToFavorites(LiveRequest(view.getLiveName(), Unit))
                .doOnSubscribe { view.showLoading() }
                .doAfterTerminate { view.hideLoading() }
                .subscribe({ updateSubscription(true, view::showLiveIsFavorite) }, view::onError)
                .addTo(subscription)
    }

    override fun onShare(view: LiveDetailView) = view.shareLive()

    override fun onExitAction(view: LiveDetailView) = view.close()

    override fun checkIfLiveFavorite(view: LiveDetailView) {
        model.getFavoriteLives()
                .observeOn(threads.computation)
                .flatMapIterable { favorites -> favorites }
                .filter { liveName -> liveName == view.getLiveName() }
                .firstElement()
                .isEmpty
                .observeOn(threads.ui)
                .subscribe(
                        { notFound -> updateSubscription(notFound, if (notFound) view::showLiveIsNotFavorite else view::showLiveIsFavorite) },
                        view::onError
                )
                .addTo(subscription)
    }

    private fun updateSubscription(hasBeenSubscribe: Boolean, action: () -> Unit) {
        subscribed = hasBeenSubscribe
        action()
    }
}