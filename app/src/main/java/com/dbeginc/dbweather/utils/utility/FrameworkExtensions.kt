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

package com.dbeginc.dbweather.utils.utility

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.Window
import android.view.WindowManager
import com.dbeginc.dbweather.utils.helper.ApplicationPreferences
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import java.util.*

/**
 * Created by darel on 18.09.17.
 *
 * Framework extensions or helpers
 */
fun <T : Parcelable> Bundle.putList(key: String, list: List<T>) {
    val arrayList : ArrayList<T> = ArrayList(list)
    putParcelableArrayList(key, arrayList)
}

fun <T : Parcelable> Bundle.getList(key: String) : List<T> = getParcelableArrayList(key)

fun SharedPreferences.Editor.putDouble(key: String, value: Double): SharedPreferences.Editor {
    putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
    return this
}

fun SharedPreferences.getDouble(key: String) : Double {
    return java.lang.Double.longBitsToDouble(getLong(key, 0))
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
fun Window.changeStatusBarColor(color: Int) {
    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    statusBarColor = color
}

fun ApplicationPreferences.findDefaultLocation(): LocationWeatherModel {
    return LocationWeatherModel(
            getDefaultLocation(),
            getDefaultLatitude(),
            getDefaultLongitude(),
            "",
            ""
    )
}

fun ApplicationPreferences.findCustomLocation(): LocationWeatherModel {
    val location = getCustomLocation().split(",")
    val latitude = getCustomLatitude()
    val longitude = getCustomLongitude()

    return if (location.isEmpty() || location.size < 3) LocationWeatherModel("", latitude, longitude, "", "")
    else LocationWeatherModel(name = location[0], countryCode = location[2], countryName = location[1], latitude = latitude, longitude = longitude)
}

fun FloatingActionMenu.availableLocations(): List<String> {
    return (childCount.minus(1) downTo 0)
            .mapNotNull { getChildAt(it) }
            .filterIsInstance(FloatingActionButton::class.java)
            .mapNotNull { view -> view.labelText }
}

fun LocationWeatherModel.fullName() = name.plus(", ").plus(countryCode)

fun <T : Parcelable> Bundle.getArray(key: String): Array<T> = getParcelableArray(key) as Array<T>