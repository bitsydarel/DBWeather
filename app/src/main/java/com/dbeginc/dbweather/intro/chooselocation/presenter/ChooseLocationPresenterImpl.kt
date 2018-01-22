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

package com.dbeginc.dbweather.intro.chooselocation.presenter

import android.util.Log
import com.dbeginc.dbweather.intro.chooselocation.ChooseLocationContract
import com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.toViewModel
import com.dbeginc.dbweatherdomain.entities.requests.weather.LocationRequest
import com.dbeginc.dbweatherdomain.usecases.weather.GetLocations
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 30.09.17.
 *
 * Choose Location Presenter
 */
class ChooseLocationPresenterImpl(private val getLocations: GetLocations) : ChooseLocationContract.ChooseLocationPresenter {
    private lateinit var view: ChooseLocationContract.ChooseLocationView
    private val subscriptions = CompositeDisposable()

    override fun bind(view: ChooseLocationContract.ChooseLocationView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        getLocations.clean()
        subscriptions.clear()
    }

    override fun loadLocations(query: String) {
        getLocations.execute(LocationRequest(query))
                .doOnSubscribe { view.showLoadingStatus() }
                .doOnTerminate { view.hideLocationAnimation() }
                .doAfterTerminate { view.hideLoadingStatus() }
                .subscribe(
                        { locations -> view.displayLocations(locations.map { location -> location.toViewModel() }) },
                        { error -> handleError(error) }
                ).addTo(subscriptions)
    }

    private fun handleError(error: Throwable) {
        Log.e(TAG, error.localizedMessage, error)
        view.showError(error.localizedMessage)
    }

    override fun onLocationSelected(location: LocationWeatherModel) {
        view.defineCurrentLocation(latitude=location.latitude, longitude=location.longitude, locationName=location.name)
        view.goToMainScreen()
    }
}