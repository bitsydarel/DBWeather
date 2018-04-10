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

package com.dbeginc.dbweatherdata.implementations.datasources.local

import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.NewsPaper
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticlesRequest
import io.reactivex.Completable
import io.reactivex.Flowable

/**
 * Created by darel on 04.10.17.
 *
 * Local News Data NewsPaper
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface LocalNewsDataSource {
    fun getArticles(request: ArticlesRequest<String>): Flowable<List<Article>>

    fun getArticle(request: ArticleRequest<Unit>): Flowable<Article>

    fun putArticles(articles: List<Article>): Completable

    fun getNewsPapers(): Flowable<List<NewsPaper>>

    fun getSubscribedNewsPapers(): Flowable<List<NewsPaper>>

    fun getNewsPaper(name: String): Flowable<NewsPaper>

    fun updateNewsPaper(newsPaper: NewsPaper): Completable

    fun putNewsPapers(newsPapers: List<NewsPaper>): Completable

    fun defineDefaultSubscribedNewsPapers(newsPapers: List<NewsPaper>): Completable
}