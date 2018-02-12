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

package com.dbeginc.dbweathernews.viewmodels

import android.os.Parcelable
import android.support.annotation.VisibleForTesting
import kotlinx.android.parcel.Parcelize
import java.util.regex.Pattern

/**
 * Created by darel on 06.10.17.
 *
 * Article Model
 */
@Parcelize
data class ArticleModel(val author: String, val title: String, val description: String?,
                        val url: String, val urlToImage: String?, val publishedAt: String,
                        val sourceId: String
) : Parcelable, Comparable<ArticleModel> {

    override fun compareTo(other: ArticleModel): Int {
        val currentPublishTime: Pair<Short, String> = if ("..." == publishedAt) (0).toShort() to publishedAt else extractPublishTime(publishedAt)

        val otherPublishTime: Pair<Short, String> = if (other.publishedAt == "...") (0).toShort() to other.publishedAt else extractPublishTime(other.publishedAt)

        return when {
            currentPublishTime == otherPublishTime -> return IS_EQUAL
            // Minutes Check
            currentPublishTime.second == "m" && otherPublishTime.second == "m" -> return currentPublishTime.first.compareTo(otherPublishTime.first)
            currentPublishTime.second == "m" -> return IS_LESS
            otherPublishTime.second == "m" -> return IS_GREATER
            // Hours check
            currentPublishTime.second == "h" && otherPublishTime.second == "h" -> return currentPublishTime.first.compareTo(otherPublishTime.first)
            currentPublishTime.second == "h" -> return IS_LESS
            otherPublishTime.second == "h" -> return IS_GREATER
            // No times check
            currentPublishTime.second == "..." -> return IS_LESS
            otherPublishTime.second == "..." -> return IS_GREATER
            // Days check
            else -> currentPublishTime.first.compareTo(otherPublishTime.first)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun extractPublishTime(publishTime: String): Pair<Short, String> = publishTime.numericalPart() to publishTime.letterPart()

    private fun String.numericalPart(): Short = this.split(regex = Pattern.compile("\\D")).first { it.isNotEmpty() }.toShort()

    private fun String.letterPart(): String = this.split(regex = Pattern.compile("\\d")).first { it.isNotEmpty() }

    companion object {
        private const val IS_EQUAL = 0
        private const val IS_LESS = -1
        private const val IS_GREATER = 1
    }
}