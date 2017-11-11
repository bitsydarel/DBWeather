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

package com.dbeginc.dbweather.news.lives.page.favorite.presenter

import com.dbeginc.dbweather.news.lives.page.favorite.FavoriteLivesTabContract
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweather.viewmodels.news.toViewModel
import com.dbeginc.dbweatherdomain.usecases.news.GetFavoriteLives
import com.dbeginc.dbweatherdomain.usecases.news.GetLives
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 20.10.17.
 *
 * Favorite Lives Tab Presenter Implementation
 */
class FavoriteLivesTabPresenterImpl(private val getFavoriteLives: GetFavoriteLives, private val getLives: GetLives) : FavoriteLivesTabContract.FavoriteLivesTabPresenter{
    private var view: FavoriteLivesTabContract.FavoriteLivesTabView? = null
    private val subscriptions = CompositeDisposable()

    override fun bind(view: FavoriteLivesTabContract.FavoriteLivesTabView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscriptions.clear()
        view = null
    }

    override fun loadFavoriteLives() {
        getFavoriteLives.execute(Unit)
                .doOnSubscribe { view?.showUpdateStatus() }
                .doOnTerminate { view?.hideUpdateStatus() }
                .flatMap { favorites -> getLives.execute(favorites) }
                .map { favorites -> favorites.map { live -> live.toViewModel(true) } }
                .subscribe(
                        { lives -> view?.displayFavoriteLives(lives) },
                        { error -> view?.showError(error.localizedMessage) }
                ).addTo(subscriptions)
    }
}