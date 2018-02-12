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

import android.support.annotation.VisibleForTesting
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.contract.LivePresenter
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.contract.LiveView
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 19.10.17.
 *
 * Live Presenter Implementation
 */
class LivePresenterImpl(private val live: LiveModel,
                        private val model: NewsRepository) : LivePresenter {

    private val tasks = CompositeDisposable()

    override fun bind(view: LiveView) = view.setupView()

    override fun unBind() = tasks.clear()

    override fun loadLive(view: LiveView) = view.displayLive(live)

    override fun getData(): LiveModel = live

    override fun onAction(view: LiveView) {
        if (live.isFavorite) removeFromFavorites(live, view)
        else addInFavorites(live, view)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun removeFromFavorites(live: LiveModel, view: LiveView) {
        model.removeLiveFromFavorites(LiveRequest(live.name, Unit))
                .subscribe(
                        {
                            view.showUnBookmarkAnimation()
                            live.isFavorite = false
                        },
                        view::onError
                ).addTo(tasks)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun addInFavorites(live: LiveModel, view: LiveView) {
        model.addLiveToFavorites(LiveRequest(live.name, Unit))
                .subscribe(
                        {
                            view.showBookmarkAnimation()
                            live.isFavorite = true
                        },
                        view::onError
                ).addTo(tasks)
    }
}