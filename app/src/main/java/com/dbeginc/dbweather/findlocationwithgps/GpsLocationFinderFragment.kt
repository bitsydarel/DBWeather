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

package com.dbeginc.dbweather.findlocationwithgps

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentGpsLocationFinderBinding
import com.dbeginc.dbweather.utils.locations.WeatherLocationManager
import com.dbeginc.dbweather.utils.utility.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.dbeginc.dbweather.utils.utility.goToChooseDefaultNewsPapers
import com.dbeginc.dbweather.utils.utility.goToIntroScreen

/**
 * Created by darel on 29.09.17.
 *
 * Gps Location Finder
 */
class GpsLocationFinderFragment : BaseFragment() {
    private lateinit var binding: FragmentGpsLocationFinderBinding
    private lateinit var locationFinder: WeatherLocationManager

    private val gpsLocationObserver: Observer<Location> = Observer { location ->
        location?.let { validLocation ->
            preferences.get().run {
                updateDefaultCoordinates(
                        city = getDefaultCity(),
                        countryCode = getDefaultCountryCode(),
                        latitude = validLocation.latitude,
                        longitude = validLocation.longitude
                )

                changeDefaultLocationStatus(isFromGps = true)
            }

            activity?.let {
                goToChooseDefaultNewsPapers(container = it, layoutId = R.id.launchContent)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        locationFinder = WeatherLocationManager(appContext = context.applicationContext)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_gps_location_finder,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.gpsLocationFinderToolbar)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { container ->
            binding.gpsLocationFinderToolbar.setNavigationOnClickListener {
                goToIntroScreen(container = container, layoutId = R.id.launchContent)
            }
        }

        if (askLocationPermIfNeeded())
            locationFinder.observe(this, gpsLocationObserver)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            preferences.get().changeGpsPermissionStatus(
                    grantResults.isNotEmpty()
                            && grantResults.first() == PackageManager.PERMISSION_GRANTED
            )

            if (preferences.get().isGpsPermissionOn()) {

                locationFinder.observe(this, gpsLocationObserver)

            } else reAskLocation()
        } else reAskLocation()
    }

    private fun reAskLocation() {
        binding.gpsLocationFinderAnimation.pauseAnimation()

        Snackbar.make(binding.gpsLocationFinderLayout, R.string.location_permission_request, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { askLocationPermIfNeeded() }
                .show()

    }

}