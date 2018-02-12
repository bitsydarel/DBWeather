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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.news

import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteNewsDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.retrofit.NewsRestAdapter
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteLive
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteSource
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.entities.news.Source
import io.reactivex.Flowable

/**
 * Created by darel on 04.10.17.
 *
 * Remote News Data Source Implementation
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class RemoteNewsDataSourceImpl private constructor(private val newsRestAdapter: NewsRestAdapter) : RemoteNewsDataSource {

    companion object {
        fun create(context: Context) : RemoteNewsDataSource{
            return RemoteNewsDataSourceImpl(NewsRestAdapter.create(context))
        }
    }

    override fun getSources(): Flowable<List<Source>> {
        return newsRestAdapter.getSources()
                .map { sources -> sources.map { source -> source.toDomain() } }
    }

    override fun getAllLives(): Flowable<List<Live>> = newsRestAdapter.getLives().map { lives -> lives.map { live -> live.toDomain() } }

    override fun getTranslatedArticles(sources: List<Source>): Flowable<List<Article>> {
        return newsRestAdapter.getTranslatedArticles(sources.map { source -> source.toProxy() })
                .map { articles -> articles.map { article -> article.toDomain() } }
    }

    override fun getArticles(sources: List<Source>): Flowable<List<Article>> {
        return newsRestAdapter.getArticles(sources.map { source -> source.toProxy() })
                .map { articles -> articles.map { article -> article.toDomain() } }
    }

    private fun RemoteSource.toDomain() = Source(id=id, name=name, description=description, url=url, category=category, language=language, country=country, subscribed=false)

    private fun Source.toProxy() : RemoteSource = RemoteSource(id=id, name=name, description=description, url=url, category=category, language=language, country=country)

    private fun RemoteLive.toDomain() = Live(name=name, url=url)
}