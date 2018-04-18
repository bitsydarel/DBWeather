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

package com.dbeginc.dbweatherdata.implementations.repositories

import android.content.Context
import android.util.Log
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.CrashlyticsLogger
import com.dbeginc.dbweatherdata.RxThreadProvider
import com.dbeginc.dbweatherdata.TAG
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.local.weather.RoomWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.HttpApiWeatherDataSource
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import com.dbeginc.dbweatherdomain.repositories.WeatherRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver

/**
 * Created by darel on 15.09.17.
 *
 * Weather Repository Implementation
 */
class WeatherRepositoryImpl private constructor(private val local: LocalWeatherDataSource,
                                                private val remote: RemoteWeatherDataSource,
                                                private val logger: Logger,
                                                private val thread: RxThreadProvider) : WeatherRepository {

    companion object {
        fun create(context: Context) : WeatherRepository {
            return WeatherRepositoryImpl(
                    RoomWeatherDataSource.create(context),
                    HttpApiWeatherDataSource.create(context),
                    CrashlyticsLogger,
                    RxThreadProvider
            )
        }
    }

    private val subscriptions = CompositeDisposable()

    override fun getWeather(request: WeatherRequest<String>): Flowable<Weather> {
        // Remote request to api
        val remoteRequest = remote.getWeather(WeatherRequest("", request.latitude, request.longitude, Unit))
                .subscribeOn(thread.IO)

        // If requested location is empty
        // we need to go to the network to get weather info by lat and long
        return if (request.arg.isEmpty()) remoteRequest.doAfterSuccess(this::addWeather).toFlowable()
        else local.getWeather(request)
                .subscribeOn(thread.CP)
                .doOnSubscribe {
                    // keep updating the weather from the api
                    remoteRequest.subscribe(this::addWeather, logger::logError)
                }
    }

    override fun getWeatherForLocation(request: WeatherRequest<String>): Flowable<Weather> {
        return local.getWeatherForLocation(locationName = request.city, countryCode = request.arg)
                .subscribeOn(thread.CP)
                .doOnSubscribe {
                    remote.getWeather(WeatherRequest("", request.latitude, request.longitude, Unit))
                            .subscribeOn(thread.IO)
                            .subscribe(this::addWeatherForLocation, logger::logError)
                }
    }

    override fun getLocations(name: String): Single<List<Location>> {
        return remote.getLocations(name)
                .subscribeOn(thread.IO)
    }

    override fun getAllUserLocations(): Flowable<List<Location>> {
        return local.getUserLocations()
                .subscribeOn(thread.CP)
    }

    override fun deleteWeatherForLocation(name: String): Completable {
        return local.deleteWeatherForLocation(name)
                .subscribeOn(thread.CP)
    }

    override fun clean() = subscriptions.clear()

    private fun addWeather(weather: Weather) {
        subscriptions.add(
                local.updateWeather(weather)
                        .subscribeOn(thread.CP)
                        .subscribeWith(UpdateObserver())
        )
    }

    private fun addWeatherForLocation(weather: Weather) {
        subscriptions.add(
                local.updateWeatherLocation(weather)
                        .subscribeOn(thread.CP)
                        .subscribeWith(UpdateObserver())
        )
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Update of data done in ${WeatherRepository::class.java.simpleName}")
            }
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, "Error: ${WeatherRepository::class.java.simpleName}", e)
        }
    }

}