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

import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesPresenter
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesView
import com.dbeginc.dbweathernews.viewmodels.toViewModel
import io.reactivex.disposables.Disposable

/**
 * Created by darel on 17.11.17.
 *
 * Favorite Lives Presenter Implementation
 */
class FavoriteLivesPresenterImpl(private val model: NewsRepository, private val threads: ThreadProvider) : FavoriteLivesPresenter {
    private var subscription: Disposable? = null

    override fun bind(view: FavoriteLivesView) = view.setupView()

    override fun unBind() {
        subscription?.dispose()
    }

    override fun loadFavoriteLives(view: FavoriteLivesView) {
        model.getFavoriteLives()
                .doOnSubscribe { view.showLoading() }
                .doAfterTerminate { view.hideLoading() }
                .observeOn(threads.computation)
                .flatMap { favoriteNames -> model.getLives(favoriteNames) }
                .map { favorites -> favorites.map { live -> live.toViewModel(true) } }
                .observeOn(threads.ui)
                .subscribe(view::displayFavoriteLives, view::onError)
                .also { subscription = it }
    }
}