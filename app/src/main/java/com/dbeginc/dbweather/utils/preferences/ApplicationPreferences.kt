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

package com.dbeginc.dbweather.utils.preferences

import android.content.SharedPreferences
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweathernews.newspapers.NewsPapersViewModel

/**
 * Created by darel on 12.11.17.
 *
 * Application Preferences Manage all configuration
 * For the application layer
 */
class ApplicationPreferences(private val sharedPreferences: SharedPreferences) {

    fun getDefaultLatitude(): Double = sharedPreferences.getDouble(LATITUDE)

    fun getDefaultLongitude(): Double = sharedPreferences.getDouble(LONGITUDE)

    fun getDefaultLocation(): String = sharedPreferences.getString(CURRENT_LOCATION, "")

    fun getCustomLatitude(): Double = sharedPreferences.getDouble(USER_LATITUDE)

    fun getCustomLongitude(): Double = sharedPreferences.getDouble(USER_LONGITUDE)

    fun getCustomLocation(): String = sharedPreferences.getString(USER_LOCATION, "")

    fun updateDefaultCoordinates(location: String, latitude: Double, longitude: Double) {
        sharedPreferences.edit()
                .putString(CURRENT_LOCATION, location)
                .putDouble(LATITUDE, latitude)
                .putDouble(LONGITUDE, longitude)
                .apply()
    }

    fun updateCustomCoordinates(location: String, latitude: Double, longitude: Double) {
        sharedPreferences.edit()
                .putString(USER_LONGITUDE, location)
                .putDouble(USER_LATITUDE, latitude)
                .putDouble(USER_LONGITUDE, longitude)
                .apply()
    }

    fun changeGpsPermissionStatus(isOn: Boolean) = sharedPreferences.edit().putBoolean(IS_GPS_PERMISSION_GRANTED, isOn).apply()

    fun isGpsPermissionOn(): Boolean = sharedPreferences.getBoolean(IS_GPS_PERMISSION_GRANTED, false)

    fun updateCurrentLocationType(isDefault: Boolean) = sharedPreferences.edit().putBoolean(IS_GPS_LOCATION, isDefault).apply()

    fun isCurrentLocationDefault(): Boolean = sharedPreferences.getBoolean(IS_GPS_LOCATION, false)

    fun changeDefaultLocationStatus(isFromGps: Boolean) = sharedPreferences.edit().putBoolean(IS_GPS_LOCATION, isFromGps).apply()

    fun isFirstLaunchOfApplication(): Boolean = sharedPreferences.getBoolean(FIRST_RUN, true)

    fun changeFirstLaunchStatus() = sharedPreferences.edit().putBoolean(FIRST_RUN, false).apply()

    fun getNewsPaperPreferredOrder(): Int = sharedPreferences.getInt(SOURCE_SORTING_PREFERENCES, NewsPapersViewModel.SORT_BY_NAME)

    fun getYoutubeLivesPreferredOrder(): Int = sharedPreferences.getInt(YOUTUBE_LIVES_SORTING_PREFERENCES, NewsPapersViewModel.SORT_BY_NAME)

}