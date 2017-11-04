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

package com.dbeginc.dbweatherdomain.repositories.news

import com.dbeginc.dbweatherdomain.entities.news.*
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.LiveRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.SourceRequest
import com.dbeginc.dbweatherdomain.repositories.Cleanable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by darel on 15.09.17.
 *
 * NewsPaper Repository
 */
interface NewsRepository : Cleanable {
    fun getArticles(request: NewsRequest<Unit>) : Flowable<List<Article>>
    fun getTranslatedArticles(request: NewsRequest<Unit>) : Flowable<List<Article>>
    fun getArticle(request: ArticleRequest<Unit>) : Single<Article>

    fun getSources() : Flowable<List<Source>>
    fun getSubscribedSources() : Flowable<List<Source>>
    fun getSource(id: String) : Single<Source>
    fun subscribeToSource(request: SourceRequest<Source>) : Completable
    fun unSubscribeToSource(request: SourceRequest<Source>) : Completable
    fun defineDefaultSubscribedSources(sourcesId: List<String>): Completable

    fun getAllLives() : Flowable<List<Live>>
    fun getLives(names: List<String>) : Flowable<List<Live>>
    fun getLive(name: String) : Single<Live>
    fun addLiveToFavorite(request: LiveRequest<Unit>): Completable
    fun removeLiveFromFavorite(request: LiveRequest<Unit>): Completable
    fun getFavoriteLives(): Flowable<List<String>>
}