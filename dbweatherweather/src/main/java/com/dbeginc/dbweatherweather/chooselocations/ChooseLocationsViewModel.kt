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

package com.dbeginc.dbweatherweather.chooselocations

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dbeginc.dbweathercommon.utils.LogDispatcher
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import com.dbeginc.dbweatherweather.chooselocations.contract.ChooseLocationsPresenter
import com.dbeginc.dbweatherweather.chooselocations.contract.ChooseLocationsView
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.toViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by darel on 12.02.18.
 *
 * Choose Locations ViewModel
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
class ChooseLocationsViewModel @Inject constructor(private val model: WeatherRepository) : ViewModel() {
    private val subscriptions = CompositeDisposable()
    private val retryRequest = PublishSubject.create<Unit>()
    private val locationsResponseListener = BehaviorRelay.create<List<LocationWeatherModel>>()
    private val _listOfLocations: MutableLiveData<List<LocationWeatherModel>> = MutableLiveData()
    val presenter: ChooseLocationsPresenter = ChooseLocationsViewPresenter()

    init {
        // We subscribe to the relay on creation
        locationsResponseListener.subscribe(_listOfLocations::postValue).addTo(subscriptions)
    }

    // public getter for observable data, it's as LiveData not Mutable because we only want
    // the view model to post new data
    fun getLocations(): LiveData<List<LocationWeatherModel>> = _listOfLocations

    fun findLocations(requestStateListener: BehaviorSubject<RequestState>, query: String) {
        model.getLocations(query)
                .doOnSubscribe { requestStateListener.onNext(RequestState.LOADING) }
                .doAfterNext { requestStateListener.onNext(RequestState.COMPLETED) }
                .doOnError {
                    requestStateListener.onNext(RequestState.ERROR)
                    LogDispatcher.logError(it)
                }
                .observeOn(ThreadProvider.computation)
                .map { locations -> locations.map { location -> location.toViewModel() } }
                .subscribe(locationsResponseListener)
                .addTo(subscriptions)
    }

    override fun onCleared() = subscriptions.clear()

    inner class ChooseLocationsViewPresenter : ChooseLocationsPresenter {
        override fun bind(view: ChooseLocationsView) = view.setupView()

        override fun onLocationSelected(view: ChooseLocationsView, location: LocationWeatherModel) {
            assert(location.name.isNotEmpty())

            assert(location.latitude > 0 && location.longitude > 0)

            view.defineCurrentLocation(location)
        }

        override fun retryLocationsRequest() = retryRequest.onNext(Unit)

        override fun onExitAction(view: ChooseLocationsView) = view.onNavigationAction()
    }
}