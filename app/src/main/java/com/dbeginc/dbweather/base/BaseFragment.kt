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

package com.dbeginc.dbweather.base

import android.arch.lifecycle.ViewModelProvider
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.dbeginc.dbweather.utils.preferences.ApplicationPreferences
import com.dbeginc.dbweather.utils.utility.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * Created by darel on 29.05.17.
 *
 * Base Fragment
 */
open class BaseFragment : DaggerFragment() {
    @Inject
    lateinit var preferences: ApplicationPreferences
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    protected fun askLocationPermIfNeeded(): Boolean {
        if (ContextCompat.checkSelfPermission(
                        context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )

        } else preferences.changeGpsPermissionStatus(true)

        return preferences.isGpsPermissionOn()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            preferences.changeGpsPermissionStatus(grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)
        }
    }
}
