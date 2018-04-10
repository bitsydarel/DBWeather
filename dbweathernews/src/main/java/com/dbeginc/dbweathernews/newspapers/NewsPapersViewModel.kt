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

package com.dbeginc.dbweathernews.newspapers

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.dbweathercommon.BaseViewModel
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.repositories.NewsRepository
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import com.dbeginc.dbweathernews.viewmodels.toUi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 10.02.18.
 *
 * News Papers ViewModel
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
class NewsPapersViewModel @Inject constructor(private val model: NewsRepository, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    // Observable data
    private val _newsPapersModels: MutableLiveData<List<NewsPaperModel>> = MutableLiveData()

    companion object {
        const val SORT_BY_NAME = 1
        const val SORT_BY_LANGUAGE = 2
        const val SORT_BY_COUNTRY = 3
        const val SORT_BY_CATEGORY = 4
    }

    // public getter for observable data, it's as LiveData not Mutable because we only want
    // the view model to post new data
    fun getNewsPapers(): LiveData<List<NewsPaperModel>> = _newsPapersModels

    /**
     * Load newspapers articles
     *
     * we don't pass the view to the method but a proxy object (behavior subject)
     *
     * @param sortBy used to sort sources by specific criteria
     */
    fun loadNewspaperSources(sortBy: Int) {
        model.getSubscribedNewsPapers()
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError {
                    requestState.postValue(RequestState.ERROR)
                    logger.logError(it)
                }
                .map { sources -> sources.map { source -> source.toUi() } }
                .map { sources ->
                    when (sortBy) {
                        SORT_BY_NAME -> sources.sortedBy { it.name }
                        SORT_BY_COUNTRY -> sources.sortedBy { it.country }
                        SORT_BY_LANGUAGE -> sources.sortedBy { it.language }
                        SORT_BY_CATEGORY -> sources.sortedBy { it.category }
                        else -> sources.sortedBy { it.name }
                    }
                }
                .observeOn(threads.UI)
                .subscribe(_newsPapersModels::postValue, logger::logError)
                .addTo(subscriptions)
    }

}