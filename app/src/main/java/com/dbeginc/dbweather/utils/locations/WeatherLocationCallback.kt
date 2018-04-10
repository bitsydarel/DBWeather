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

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

/**
 * Created by darel on 22.03.18.
 *
 * Location Callback
 */
class WeatherLocationCallback(private val bridge: LocationBridge) : LocationCallback() {
    override fun onLocationResult(result: LocationResult?) {
        result?.let {
            bridge.onLocationChanged(it.lastLocation)
        }
    }
}