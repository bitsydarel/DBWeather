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

package com.dbeginc.dbweathernews.newspaperdetail

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.dbweathercommon.BaseViewModel
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsPaperRequest
import com.dbeginc.dbweatherdomain.repositories.NewsRepository
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import com.dbeginc.dbweathernews.viewmodels.toUi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 24.03.18.
 *
 * NewsPaper Detail ViewModel
 */
class NewsPaperDetailViewModel @Inject constructor(private val model: NewsRepository, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _newsPaperDetail = MutableLiveData<NewsPaperModel>()

    fun getNewsPaperDetail(): LiveData<NewsPaperModel> = _newsPaperDetail

    fun loadNewsPaperDetail(name: String) {
        model.getNewsPaper(NewsPaperRequest(sourceId = name, arg = Unit))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError { requestState.postValue(RequestState.ERROR) }
                .map { source -> source.toUi() }
                .observeOn(threads.UI)
                .subscribe(_newsPaperDetail::postValue, logger::logError)
                .addTo(subscriptions)
    }

}