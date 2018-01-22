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

package com.dbeginc.dbweather.weather.presenter

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweather.weather.WeatherTabContract
import com.dbeginc.dbweathercommon.ThreadProvider
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.usecases.weather.GetAllUserLocations
import com.dbeginc.dbweatherdomain.usecases.weather.GetLocations
import com.dbeginc.dbweatherdomain.usecases.weather.GetWeather
import com.dbeginc.dbweatherdomain.usecases.weather.GetWeatherByLocation
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.toViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Created by darel on 28.05.17.
 * Weather view presenter
 */

class WeatherTabPresenterImpl(private val getLocations: GetLocations,
                              private val getWeather: GetWeather,
                              private val getWeatherByLocation: GetWeatherByLocation,
                              private val getAllUserLocationsCommand: GetAllUserLocations,
                              private val locationSearchEvent: BehaviorSubject<List<Location>>,
                              private val threads: ThreadProvider) : WeatherTabContract.WeatherTabPresenter {

    private var view: WeatherTabContract.WeatherTabView? = null
    private val rxSubscriptions = CompositeDisposable()
    private val retryRequest = PublishSubject.create<Unit>()
    private var searchLocations: List<LocationWeatherModel> = listOf()

    override fun onGpsLocationChanged(latitude: Double, longitude: Double) {
        view?.defineGpsCoordinates(LocationWeatherModel(view!!.getGpsLocation(), latitude, longitude, "", ""))
    }

    override fun bind(view: WeatherTabContract.WeatherTabView) {
        this.view = view
        this.view?.setupView()

        locationSearchEvent
                .observeOn(threads.computation)
                .subscribe(
                        { locations -> searchLocations = locations.map { location -> location.toViewModel() } },
                        { error -> notifyLoadError(error) }
                ).addTo(rxSubscriptions)
    }

    override fun unBind() {
        getWeather.clean()
        getWeatherByLocation.clean()
        getLocations.clean()
        getAllUserLocationsCommand.clean()
        rxSubscriptions.clear()
        view = null
    }

    override fun getWeatherForCity(location: LocationWeatherModel) {
        getWeatherByLocation.execute(WeatherRequest(latitude = location.latitude, longitude = location.longitude, arg = location.name))
                .doOnSubscribe { _ -> view?.displayLoadingWeatherStatus() }
                .doAfterTerminate { view?.hideLoadingWeatherStatus() }
                .observeOn(threads.computation)
                .map { weather -> weather.toViewModel() }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .observeOn(threads.ui)
                .subscribe(
                        { weatherModel ->
                            view?.displayWeather(weatherModel)
                            view?.defineLastPreviewedCoordinates(weatherModel.location)
                            view?.changeCurrentLocationSource(false)
                        },
                        { error -> notifyWeatherError(error) }
                ).addTo(rxSubscriptions)
    }

    override fun getWeather() {
        loadWeather()
    }

    override fun loadUserCities() {
        getAllUserLocationsCommand.execute(Unit)
                .doOnSubscribe { view?.displayUpdatingStatus() }
                .doAfterTerminate { view?.hideUpdatingStatus() }
                .observeOn(threads.computation)
                .map { locations -> locations.map { location -> location.toViewModel() } }
                .observeOn(threads.ui)
                .subscribe(
                        { locations -> view?.showUserLocations(locations) },
                        { error -> notifyLoadError(error) }
                ).addTo(rxSubscriptions)
    }

    override fun onUserLocationSelected(position: Int) {
        if (searchLocations.isNotEmpty()) {
            getWeatherForCity(searchLocations[position])
            view?.changeCurrentLocationSource(false)
        }
    }

    private fun loadWeather() {
        getWeather.execute(WeatherRequest(view!!.getGpsLatitude(), view!!.getGpsLongitude(), view!!.getGpsLocation()))
                .doOnSubscribe { view?.displayLoadingWeatherStatus() }
                .doAfterTerminate { view?.hideLoadingWeatherStatus() }
                .observeOn(threads.computation)
                .map { weather -> weather.toViewModel() }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .observeOn(threads.ui)
                .subscribe(
                        { weatherModel ->
                            view?.displayWeather(weatherModel)
                            view?.defineGpsCoordinates(weatherModel.location)
                            view?.changeCurrentLocationSource(true)
                        },
                        { error -> notifyWeatherError(error) }
                ).addTo(rxSubscriptions)
    }

    override fun retryWeatherRequest() = retryRequest.onNext(Unit)

    private fun notifyLoadError(error: Throwable) {
        view?.showError(error.localizedMessage)
        Log.e(TAG, error.localizedMessage, error)
        Crashlytics.logException(error)
    }

    private fun notifyWeatherError(error: Throwable) {
        view?.showWeatherError(error.localizedMessage)
        Log.e(TAG, error.localizedMessage, error)
        Crashlytics.logException(error)
    }
}
