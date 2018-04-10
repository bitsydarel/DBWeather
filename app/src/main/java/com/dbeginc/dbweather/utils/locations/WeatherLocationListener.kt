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
import android.location.Location
import android.location.LocationListener
import android.os.Bundle

/**
 * Created by darel on 22.03.18.
 *
 * Location Listener
 */
class WeatherLocationListener(private val bridge: LocationBridge) : com.google.android.gms.location.LocationListener, LocationListener {
    override fun onLocationChanged(location: Location) = bridge.onLocationChanged(location)

    @SuppressLint("MissingPermission")
    override fun onProviderEnabled(provider: String) = bridge.onReadyToListen(provider)

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}

    override fun onProviderDisabled(p0: String?) {}
}