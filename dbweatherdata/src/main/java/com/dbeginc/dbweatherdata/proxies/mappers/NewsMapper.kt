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

package com.dbeginc.dbweatherdata.proxies.mappers

import com.dbeginc.dbweatherdata.proxies.local.news.LocalArticle
import com.dbeginc.dbweatherdata.proxies.local.news.LocalLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalSource
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteArticle
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.entities.news.Source

/**
 * Created by darel on 04.10.17.
 *
 * Mapper of data proxies to domain entities
 */
fun LocalArticle.toDomain() = Article(sourceId=sourceId, author=author, title=title, description=description, url=url, urlToImage=urlToImage, publishedAt=publishedAt)

fun LocalSource.toDomain() = Source(id=id, name=name, description=description, url=url, category=category, language=language, country=country, subscribed=subscribed)

fun LocalLive.toDomain() = Live(name=name, url=url)

fun RemoteArticle.toDomain() = Article(author=author, title=title, description=description, url=url, urlToImage=urlToImage, publishedAt=publishedAt, sourceId=source.name)
