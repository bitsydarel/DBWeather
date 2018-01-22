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

package com.dbeginc.dbweathernews.favoritelives.presenter

import com.dbeginc.dbweathernews.viewmodels.toViewModel
import com.dbeginc.dbweathercommon.ThreadProvider
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.usecases.news.GetFavoriteLives
import com.dbeginc.dbweatherdomain.usecases.news.GetLives
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesPresenter
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesView
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import io.reactivex.disposables.Disposable

/**
 * Created by darel on 17.11.17.
 *
 * Favorite Lives Presenter Implementation
 */
class FavoriteLivesPresenterImpl(private val getFavoriteLives: GetFavoriteLives, private val getLives: GetLives, private val threads: ThreadProvider)  : FavoriteLivesPresenter {
    private var view: FavoriteLivesView? = null
    private var subscription: Disposable? = null

    override fun bind(view: FavoriteLivesView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscription?.dispose()
        view = null
    }

    override fun loadFavoriteLives() {
        subscription = getFavoriteLives.execute(Unit)
                .doOnSubscribe { view?.showLoading() }
                .doAfterTerminate{ view?.hideLoading() }
                .observeOn(threads.computation)
                .flatMap { favorites -> getLives.execute(favorites) }
                .map { favorites -> favorites.map { live -> live.toViewModel(true) } }
                .observeOn(threads.ui)
                .subscribe(this::onValue, this::onError)
    }

    override fun onValue(newValue: List<LiveModel>) {
        view?.displayFavoriteLives(newValue)
    }

    override fun onError(error: Throwable) {
        Logger.error(FavoriteLivesPresenterImpl::class.java.canonicalName, error.localizedMessage, error)
        view?.showError(error.localizedMessage)
    }
}