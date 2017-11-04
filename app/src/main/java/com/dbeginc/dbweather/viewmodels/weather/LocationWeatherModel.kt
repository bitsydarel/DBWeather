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

package com.dbeginc.dbweather.viewmodels.weather

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by darel on 30.09.17.
 *
 * Location Weather Model
 */
data class LocationWeatherModel(val name: String, val latitude: Double, val longitude: Double,
                                val countryCode: String, val countryName: String
) : Parcelable, Comparable<LocationWeatherModel> {

    private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(countryCode)
        parcel.writeString(countryName)
    }

    override fun describeContents(): Int = 0

    override fun compareTo(other: LocationWeatherModel): Int {
        return if (name != other.name) name.compareTo(other.name)
        else countryCode.compareTo(other.countryCode)
    }

    companion object CREATOR : Parcelable.Creator<LocationWeatherModel> {
        override fun createFromParcel(parcel: Parcel): LocationWeatherModel = LocationWeatherModel(parcel)
        override fun newArray(size: Int): Array<LocationWeatherModel?> = arrayOfNulls(size)
    }
}