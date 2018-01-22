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

package com.dbeginc.dbweathernews.lives.presenter

import com.dbeginc.dbweathernews.viewmodels.toViewModel
import com.dbeginc.dbweathercommon.ThreadProvider
import com.dbeginc.dbweathercommon.logger.Logger
import com.dbeginc.dbweatherdomain.usecases.news.GetAllLives
import com.dbeginc.dbweatherdomain.usecases.news.GetFavoriteLives
import com.dbeginc.dbweathernews.lives.contract.LivesPresenter
import com.dbeginc.dbweathernews.lives.contract.LivesView
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import io.reactivex.disposables.Disposable

/**
 * Created by darel on 17.11.17.
 *
 * Lives Presenter Implementation
 */
class LivesPresenterImpl(private val getAllLives: GetAllLives, private val getFavoriteLives: GetFavoriteLives, private val thread: ThreadProvider) : LivesPresenter {
    private var view: LivesView? = null
    private var subscription: Disposable? = null

    override fun bind(view: LivesView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscription?.dispose()
        view = null
    }

    override fun loadAllLives() {
        subscription = getAllLives.execute(Unit)
                .doOnSubscribe { view?.showLoading() }
                .doAfterTerminate { view?.hideLoading() }
                .observeOn(thread.computation)
                .flatMap(
                        { getFavoriteLives.execute(Unit) },
                        {lives, favorites -> lives.map { live -> live.toViewModel(favorites.contains(live.name)) } }
                )
                .observeOn(thread.ui)
                .subscribe(this::onValue, this::onError)
    }

    override fun onValue(newValue: List<LiveModel>) {
        view?.displayLives(newValue)
    }

    override fun onError(error: Throwable) {
        Logger.error(tag=LivesPresenterImpl::class.java.simpleName, message=error.localizedMessage, error=error)
        view?.showError(error.localizedMessage)
    }
}