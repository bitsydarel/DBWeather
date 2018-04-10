/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.proxies.mappers

import com.dbeginc.dbweatherdata.proxies.local.news.LocalArticle
import com.dbeginc.dbweatherdata.proxies.local.news.LocalNewsPaper
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteArticle
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteSource
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.NewsPaper
import org.threeten.bp.ZonedDateTime

/**
 * Created by darel on 04.10.17.
 *
 * Mapper of data proxies to domain entities
 */
internal fun LocalArticle.toDomain() = Article(
        sourceId = newsPaperId,
        author = author,
        title = title,
        description = description,
        url = url,
        imageUrl = urlToImage,
        publishedAt = publishedAt
)

internal fun LocalNewsPaper.toDomain() = NewsPaper(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country,
        subscribed = subscribed
)

internal fun RemoteArticle.toDomain(): Article {
    return Article(
            author = author,
            title = title,
            description = description,
            url = url,
            imageUrl = urlToImage,
            publishedAt = if (publishedAt == null) 0 else ZonedDateTime.parse(publishedAt).toInstant().toEpochMilli(),
            sourceId = source.name ?: source.id!!
    )
}

internal fun Article.toProxy(): LocalArticle = LocalArticle(
        newsPaperId = sourceId,
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = imageUrl,
        publishedAt = publishedAt
)

internal fun RemoteSource.toDomain() = NewsPaper(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country,
        subscribed = false
)

internal fun NewsPaper.toProxy(): LocalNewsPaper = LocalNewsPaper(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country,
        subscribed = subscribed
)