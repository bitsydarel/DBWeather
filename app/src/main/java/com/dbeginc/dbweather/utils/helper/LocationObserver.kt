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

package com.dbeginc.dbweather.utils.helper

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.google.android.gms.location.*

/**
 * Created by darel on 21.09.17.
 *
 * Location Observer
 */
class LocationObserver(appContext: Context) : LiveData<Location>() {
    private val locationClient: FusedLocationProviderClient
    private val listener: Listener
    private val locationManager = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val locationCallback: Callback = Callback()

    init {
        listener = Listener()
        locationClient = LocationServices.getFusedLocationProviderClient(appContext)
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        locationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null)

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300000, 0f, listener)
        else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300000, 0f, listener)

    }

    override fun onInactive() {
        locationClient.removeLocationUpdates(locationCallback)
        locationManager.removeUpdates(listener)
    }

    private inner class Listener : com.google.android.gms.location.LocationListener, LocationListener {
        override fun onLocationChanged(location: Location) = postValue(location)
        @SuppressLint("MissingPermission")
        override fun onProviderEnabled(provider: String) = locationManager.requestLocationUpdates(provider, 300000, 0f, this)
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
        override fun onProviderDisabled(p0: String?) {}
    }

    private inner class Callback : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result?.postValue()
        }
    }

    /**
     * Method that create an LocationRequest
     * with specific parameter
     *
     * @return LocationRequest
     */
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(300000) // Seconds, in milliseconds
                .setFastestInterval(1000) // 1 Seconds, in milliseconds
    }

    private fun LocationResult.postValue() = this@LocationObserver.postValue(lastLocation)
}