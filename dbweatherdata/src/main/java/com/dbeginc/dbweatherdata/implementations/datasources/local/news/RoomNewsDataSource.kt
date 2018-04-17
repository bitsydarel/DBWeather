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

package com.dbeginc.dbweatherdata.implementations.datasources.local.news

import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalNewsDataSource
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.mappers.toProxy
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.NewsPaper
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticlesRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

/**
 * Created by darel on 04.10.17.
 *
 * Local News Data NewsPaper Implementation
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class RoomNewsDataSource private constructor(private val db: RoomNewsDatabase) : LocalNewsDataSource {

    companion object {
        @JvmStatic
        fun create(context: Context): LocalNewsDataSource {
            return RoomNewsDataSource(RoomNewsDatabase.createDb(context))
        }
    }

    override fun getArticles(request: ArticlesRequest<String>): Flowable<List<Article>> {
        return db.newsDao()
                .getArticles(newsPaperId = request.newsPaperId, newsPaperName = request.arg)
                .map { articles -> articles.map { article -> article.toDomain() } }
    }

    override fun getArticle(request: ArticleRequest<Unit>): Flowable<Article> {
        return db.newsDao()
                .getArticle(newsPaperId = request.newsPaperId, articleUrl = request.url)
                .map { article -> article.toDomain() }
    }

    override fun putArticles(articles: List<Article>): Completable =
            Completable.fromAction {
                db.newsDao().putArticles(articles.map { article -> article.toProxy() })
            }

    override fun getNewsPapers(): Flowable<List<NewsPaper>> {
        return db.newsDao().getNewsPapers()
                .map { newsPapers -> newsPapers.map { it.toDomain() } }
    }

    override fun defineDefaultSubscribedNewsPapers(newsPapers: List<NewsPaper>): Completable {
        return Completable.fromCallable {
            db.newsDao().defineDefaultNewsPapers(newsPapers.map { it.toProxy() })
        }
    }

    override fun getSubscribedNewsPapers(): Flowable<List<NewsPaper>> {
        return db.newsDao().getSubscribedNewsPapers()
                .map { newsPapers -> newsPapers.map { it.toDomain() } }
    }

    override fun getNewsPaper(name: String): Flowable<NewsPaper> =
            db.newsDao().getNewsPaper(name).map { source -> source.toDomain() }

    override fun findNewspaper(name: String): Maybe<List<NewsPaper>> =
            db.newsDao().findNewsPaperByName(name).map { newsPapers -> newsPapers.map { it.toDomain() } }

    override fun updateNewsPaper(newsPaper: NewsPaper): Completable =
            Completable.fromAction { db.newsDao().updateNewsPaper(newsPaper.toProxy()) }

    override fun putNewsPapers(newsPapers: List<NewsPaper>): Completable =
            Completable.fromAction {
                db.newsDao().putNewsPapers(
                        newsPapers.map { source -> source.toProxy() }
                )
            }

}