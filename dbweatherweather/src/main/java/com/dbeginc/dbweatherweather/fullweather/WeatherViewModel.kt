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

package com.dbeginc.dbweatherweather.fullweather

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.dbeginc.dbweathercommon.utils.LogDispatcher
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import com.dbeginc.dbweatherweather.fullweather.contract.WeatherPresenter
import com.dbeginc.dbweatherweather.fullweather.contract.WeatherView
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.WeatherModel
import com.dbeginc.dbweatherweather.viewmodels.toViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by darel on 07.02.18.
 *
 * Weather Presenter Implementation
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
class WeatherViewModel @Inject constructor(private val model: WeatherRepository) : ViewModel() {
    private val subscriptions = CompositeDisposable()
    // RX Stream that notify request stream if user want to retry
    private val retryRequest = PublishSubject.create<Unit>()
    // Rx Behavior relay that subscribe to request and notify view of new data
    private val defaultWeatherResponseListener = BehaviorRelay.create<WeatherModel>()
    private val customWeatherResponseListener = BehaviorRelay.create<WeatherModel>()
    private val locationsResponseListener = BehaviorRelay.create<List<LocationWeatherModel>>()
    // Observable data
    private val _defaultWeatherModel: MutableLiveData<WeatherModel> = MutableLiveData()
    private val _customWeatherModel: MutableLiveData<WeatherModel> = MutableLiveData()
    private val _listOfLocations: MutableLiveData<List<LocationWeatherModel>> = MutableLiveData()
    // View Presenter
    val presenter = WeatherViewPresenter()

    init {
        // We subscribe to the relay on creation
        defaultWeatherResponseListener.subscribe(_defaultWeatherModel::postValue).addTo(subscriptions)
        customWeatherResponseListener.subscribe(_customWeatherModel::postValue).addTo(subscriptions)
        locationsResponseListener.subscribe(_listOfLocations::postValue).addTo(subscriptions)
    }

    // public getter for observable data, it's as LiveData not Mutable because we only want
    // the view model to post new data
    fun getDefaultWeather(): LiveData<WeatherModel> = _defaultWeatherModel

    fun getCustomWeather(): LiveData<WeatherModel> = _customWeatherModel

    fun getUserLocations(): LiveData<List<LocationWeatherModel>> = _listOfLocations

    fun loadWeatherForCity(requestStateListener: BehaviorSubject<RequestState>, location: LocationWeatherModel) {
        model.getWeatherForLocation(WeatherRequest(arg = location.name, latitude = location.latitude, longitude = location.longitude))
                .doOnSubscribe { requestStateListener.onNext(RequestState.LOADING) }
                .doAfterNext { requestStateListener.onNext(RequestState.COMPLETED) }
                .doOnError {
                    requestStateListener.onNext(RequestState.ERROR)
                    LogDispatcher.logError(it)
                }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .observeOn(ThreadProvider.computation)
                .map { weather -> weather.toViewModel() }
                .observeOn(ThreadProvider.ui)
                .subscribe(customWeatherResponseListener)
                .addTo(subscriptions)
    }

    fun loadWeather(requestStateListener: BehaviorSubject<RequestState>, location: LocationWeatherModel) {
        model.getWeather(WeatherRequest(arg = location.name, latitude = location.latitude, longitude = location.longitude))
                .doOnSubscribe { requestStateListener.onNext(RequestState.LOADING) }
                .doAfterNext { requestStateListener.onNext(RequestState.COMPLETED) }
                .doOnError {
                    requestStateListener.onNext(RequestState.ERROR)
                    LogDispatcher.logError(it)
                }
                .observeOn(ThreadProvider.computation)
                .map { weather -> weather.toViewModel() }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .observeOn(ThreadProvider.ui)
                .subscribe(defaultWeatherResponseListener)
                .addTo(subscriptions)
    }

    fun loadUserCities() {
        model.getAllUserLocations()
                .observeOn(ThreadProvider.computation)
                .map { locations -> locations.map { location -> location.toViewModel() } }
                .observeOn(ThreadProvider.ui)
                .subscribe(locationsResponseListener)
                .addTo(subscriptions)
    }

    fun onUserLocationSelected(requestStateListener: BehaviorSubject<RequestState>, position: Int, foundedLocations: List<LocationWeatherModel>) {
        if (foundedLocations.isNotEmpty()) {
            loadWeatherForCity(requestStateListener, foundedLocations[position])
        }
    }

    override fun onCleared() = subscriptions.clear()


    inner class WeatherViewPresenter : WeatherPresenter {
        override fun retryWeatherRequest() = retryRequest.onNext(Unit)

        override fun bind(view: WeatherView) = view.setupView()

        override fun onDefaultWeatherInfoReceived(view: WeatherView, location: LocationWeatherModel) {
            view.defineDefaultCoordinates(location)
            view.changeCurrentLocationType(true)
        }

        override fun onNonGpsWeatherInfoReceived(view: WeatherView, location: LocationWeatherModel) {
            view.defineCustomLocationCoordinates(location)
            view.changeCurrentLocationType(false)
        }

        override fun onDefaultLocationChanged(view: WeatherView, location: LocationWeatherModel) = view.defineDefaultCoordinates(location)
    }

}