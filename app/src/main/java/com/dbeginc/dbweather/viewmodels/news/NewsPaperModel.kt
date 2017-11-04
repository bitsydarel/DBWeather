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
import android.support.annotation.DrawableRes
import com.dbeginc.dbweather.news.UpdatableModel

/**
 * Created by darel on 08.10.17.
 *
 * Parent Group
 */
data class NewsPaperModel(var name: String, @DrawableRes val logo: Int=0, val children: List<ArticleModel> = mutableListOf()) : Parcelable, UpdatableModel {
    private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.createTypedArrayList(ArticleModel)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(logo)
        parcel.writeTypedList(children)
    }

    override fun describeContents(): Int = 0

    override fun getId() = name

    companion object CREATOR : Parcelable.Creator<NewsPaperModel> {
        override fun createFromParcel(parcel: Parcel): NewsPaperModel = NewsPaperModel(parcel)
        override fun newArray(size: Int): Array<NewsPaperModel?> = arrayOfNulls(size)
    }
}