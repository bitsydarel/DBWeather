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

package com.dbeginc.dbweatherweather.fullweather

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.dbeginc.dbweathercommon.BaseViewModel
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.repositories.WeatherRepository
import com.dbeginc.dbweatherweather.viewmodels.WeatherLocationModel
import com.dbeginc.dbweatherweather.viewmodels.WeatherModel
import com.dbeginc.dbweatherweather.viewmodels.toUi
import io.reactivex.disposables.CompositeDisposable
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
class WeatherViewModel @Inject constructor(private val model: WeatherRepository, private val threads: ThreadProvider, private val logger: Logger) : BaseViewModel() {
    override val subscriptions = CompositeDisposable()
    override val requestState: MutableLiveData<RequestState> = MutableLiveData()
    // Observable data
    private val _defaultWeatherModel: MutableLiveData<WeatherModel> = MutableLiveData()
    private val _customWeatherModel: MutableLiveData<WeatherModel> = MutableLiveData()
    private val _listOfLocations: MutableLiveData<List<WeatherLocationModel>> = MutableLiveData()
    // public getter for observable data, it's as LiveData not Mutable because we only want
    // the view model to post new data
    fun getDefaultWeather(): LiveData<WeatherModel> = _defaultWeatherModel

    fun getCustomWeather(): LiveData<WeatherModel> = _customWeatherModel

    fun getUserLocations(): LiveData<List<WeatherLocationModel>> = _listOfLocations

    fun loadWeatherForCity(location: WeatherLocationModel) {
        model.getWeatherForLocation(WeatherRequest(arg = location.name, latitude = location.latitude, longitude = location.longitude))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError { requestState.postValue(RequestState.ERROR) }
                .map { weather -> weather.toUi() }
                .observeOn(threads.UI)
                .subscribe(_customWeatherModel::postValue, logger::logError)
                .addTo(subscriptions)
    }

    fun loadWeather(location: WeatherLocationModel) {
        model.getWeather(WeatherRequest(arg = location.name, latitude = location.latitude, longitude = location.longitude))
                .doOnSubscribe { requestState.postValue(RequestState.LOADING) }
                .doAfterNext { requestState.postValue(RequestState.COMPLETED) }
                .doOnError { requestState.postValue(RequestState.ERROR) }
                .map { weather -> weather.toUi() }
                .observeOn(threads.UI)
                .subscribe(_defaultWeatherModel::postValue, logger::logError)
                .addTo(subscriptions)
    }

    fun loadUserCities() {
        model.getAllUserLocations()
                .map { locations -> locations.map { location -> location.toUi() } }
                .observeOn(threads.UI)
                .subscribe(_listOfLocations::postValue, logger::logError)
                .addTo(subscriptions)
    }

    fun onUserLocationSelected(position: Int, foundedLocations: List<WeatherLocationModel>) {
        if (foundedLocations.isNotEmpty()) loadWeatherForCity(foundedLocations[position])
    }

}