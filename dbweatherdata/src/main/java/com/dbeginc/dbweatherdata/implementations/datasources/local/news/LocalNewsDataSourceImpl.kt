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

package com.dbeginc.dbweatherdata.implementations.datasources.local.news

import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalNewsDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.local.news.room.LocalNewsDatabase
import com.dbeginc.dbweatherdata.proxies.local.news.LocalArticle
import com.dbeginc.dbweatherdata.proxies.local.news.LocalFavoriteLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalSource
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.entities.news.Source
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsRequest
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by darel on 04.10.17.
 *
 * Local News Data Source Implementation
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class LocalNewsDataSourceImpl private constructor(private val db: LocalNewsDatabase) : LocalNewsDataSource {

    companion object {
        fun create(context: Context): LocalNewsDataSource {
            return LocalNewsDataSourceImpl(LocalNewsDatabase.createDb(context))
        }
    }

    override fun getArticles(request: NewsRequest<Unit>): Maybe<List<Article>> {
        return db.newsDao().getArticles(request.sources.map { source -> source.id })
                .map { articles -> articles.map { article -> article.toDomain()  } }
    }

    override fun getArticle(request: ArticleRequest<Unit>): Single<Article> {
        return db.newsDao().getArticle(request.sourceId, request.url)
                .map { article -> article.toDomain() }
    }

    override fun putArticles(articles: List<Article>): Completable = Completable.fromAction { db.newsDao().putArticles(articles.map { article -> article.toProxy() }) }

    override fun getSources(): Maybe<List<Source>> {
        return db.newsDao().getSources()
                .map { sources -> sources.map { source -> source.toDomain() } }
    }

    override fun getSubscribedSources(): Maybe<List<Source>> {
        return db.newsDao().getSubscribedSources()
                .map { sources -> sources.map { source -> source.toDomain() } }
    }

    override fun getSource(id: String): Single<Source> = db.newsDao().getSource(id).map { source -> source.toDomain() }

    override fun updateSource(source: Source): Completable = Completable.fromAction { db.newsDao().updateSource(source.toProxy()) }

    override fun defineDefaultSubscribedSources(sources: List<Source>): Completable {
        return Completable.fromAction { db.newsDao().putSources(sources.map { source -> source.toProxy() }) }
    }

    override fun putSources(sources: List<Source>): Completable = Completable.fromAction { db.newsDao().putSources(sources.map { source -> source.toProxy() }) }

    override fun getAllLives(): Maybe<List<Live>> = db.newsDao().getAllLives().map { lives -> lives.map { live -> live.toDomain() } }

    override fun getLives(names: List<String>): Maybe<List<Live>> = db.newsDao().getLives(names).map { lives -> lives.map { live -> live.toDomain() } }

    override fun getLive(name: String): Single<Live> = db.newsDao().getLive(name).map { live -> live.toDomain() }

    override fun getFavoriteLives(): Maybe<List<String>> = db.newsDao().getFavoriteLives().map { lives -> lives.map { live -> live.live_id } }

    override fun addLiveToFavorite(request: LiveRequest<Unit>): Completable = Completable.fromAction { db.newsDao().addToFavorites(LocalFavoriteLive(request.name, request.name)) }

    override fun removeLiveFromFavorite(request: LiveRequest<Unit>): Completable = Completable.fromAction { db.newsDao().removeFromFavorites(LocalFavoriteLive(request.name, request.name)) }

    override fun putLives(lives: List<Live>): Completable = Completable.fromAction { db.newsDao().putLives(lives.map { live -> live.toProxy() }) }

    private fun Article.toProxy() : LocalArticle = LocalArticle(author, title, description, url, urlToImage, publishedAt, sourceId)

    private fun Live.toProxy() : LocalLive = LocalLive(name=name, url=url)

    private fun Source.toProxy() : LocalSource = LocalSource(id=id, name=name, description=description, url=url, category=category, language=language, country=country, sortBysAvailable=sortBysAvailable, subscribed=subscribed)

}