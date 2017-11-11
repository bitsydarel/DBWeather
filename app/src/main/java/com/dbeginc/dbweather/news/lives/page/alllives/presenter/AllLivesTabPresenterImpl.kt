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

package com.dbeginc.dbweather.news.lives.page.alllives.presenter

import com.dbeginc.dbweather.news.lives.page.alllives.AllLivesTabContract
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweather.viewmodels.news.LiveModel
import com.dbeginc.dbweather.viewmodels.news.toViewModel
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.usecases.news.GetAllLives
import com.dbeginc.dbweatherdomain.usecases.news.GetFavoriteLives
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction

/**
 * Created by darel on 20.10.17.
 *
 * All Lives Tab Presenter Implementation
 */
class AllLivesTabPresenterImpl(private val getAllLives: GetAllLives, private val getFavoriteLives: GetFavoriteLives) : AllLivesTabContract.AllLivesTabPresenter {
    private var view: AllLivesTabContract.AllLivesTabView? = null
    private val subscriptions = CompositeDisposable()

    override fun bind(view: AllLivesTabContract.AllLivesTabView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscriptions.clear()
        view = null
    }

    override fun loadAllLives() {
        getAllLives.execute(Unit)
                .zipWith(
                        getFavoriteLives.execute(Unit),
                        BiFunction<List<Live>, List<String>, List<LiveModel>> { lives, favorites -> lives.map { live -> live.toViewModel(favorites.contains(live.name)) } }
                ).doOnSubscribe { view?.showUpdateStatus() }
                .doOnTerminate { view?.hideUpdateStatus() }
                .subscribe(
                        { lives -> view?.displayAllLives(lives) },
                        { error -> view?.showError(error.localizedMessage) }
                ).addTo(subscriptions)
    }
}