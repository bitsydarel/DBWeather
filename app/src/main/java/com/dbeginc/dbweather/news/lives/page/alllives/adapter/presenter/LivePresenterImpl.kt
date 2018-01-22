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

package com.dbeginc.dbweather.news.lives.page.alllives.adapter.presenter

import com.dbeginc.dbweather.news.lives.page.alllives.adapter.contract.LivePresenter
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.contract.LiveView
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.usecases.news.AddLiveToFavorite
import com.dbeginc.dbweatherdomain.usecases.news.RemoveLiveToFavorite
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 19.10.17.
 *
 * Live Presenter Implementation
 */
class LivePresenterImpl(private val live: LiveModel,
                        private val addLiveToFavorite: AddLiveToFavorite,
                        private val removeLiveToFavorite: RemoveLiveToFavorite) : LivePresenter {

    private var view: LiveView? = null
    private val tasks = CompositeDisposable()

    override fun bind(view: LiveView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        tasks.clear()
        view = null
    }

    override fun loadLive() {
        view?.displayLive(live)
    }

    override fun getData(): LiveModel = live

    override fun onAction() {
        if (live.isFavorite) {
            removeLiveToFavorite.execute(LiveRequest(live.name, Unit))
                    .doOnSubscribe { view?.showUnBookmarkAnimation() }
                    .subscribe(
                            { live.isFavorite = false },
                            { error ->
                                view?.showBookmarkAnimation()
                                view?.showError(error)
                            }
                    )
        } else {
            addLiveToFavorite.execute(LiveRequest(live.name, Unit))
                    .doOnSubscribe { view?.showBookmarkAnimation() }
                    .subscribe(
                            { live.isFavorite = true },
                            { error ->
                                view?.showUnBookmarkAnimation()
                                view?.showError(error)
                            }
                    )
        }
    }
}