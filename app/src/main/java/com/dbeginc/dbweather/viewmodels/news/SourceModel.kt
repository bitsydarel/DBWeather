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
 * Created by darel on 27.10.17.
 *
 * Source Model
 */
data class SourceModel(val id: String, val name: String, val description: String,
                       val url: String, val category: String, val language: String,
                       val country: String, val sortBysAvailable: List<String>,
                       var subscribed: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList(),
            parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(url)
        parcel.writeString(category)
        parcel.writeString(language)
        parcel.writeString(country)
        parcel.writeStringList(sortBysAvailable)
        parcel.writeByte(if (subscribed) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<SourceModel> {
        override fun createFromParcel(parcel: Parcel): SourceModel = SourceModel(parcel)
        override fun newArray(size: Int): Array<SourceModel?> = arrayOfNulls(size)
    }
}