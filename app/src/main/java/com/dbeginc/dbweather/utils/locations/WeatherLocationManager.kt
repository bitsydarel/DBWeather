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

package com.dbeginc.dbweather.utils.locations

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

/**
 * Created by darel on 21.09.17.
 *
 * Location Observer
 */
class WeatherLocationManager(appContext: Context) : LiveData<Location>(), LocationBridge {
    private val listener by lazy { WeatherLocationListener(this) }
    private val locationCallback by lazy { WeatherLocationCallback(this) }
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val locationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(appContext)

    companion object {
        private const val MIN_LOCATION_TIME_INTERVAL: Long = 300000
        private const val MIN_LOCATION_DISTANCE = 0f
    }

    override fun onLocationChanged(location: Location) = postValue(location)

    @SuppressLint("MissingPermission")
    override fun onReadyToListen(locationProvider: String) =
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_LOCATION_TIME_INTERVAL,
                    MIN_LOCATION_DISTANCE,
                    listener
            )

    @SuppressLint("MissingPermission")
    override fun onActive() {
        val request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(MIN_LOCATION_TIME_INTERVAL) // Seconds, in milliseconds
                .setFastestInterval(1000) // 1 Seconds, in milliseconds

        locationClient.requestLocationUpdates(
                request,
                locationCallback,
                null
        )

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_LOCATION_TIME_INTERVAL,
                    MIN_LOCATION_DISTANCE,
                    listener
            )
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_LOCATION_TIME_INTERVAL,
                    MIN_LOCATION_DISTANCE,
                    listener
            )

    }

    override fun onInactive() {
        locationClient.removeLocationUpdates(locationCallback)
        locationManager.removeUpdates(listener)
    }

}