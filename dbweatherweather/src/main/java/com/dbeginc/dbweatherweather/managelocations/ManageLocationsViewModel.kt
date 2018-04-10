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

package com.dbeginc.dbweatherweather.managelocations

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.dbweathercommon.BaseViewModel
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.repositories.WeatherRepository
import com.dbeginc.dbweatherweather.viewmodels.WeatherLocationModel
import com.dbeginc.dbweatherweather.viewmodels.toUi
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by darel on 23.03.18.
 *
 * Manage Locations ViewModel
 */
class ManageLocationsViewModel @Inject constructor(private val model: WeatherRepository, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions: CompositeDisposable = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    private val _locations = MutableLiveData<List<WeatherLocationModel>>()

    fun getLocations(): LiveData<List<WeatherLocationModel>> = _locations

    fun loadUserLocations() {
        model.getAllUserLocations()
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError { requestState.postValue(RequestState.ERROR) }
                .map { locations -> locations.map { it.toUi() } }
                .observeOn(threads.UI)
                .subscribe(_locations::postValue, logger::logError)
                .addTo(subscriptions)
    }

    fun deleteLocation(location: WeatherLocationModel) {
        model.deleteWeatherForLocation(location.name)
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .observeOn(threads.UI)
                .subscribe(
                        { requestState.postValue(RequestState.COMPLETED) },
                        {
                            requestState.postValue(RequestState.ERROR)
                            logger.logError(error = it)
                        }
                ).addTo(subscriptions)
    }
}