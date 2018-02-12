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

import com.dbeginc.dbweathercommon.utils.LogDispatcher
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.entities.news.Source
import org.threeten.bp.Duration
import org.threeten.bp.Instant

/**
 * Created by darel on 06.10.17.
 *
 * News Mapper
 */
fun Article.toViewModel(unknownAuthor: String, currentTime: Instant) : ArticleModel {

    var publishTime: String = "..."

    try {
        if (publishedAt != null) {
            val articleAt = if (!publishedAt!!.toUpperCase().endsWith("Z")) {
                if (publishedAt!!.contains("+")) publishedAt?.substring(0, publishedAt!!.indexOf("+"))?.plus("Z") else publishedAt.plus("Z")

            } else publishedAt

            val duration = Duration.between(currentTime, Instant.parse(articleAt))

            publishTime = when {
                duration.days() > 0 -> "${duration.days()}d"
                duration.hours() > 0 -> "${duration.hours()}h"
                else -> "${duration.minutes()}m"
            }
        }
    } catch (error: Throwable) {
        LogDispatcher.logError(error)
    }

    return ArticleModel(author=author ?: unknownAuthor, title=title, description=description, url=url, urlToImage=urlToImage, publishedAt=publishTime, sourceId=sourceId)
}

fun Live.toViewModel(isFavorite: Boolean = false): LiveModel = LiveModel(name, url, isFavorite)

fun Source.toViewModel(): SourceModel = SourceModel(id, name, description, url, category, language, country, subscribed)

fun SourceModel.toDomain(): Source = Source(id, name, description, url, category, language, country, subscribed)

private fun Duration.days() : Int = if (isNegative) toDays().unaryMinus().toInt() else toDays().toInt()

private fun Duration.hours() : Int = if (isNegative) toHours().unaryMinus().toInt() else toHours().toInt()

private fun Duration.minutes() : Int = if (isNegative) toMinutes().unaryMinus().toInt() else toMinutes().toInt()