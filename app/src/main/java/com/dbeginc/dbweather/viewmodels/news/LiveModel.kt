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

package com.dbeginc.dbweather.viewmodels.news

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by darel on 18.10.17.
 *
 * Live Model
 */
data class LiveModel(val name: String, val url: String, var isFavorite: Boolean) : Parcelable, Comparable<LiveModel> {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int = 0

    override fun compareTo(other: LiveModel): Int = name.compareTo(other.name)

    companion object CREATOR : Parcelable.Creator<LiveModel> {
        override fun createFromParcel(parcel: Parcel): LiveModel {
            return LiveModel(parcel)
        }

        override fun newArray(size: Int): Array<LiveModel?> {
            return arrayOfNulls(size)
        }
    }
}