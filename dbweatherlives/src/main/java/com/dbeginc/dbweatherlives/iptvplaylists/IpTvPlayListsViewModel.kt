/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherlives.iptvplaylists

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.dbweathercommon.BaseViewModel
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.repositories.LivesRepository
import com.dbeginc.dbweatherlives.viewmodels.IpTvPlayListModel
import com.dbeginc.dbweatherlives.viewmodels.toUi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class IpTvPlayListsViewModel @Inject constructor(private val model: LivesRepository, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _iptvPlayLists = MutableLiveData<List<IpTvPlayListModel>>()

    fun getIpTvPlayLists(): LiveData<List<IpTvPlayListModel>> = _iptvPlayLists

    fun loadIpTvPlayLists() {
        model.getAllIpTvPlaylist()
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError { requestState.postValue(RequestState.ERROR) }
                .map { playlists -> playlists.map { playList -> playList.toUi() } }
                .observeOn(threads.UI)
                .subscribe(_iptvPlayLists::postValue, logger::logError)
                .addTo(subscriptions)
    }

    fun findPlayList(name: String) {
        model.findPlaylist(name = name)
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterSuccess { requestState.postValue(RequestState.COMPLETED) }
                .doOnError { requestState.postValue(RequestState.ERROR) }
                .map { playlists -> playlists.map { playList -> playList.toUi() } }
                .observeOn(threads.UI)
                .subscribe(_iptvPlayLists::postValue, logger::logError)
                .addTo(subscriptions)
    }
}