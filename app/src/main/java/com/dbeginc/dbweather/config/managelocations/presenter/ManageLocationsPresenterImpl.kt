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

import com.dbeginc.dbweather.config.managelocations.ManageLocationsContract
import com.dbeginc.dbweather.viewmodels.weather.LocationWeatherModel
import com.dbeginc.dbweather.viewmodels.weather.toViewModel
import com.dbeginc.dbweatherdomain.entities.requests.weather.LocationRequest
import com.dbeginc.dbweatherdomain.usecases.weather.GetAllUserLocations
import com.dbeginc.dbweatherdomain.usecases.weather.RemoveLocation

/**
 * Created by darel on 26.10.17.
 *
 * Manage Locations Presenter Implementation
 */
class ManageLocationsPresenterImpl(private val getAllUserLocations: GetAllUserLocations, private val removeLocation: RemoveLocation) : ManageLocationsContract.ManageLocationsPresenter {
    private lateinit var view: ManageLocationsContract.ManageLocationsView

    override fun bind(view: ManageLocationsContract.ManageLocationsView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        getAllUserLocations.clean()
        removeLocation.clean()
    }

    override fun loadUserLocations() {
        getAllUserLocations.execute(Unit)
                .doOnSubscribe { view.showUpdateStatus() }
                .doOnTerminate { view.hideUpdateStatus() }
                .map { locations -> locations.map { location -> location.toViewModel() } }
                .subscribe(
                        { locations -> if (locations.isEmpty()) view.displayNoLocations() else view.displayLocations(locations) },
                        { error -> view.showError(error.localizedMessage) }
                )
    }

    override fun removeLocation(removedItem: LocationWeatherModel) {
        removeLocation.execute(LocationRequest(removedItem.name))
                .doOnSubscribe { view.showUpdateStatus() }
                .doAfterTerminate { view.hideUpdateStatus() }
                .subscribe(
                        { view.showLocationRemovedMessage() },
                        { error ->
                            view.showError(error.localizedMessage)
                            loadUserLocations()
                        }
                )
    }

    override fun goBack() = view.goBackToConfiguration()
}