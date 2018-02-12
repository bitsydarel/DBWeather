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

package com.dbeginc.dbweather.config.managelocations.presenter

import com.dbeginc.dbweather.config.managelocations.view.ManageLocationsView
import com.dbeginc.dbweathercommon.utils.ThreadProvider
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.repositories.weather.WeatherRepository
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.toViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 26.10.17.
 *
 * Manage Locations Presenter Implementation
 */
class ManageLocationsPresenterImpl(private val model: WeatherRepository, private val threads: ThreadProvider) : ManageLocationsPresenter {
    private val subscriptions = CompositeDisposable()

    override fun bind(view: ManageLocationsView) = view.setupView()

    override fun unBind() = subscriptions.clear()

    override fun loadUserLocations(view: ManageLocationsView) {
        model.getAllUserLocations()
                .doOnSubscribe { view.showUpdateStatus() }
                .doAfterTerminate(view::hideUpdateStatus)
                .observeOn(threads.computation)
                .map { locations -> locations.map { location -> location.toViewModel() } }
                .observeOn(threads.ui)
                .subscribe(
                        { locations -> if (locations.isEmpty()) view.displayNoLocations() else view.displayLocations(locations) },
                        view::onError
                ).addTo(subscriptions)
    }

    override fun removeLocation(view: ManageLocationsView, removedItem: LocationWeatherModel) {
        model.deleteWeatherForLocation(removedItem.name)
                .doOnSubscribe { view.showUpdateStatus() }
                .doAfterTerminate(view::hideUpdateStatus)
                .doOnError { loadUserLocations(view) }
                .subscribe(view::showLocationRemovedMessage, view::onError)
                .addTo(subscriptions)
    }

    override fun goBack(view: ManageLocationsView) = view.goBackToConfiguration()

}