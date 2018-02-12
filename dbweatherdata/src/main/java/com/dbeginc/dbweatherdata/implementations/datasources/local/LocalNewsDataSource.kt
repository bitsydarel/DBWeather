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

package com.dbeginc.dbweatherdata.implementations.datasources.local

import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.entities.news.Source
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by darel on 04.10.17.
 *
 * Local News Data Source
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface LocalNewsDataSource {
    fun getArticles(request: NewsRequest<Unit>): Flowable<List<Article>>

    fun getArticle(request: ArticleRequest<Unit>) : Single<Article>

    fun putArticles(articles: List<Article>): Completable

    fun getSources(): Flowable<List<Source>>

    fun getSubscribedSources() : Maybe<List<Source>>

    fun getSource(id: String) : Single<Source>

    fun updateSource(source: Source): Completable

    fun putSources(sources: List<Source>) : Completable

    fun getAllLives(): Flowable<List<Live>>

    fun getLives(names: List<String>) : Maybe<List<Live>>

    fun getFavoriteLives(): Maybe<List<String>>

    fun getLive(name: String) : Single<Live>

    fun addLiveToFavorite(request: LiveRequest<Unit>): Completable

    fun removeLiveFromFavorite(request: LiveRequest<Unit>): Completable

    fun putLives(lives: List<Live>): Completable

    fun defineDefaultSubscribedSources(sources: List<Source>): Completable
}