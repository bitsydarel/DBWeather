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

package com.dbeginc.dbweatherdata.implementations.repositories

import android.content.Context
import android.util.Log
import com.dbeginc.dbweatherdata.ConstantHolder
import com.dbeginc.dbweatherdata.ThreadProvider
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.local.weather.LocalWeatherDataSourceImpl
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteWeatherDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.RemoteWeatherDataSourceImpl
import com.dbeginc.dbweatherdomain.entities.requests.weather.LocationRequest
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver

/**
 * Created by darel on 15.09.17.
 *
 * Weather Repository Implementation
 */
class WeatherRepositoryImpl private constructor(private val local: LocalWeatherDataSource,
                                                private val remote: RemoteWeatherDataSource,
                                                private val thread: ThreadProvider) : WeatherRepository {

    companion object {
        fun create(context: Context) : WeatherRepository {
            return WeatherRepositoryImpl(
                    LocalWeatherDataSourceImpl.create(context),
                    RemoteWeatherDataSourceImpl.create(context),
                    ThreadProvider
            )
        }
    }

    private val subscriptions = CompositeDisposable()

    override fun getWeather(request: WeatherRequest<String>): Flowable<Weather> {
        return remote.getWeather(WeatherRequest(request.latitude, request.longitude, Unit))
                .subscribeOn(thread.io)
                .doOnNext { weather -> subscriptions.addWeather(weather) }
                .publish {
                    remoteData -> Flowable.mergeDelayError(remoteData, local.getWeather(request).takeUntil(remoteData).subscribeOn(thread.computation).toFlowable())
                }.observeOn(thread.ui)
    }

    override fun getWeatherForLocation(request: WeatherRequest<String>): Flowable<Weather> {
        return remote.getWeatherForLocation(WeatherRequest(request.latitude, request.longitude, Unit))
                .subscribeOn(thread.io)
                .doOnNext { weather -> subscriptions.addWeatherForLocation(weather) }
                .publish {
                    remoteData -> Flowable.mergeDelayError(remoteData, local.getWeatherForLocation(request.arg).takeUntil(remoteData).subscribeOn(thread.computation).toFlowable())
                }.observeOn(thread.ui)
    }

    override fun getLocations(request: LocationRequest): Flowable<List<Location>> {
        return remote.getLocations(request)
                .subscribeOn(thread.io)
                .publish {
                    remoteData -> Flowable.mergeDelayError(remoteData, local.getLocations(request).takeUntil(remoteData).subscribeOn(thread.computation).toFlowable())
                }
                .observeOn(thread.ui)
    }

    override fun getAllUserLocations(): Flowable<List<Location>> {
        return local.getUserLocations()
                .subscribeOn(thread.computation)
                .toFlowable()
                .observeOn(thread.ui)
    }

    override fun deleteWeatherForLocation(request: LocationRequest): Completable {
        return local.getLocations(request)
                .subscribeOn(thread.computation)
                .toFlowable()
                .flatMapIterable { locations -> locations }
                .flatMap { (name, _, _) -> local.getWeatherForLocation(name).subscribeOn(thread.computation).toFlowable() }
                .flatMapCompletable { weather -> local.deleteWeatherForLocation(weather).subscribeOn(thread.computation) }
                .observeOn(thread.ui)
    }

    override fun clean() = subscriptions.clear()

    private fun CompositeDisposable.addWeather(weather: Weather) {
        add(local.updateWeather(weather).subscribeOn(thread.computation)
                .subscribeWith(UpdateObserver())
        )
    }

    private fun CompositeDisposable.addWeatherForLocation(weather: Weather) {
        add(local.updateWeatherLocation(weather).subscribeOn(thread.computation)
                .subscribeWith(UpdateObserver())
        )
    }

    private inner class UpdateObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            Log.i(ConstantHolder.TAG, "Update of data done in ${WeatherRepository::class.java.simpleName}")
        }

        override fun onError(e: Throwable) {
            Log.e(ConstantHolder.TAG, "Error: ${WeatherRepository::class.java.simpleName}", e)
        }
    }

}