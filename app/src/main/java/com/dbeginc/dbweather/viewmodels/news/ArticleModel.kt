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
import java.util.regex.Pattern

/**
 * Created by darel on 06.10.17.
 *
 * Article Model
 */
data class ArticleModel(val author: String, val title: String, val description: String?,
                        val url: String, val urlToImage: String?, val publishedAt: String?,
                        val sourceId: String
) : Parcelable, Comparable<ArticleModel> {

    private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(author)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(url)
        parcel.writeString(urlToImage)
        parcel.writeString(publishedAt)
        parcel.writeString(sourceId)
    }

    override fun describeContents(): Int = 0

    override fun compareTo(other: ArticleModel): Int {
        val currentPublish = if (publishedAt == "...") (0).to(publishedAt)
        else {
            publishedAt?.split(regex=Pattern.compile("\\D"))
                    ?.first { it.isNotEmpty() }
                    ?.toInt()!!
                    .to(publishedAt.split(regex= Pattern.compile("\\d")).first { it.isNotEmpty() })
        }

        val otherPublish = if (other.publishedAt == "...") (0).to(other.publishedAt)
        else {
            other.publishedAt?.split(regex=Pattern.compile("\\D"))
                    ?.first { it.isNotEmpty() }
                    ?.toInt()!!
                    .to(other.publishedAt.split(regex= Pattern.compile("\\d")).first { it.isNotEmpty() })
        }

        return when {
            currentPublish == otherPublish-> return IS_EQUAL
            // Minutes Check
            currentPublish.second == "m" && otherPublish.second == "m" -> return currentPublish.first.compareTo(otherPublish.first)
            currentPublish.second == "m" -> return IS_LESS
            otherPublish.second == "m" -> return IS_GREATER
            // Hours check
            currentPublish.second == "h" && otherPublish.second == "h" -> return currentPublish.first.compareTo(otherPublish.first)
            currentPublish.second == "h" -> return IS_LESS
            otherPublish.second == "h" -> return IS_GREATER
            // No times check
            currentPublish.second == "..." -> return IS_LESS
            otherPublish.second == "..." -> return IS_GREATER
            // Days check
            else -> currentPublish.first.compareTo(otherPublish.first)
        }
    }

    companion object CREATOR : Parcelable.Creator<ArticleModel> {
        private val IS_EQUAL = 0
        private val IS_LESS = -1
        private val IS_GREATER = 1

        override fun createFromParcel(parcel: Parcel): ArticleModel = ArticleModel(parcel)
        override fun newArray(size: Int): Array<ArticleModel?> = arrayOfNulls(size)
    }

}