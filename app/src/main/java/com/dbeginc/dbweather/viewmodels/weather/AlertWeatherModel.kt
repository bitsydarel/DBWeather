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
 * Weather Alert Model
 */
data class AlertWeatherModel(val time: String, val title: String,
                             val description: String, val uri: String,
                             val expires: String, val severity: String,
                             val regions: List<String>
) : Parcelable {
    private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(uri)
        parcel.writeString(expires)
        parcel.writeString(severity)
        parcel.writeStringList(regions)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<AlertWeatherModel> {
        override fun createFromParcel(parcel: Parcel): AlertWeatherModel = AlertWeatherModel(parcel)
        override fun newArray(size: Int): Array<AlertWeatherModel?> = arrayOfNulls(size)
    }
}