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

package com.dbeginc.dbweather.utils.utility

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.support.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import com.dbeginc.dbweather.utils.preferences.ApplicationPreferences
import com.dbeginc.dbweatherweather.viewmodels.WeatherLocationModel
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by darel on 18.09.17.
 *
 * Framework extensions or helpers
 */

fun View.getInflater(): LayoutInflater = LayoutInflater.from(context)

/**
 * Returns a list of packages that support Custom Tabs.
 */
fun getCustomTabsPackages(context: Context): List<String> {
    val pm = context.packageManager
    // Get default VIEW intent handler.
    val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))

    // Get all apps that can handle VIEW intents.
    val resolvedActivityList = pm.queryIntentActivities(activityIntent, 0)

    val packagesSupportingCustomTabs = mutableListOf<String>()

    for (info in resolvedActivityList) {
        val serviceIntent = Intent()
        serviceIntent.action = ACTION_CUSTOM_TABS_CONNECTION

        serviceIntent.`package` = info.activityInfo.packageName

        // Check if this package also resolves the Custom Tabs service.
        if (pm.resolveService(serviceIntent, 0) != null) {
            packagesSupportingCustomTabs.add(serviceIntent.`package`)
        }
    }
    return packagesSupportingCustomTabs
}


@JvmField
val WEATHER_SEARCH_RESULTS: BehaviorSubject<List<WeatherLocationModel>> =
        BehaviorSubject.create()

fun Context.getColorPrimaryDark(): Int {
    val colorAccent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.attr.colorPrimaryDark
    else resources.getIdentifier("colorPrimaryDark", "attr", packageName)

    val value = TypedValue()

    theme.resolveAttribute(colorAccent, value, true)

    return value.data
}

fun SharedPreferences.Editor.putDouble(key: String, value: Double): SharedPreferences.Editor {
    putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
    return this
}

fun SharedPreferences.getDouble(key: String): Double =
        java.lang.Double.longBitsToDouble(getLong(key, 0))

fun ApplicationPreferences.findDefaultLocation(): WeatherLocationModel = WeatherLocationModel(
        name = getDefaultCity(),
        countryCode = getDefaultCountryCode(),
        countryName = "",
        latitude = getDefaultLatitude(),
        longitude = getDefaultLongitude()
)

fun ApplicationPreferences.findCustomLocation(): WeatherLocationModel = WeatherLocationModel(
        name = getCustomCity(),
        countryCode = getCustomCountryCode(),
        countryName = "",
        latitude = getCustomLatitude(),
        longitude = getCustomLongitude()
)

fun WeatherLocationModel.fullName() = name.plus(", ").plus(countryCode)