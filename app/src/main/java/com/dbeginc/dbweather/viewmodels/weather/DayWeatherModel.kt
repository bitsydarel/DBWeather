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
import java.util.*

/**
 * Created by darel on 19.09.17.
 *
 *
 */
data class DayWeatherModel(val dayName: String, val time: Long,
                           val summary: String, val icon: Int,
                           val temperatureUnit: String,
                           val temperatureHigh: Int, val temperatureHighTime: Long,
                           val temperatureLow: Int, val temperatureLowTime: Long,
                           val apparentTemperatureHigh: Int, val apparentTemperatureLow: Int,
                           val dewPoint: Double, val humidity: String,
                           val pressure: Double, val windSpeed: String,
                           val windGust: Double, val windGustTime: Long,
                           val windBearing: Long, val moonPhase: Double,
                           val uvIndex: Long, val uvIndexTime: Long,
                           val sunsetTime: String, val sunriseTime: String,
                           val precipIntensity: Double, val precipIntensityMax: Double,
                           val precipProbability: Double, val precipType: String?
) : Parcelable, Comparable<DayWeatherModel> {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readDouble(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(dayName)
        parcel.writeLong(time)
        parcel.writeString(summary)
        parcel.writeInt(icon)
        parcel.writeString(temperatureUnit)
        parcel.writeInt(temperatureHigh)
        parcel.writeLong(temperatureHighTime)
        parcel.writeInt(temperatureLow)
        parcel.writeLong(temperatureLowTime)
        parcel.writeInt(apparentTemperatureHigh)
        parcel.writeInt(apparentTemperatureLow)
        parcel.writeDouble(dewPoint)
        parcel.writeString(humidity)
        parcel.writeDouble(pressure)
        parcel.writeString(windSpeed)
        parcel.writeDouble(windGust)
        parcel.writeLong(windGustTime)
        parcel.writeLong(windBearing)
        parcel.writeDouble(moonPhase)
        parcel.writeLong(uvIndex)
        parcel.writeLong(uvIndexTime)
        parcel.writeString(sunsetTime)
        parcel.writeString(sunriseTime)
        parcel.writeDouble(precipIntensity)
        parcel.writeDouble(precipIntensityMax)
        parcel.writeDouble(precipProbability)
        parcel.writeString(precipType)
    }

    override fun describeContents(): Int = 0

    override fun compareTo(other: DayWeatherModel): Int = time.toDate().compareTo(other.time.toDate())

    companion object CREATOR : Parcelable.Creator<DayWeatherModel> {
        override fun createFromParcel(parcel: Parcel): DayWeatherModel = DayWeatherModel(parcel)
        override fun newArray(size: Int): Array<DayWeatherModel?> = arrayOfNulls(size)
    }
}