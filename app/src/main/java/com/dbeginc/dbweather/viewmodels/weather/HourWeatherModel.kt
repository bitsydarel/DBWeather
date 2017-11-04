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
import com.dbeginc.dbweather.utils.utility.toDate

/**
 * Created by darel on 19.09.17.
 *
 * Hourly Weather View Model
 */
data class HourWeatherModel(val hourlyTime: String, val time: Long, val icon: Int, val temperature: Int, val temperatureUnit: String)
    : Parcelable, Comparable<HourWeatherModel> {
    private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeString(hourlyTime)
        parcel.writeLong(time)
        parcel.writeInt(icon)
        parcel.writeInt(temperature)
        parcel.writeString(hourlyTime)
    }

    override fun describeContents(): Int = 0

    override fun compareTo(other: HourWeatherModel): Int {
        return time.toDate().compareTo(other.time.toDate())
    }

    companion object CREATOR : Parcelable.Creator<HourWeatherModel> {
        override fun createFromParcel(parcel: Parcel): HourWeatherModel = HourWeatherModel(parcel)
        override fun newArray(size: Int): Array<HourWeatherModel?> = arrayOfNulls(size)
    }
}