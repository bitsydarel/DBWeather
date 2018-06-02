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

package com.dbeginc.dbweathernews.lives

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.dbweathercommon.BaseViewModel
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.viewmodels.YoutubeLiveModel
import com.dbeginc.dbweathernews.viewmodels.toUi
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 12.02.18.
 *
 * Youtube Lives ViewModel
 *
 * This view model also act as a bridge between
 * View -> ViewModel -> Presenter
 *
 * The view model process any long related task that the view want to do
 * presenter take care of validation or processing of synchronous task
 *
 * the view model don't have reference to view neither the presenter
 * view model use observable data example (liveData or BehaviorSubject)
 * presenter don't save the view instance so we dont have to deal with leaks
 * presenter only receive the view as method parameter (method scope variable), execute his task and then return void or result
 */
class YoutubeLivesViewModel @Inject constructor(private val model: NewsRepository, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override var subscriptions = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _youtubeLives: MutableLiveData<List<YoutubeLiveModel>> = MutableLiveData()
    private val _youtubeLivesListener = BehaviorRelay.create<List<YoutubeLiveModel>>()
    val presenter = YoutubeLivesViewPresenter()

    init {
        _youtubeLivesListener.subscribe(_youtubeLives::postValue)
    }

    // public getter for observable data, it's as LiveData not Mutable because we only want
    // the view model to post new data
    fun getLives(): LiveData<List<YoutubeLiveModel>> = _youtubeLives

    fun loadAllLives() {
        model.getAllLives()
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .flatMap(
                        { model.getFavoriteLives() },
                        { lives, favorites ->
                            lives.map { live ->
                                live.toUi(favorites.contains(live.name))
                            }
                        }
                )
                .observeOn(threads.UI)
                .subscribe(_youtubeLivesListener)
                .addTo(subscriptions)
    }

    fun addToFavorite(youtubeLive: YoutubeLiveModel) {
        model.addLiveToFavorites(LiveRequest(youtubeLive.name, Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                )
    }

    fun removeFromFavorite(youtubeLive: YoutubeLiveModel) {
        model.removeLiveFromFavorites(LiveRequest(youtubeLive.name, Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(it)
                        }
                )
    }

}