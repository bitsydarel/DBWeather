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

package com.dbeginc.dbweather.intro.gpslocationfinder.view

import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentGpsLocationFinderBinding
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.intro.gpslocationfinder.presenter.GpsLocationFinderPresenter
import com.dbeginc.dbweather.intro.gpslocationfinder.presenter.GpsLocationFinderPresenterImpl
import com.dbeginc.dbweather.utils.locations.WeatherLocationManager
import com.dbeginc.dbweather.utils.utility.snack

/**
 * Created by darel on 29.09.17.
 *
 * Gps Location Finder
 */
class GpsLocationFinderFragment : BaseFragment() {
    private val presenter: GpsLocationFinderPresenter = GpsLocationFinderPresenterImpl()
    private lateinit var binding: FragmentGpsLocationFinderBinding
    private lateinit var locationFinder: WeatherLocationManager
    private lateinit var observer: Observer

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        locationFinder = WeatherLocationManager(appContext)

        observer = Observer(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_gps_location_finder, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        cleanState()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        binding.gpsLocationFinderLayout.post {
            if (preferences.isGpsPermissionOn()) {
                playAnimation()
                locationFinder.observe(this, observer)

            } else reAskLocation()
        }
    }

    private fun reAskLocation() {
        binding.gpsLocationFinderAnimation.pauseAnimation()

        showMessage(getString(R.string.location_permission_request))

        binding.gpsLocationFinderLayout.postDelayed({ askLocationPermIfNeeded() }, 320)
    }

    /***************************************** Gps Location Finder Custom View Part *****************************************/

    override fun setupView() {
        binding.gpsLocationFinderLayout.post {
            pauseAnimation()
            if (askLocationPermIfNeeded()) {
                playAnimation()
                locationFinder.observe(this, observer)
            }
        }
    }

    override fun cleanState() {
        locationFinder.removeObserver(observer)
        presenter.unBind()
    }

    override fun defineGpsLocation(latitude: Double, longitude: Double) {
        preferences.updateDefaultCoordinates(preferences.getDefaultLocation(), latitude, longitude)
        preferences.changeDefaultLocationStatus(true)
    }

    override fun goToMainScreen() {
        Navigator.goToMainScreen(context)
        activity?.finish()
    }

    override fun showMessage(message: String) = binding.gpsLocationFinderLayout.snack(message)

    private fun playAnimation() = binding.gpsLocationFinderAnimation.playAnimation()

    private fun pauseAnimation() = binding.gpsLocationFinderAnimation.pauseAnimation()

    private inner class Observer(val locationPresenter: GpsLocationFinderPresenter) : android.arch.lifecycle.Observer<android.location.Location> {
        override fun onChanged(location: Location?) {
            if (location != null) locationPresenter.onLocationFind(this@GpsLocationFinderFragment, location.latitude, location.longitude)
        }
    }
}