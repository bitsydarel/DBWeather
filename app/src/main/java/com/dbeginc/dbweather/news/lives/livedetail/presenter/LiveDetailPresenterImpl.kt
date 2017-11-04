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

package com.dbeginc.dbweather.news.lives.livedetail.presenter

import com.dbeginc.dbweather.news.lives.livedetail.LiveDetailContract
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweather.viewmodels.news.toViewModel
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.usecases.news.AddLiveToFavorite
import com.dbeginc.dbweatherdomain.usecases.news.GetFavoriteLives
import com.dbeginc.dbweatherdomain.usecases.news.GetLive
import com.dbeginc.dbweatherdomain.usecases.news.RemoveLiveToFavorite
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 21.10.17.
 *
 * Live Detail Presenter Implementation
 */
class LiveDetailPresenterImpl(private val getLive: GetLive, private val addLiveToFavorite: AddLiveToFavorite, private val removeLiveToFavorite: RemoveLiveToFavorite, private val getFavoriteLives: GetFavoriteLives) : LiveDetailContract.LiveDetailPresenter{
    private lateinit var view: LiveDetailContract.LiveDetailView
    private val subscription = CompositeDisposable()
    private var subscribed = true

    override fun bind(view: LiveDetailContract.LiveDetailView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        subscription.clear()
    }

    override fun loadLive() {
        getLive.execute(LiveRequest(view.getLiveName(), Unit))
                .doOnSubscribe { view.showUpdatingStatus() }
                .doAfterTerminate { view.hideUpdatingStatus() }
                .map { live -> live.toViewModel() }
                .subscribe(
                        { live -> view.displayYoutube(live) },
                        { error -> view.showError(error.localizedMessage) }
                ).addTo(subscription)
    }

    override fun onBookmark() {
        if (subscribed) {
            removeLiveToFavorite.execute(LiveRequest(view.getLiveName(), Unit))
                    .doOnSubscribe { view.showUpdatingStatus() }
                    .doAfterTerminate { view.hideUpdatingStatus() }
                    .subscribe(
                            {
                                subscribed = false
                                view.liveFavorite()
                            },
                            { error -> view.showError(error.localizedMessage) }
                    ).addTo(subscription)
        } else {
            addLiveToFavorite.execute(LiveRequest(view.getLiveName(), Unit))
                    .doOnSubscribe { view.showUpdatingStatus() }
                    .doAfterTerminate { view.hideUpdatingStatus() }
                    .subscribe(
                            {
                                subscribed = true
                                view.liveNotFavorite()
                            },
                            { error -> view.showError(error.localizedMessage) }
                    ).addTo(subscription)
        }
    }

    override fun onShare() {
        view.shareLive()
    }

    override fun onBackClicked() {
        view.goBackToLiveList()
    }

    override fun checkIfLiveFavorite() {
        getFavoriteLives.execute(Unit)
                .flatMapIterable { favorites -> favorites.toMutableList() }
                .filter { liveName -> liveName == view.getLiveName() }
                .firstElement()
                .isEmpty
                .subscribe(
                        { notFound ->
                            subscribed = notFound
                            if (notFound) view.liveNotFavorite() else view.liveFavorite() },
                        { error -> view.showError(error.localizedMessage) }
                ).addTo(subscription)
    }
}