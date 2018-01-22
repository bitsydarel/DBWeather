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

import com.dbeginc.dbweathernews.viewmodels.toViewModel
import com.dbeginc.dbweathercommon.ThreadProvider
import com.dbeginc.dbweathercommon.addTo
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.usecases.news.AddLiveToFavorite
import com.dbeginc.dbweatherdomain.usecases.news.GetFavoriteLives
import com.dbeginc.dbweatherdomain.usecases.news.GetLive
import com.dbeginc.dbweatherdomain.usecases.news.RemoveLiveToFavorite
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailPresenter
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailView
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 18.11.17.
 *
 * Live Detail Presenter Implementation
 */
class LiveDetailPresenterImpl(private val getLive: GetLive, private val addLiveToFavorite: AddLiveToFavorite, private val removeLiveToFavorite: RemoveLiveToFavorite, private val getFavoriteLives: GetFavoriteLives, private val threads: ThreadProvider): LiveDetailPresenter {
    private var view: LiveDetailView? = null
    private var subscribed = true
    private val subscription = CompositeDisposable()

    override fun bind(view: LiveDetailView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        getLive.clean()
        addLiveToFavorite.clean()
        removeLiveToFavorite.clean()
        getFavoriteLives.clean()
        subscription.clear()
    }

    override fun loadLive() {
        getLive.execute(LiveRequest(view!!.getLiveName(), Unit))
                .doOnSubscribe { view?.showLoading() }
                .doAfterTerminate { view?.hideLoading() }
                .map { live -> live.toViewModel() }
                .subscribe(this::onValue, this::onError)
                .addTo(subscription)
    }

    override fun onBookmark() {
        if (subscribed) {
            removeLiveToFavorite.execute(LiveRequest(view!!.getLiveName(), Unit))
                    .doOnSubscribe { view?.showLoading() }
                    .doAfterTerminate { view?.hideLoading() }
                    .subscribe(
                            {
                                subscribed = false
                                view?.showLiveIsNotFavorite()
                            },
                            { error -> onError(error) }
                    ).addTo(subscription)
        } else {
            addLiveToFavorite.execute(LiveRequest(view!!.getLiveName(), Unit))
                    .doOnSubscribe { view?.showLoading() }
                    .doAfterTerminate { view?.hideLoading() }
                    .subscribe(
                            {
                                subscribed = true
                                view?.showLiveIsFavorite()
                            },
                            { error -> onError(error) }
                    ).addTo(subscription)
        }
    }

    override fun onShare() {
        view?.shareLive()
    }

    override fun onExitAction() {
        view?.close()
    }

    override fun checkIfLiveFavorite() {
        getFavoriteLives.execute(Unit)
                .observeOn(threads.computation)
                .flatMapIterable { favorites -> favorites }
                .filter { liveName -> liveName == view?.getLiveName() }
                .firstElement()
                .isEmpty
                .observeOn(threads.ui)
                .subscribe(
                        { notFound ->
                            subscribed = notFound
                            if (notFound) view?.showLiveIsNotFavorite() else view?.showLiveIsFavorite()
                        },
                        { error -> onError(error) }
                ).addTo(subscription)
    }

    override fun onValue(newValue: LiveModel) {
        view?.displayLive(newValue)
    }

    override fun onError(error: Throwable) {
        Logger.error(LiveDetailPresenterImpl::class.java.simpleName, error.localizedMessage, error)
        view?.showError(error.localizedMessage)
    }
}