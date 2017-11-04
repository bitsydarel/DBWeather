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

package com.dbeginc.dbweather.base

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_GPS_PERMISSION_GRANTED
import com.dbeginc.dbweather.utils.holder.ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.dbeginc.dbweather.utils.utility.Injector
import com.google.firebase.database.DataSnapshot
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

/**
 * Created by darel on 29.05.17.
 * Base Fragment
 */

open class BaseFragment : Fragment() {
    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var appContext: Context

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectBaseFragmentDep(this)
    }

    protected fun isNetworkAvailable() : Boolean {
        val networkInfo: NetworkInfo?
        val manager = activity.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        networkInfo = manager.activeNetworkInfo

        var isAvailable = false

        if (networkInfo != null && networkInfo.isConnected) {
            isAvailable = true
        }

        return isAvailable
    }

    protected fun askLocationPermIfNeeded(): Boolean {
        if (ContextCompat.checkSelfPermission(appContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )

        } else preferences.edit().putBoolean(IS_GPS_PERMISSION_GRANTED, true).apply()

        return preferences.getBoolean(IS_GPS_PERMISSION_GRANTED, false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                preferences.edit().putBoolean(IS_GPS_PERMISSION_GRANTED, true).apply()

            } else preferences.edit().putBoolean(IS_GPS_PERMISSION_GRANTED, false).apply()
        }
    }
}
