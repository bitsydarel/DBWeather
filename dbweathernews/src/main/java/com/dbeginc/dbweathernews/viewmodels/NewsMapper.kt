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

package com.dbeginc.dbweathernews.viewmodels

import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.NewsPaper

/**
 * Created by darel on 06.10.17.
 *
 * News Mapper
 */
fun Article.toUi(): ArticleModel = ArticleModel(
        author = author,
        title = title,
        description = description,
        url = url,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        sourceId = sourceId
)

fun NewsPaper.toUi(): NewsPaperModel = NewsPaperModel(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country,
        subscribed = subscribed
)

fun NewsPaperModel.toDomain(): NewsPaper = NewsPaper(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        language = language,
        country = country,
        subscribed = subscribed
)