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
 * Created by darel on 19.09.17.
 *
 * Weather Model
 */
data class WeatherModel(val location: LocationWeatherModel, val current: CurrentWeatherModel, val hourly: List<HourWeatherModel>,
                        val daily: List<DayWeatherModel>, val alerts: List<AlertWeatherModel>?
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(LocationWeatherModel::class.java.classLoader),
            parcel.readParcelable(CurrentWeatherModel::class.java.classLoader),
            parcel.createTypedArrayList(HourWeatherModel),
            parcel.createTypedArrayList(DayWeatherModel),
            parcel.createTypedArrayList(AlertWeatherModel)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(location, flags)
        parcel.writeParcelable(current, flags)
        parcel.writeTypedList(hourly)
        parcel.writeTypedList(daily)
        parcel.writeTypedList(alerts)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<WeatherModel> {
        override fun createFromParcel(parcel: Parcel): WeatherModel = WeatherModel(parcel)
        override fun newArray(size: Int): Array<WeatherModel?> = arrayOfNulls(size)
    }
}