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

import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.Live
import com.dbeginc.dbweatherdomain.entities.news.Source
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
 * News Repository
 *
 * Interface following repository pattern.
 *
 * Each method in this interface are threaded as Use Cases (Interactor in terms of Clean Architecture).
 */
interface NewsRepository : Cleanable {

    /**
     * Created by darel on 06.10.17.
     *
     * Get Articles
     *
     * @param request containing information about from which sources to retrieve articles
     *
     * @return [Flowable] reactive stream of list of articles that provide list of articles requested
     */
    fun getArticles(request: NewsRequest<Unit>) : Flowable<List<Article>>


    /**
     * Created by darel on 06.10.17.
     *
     * Get translated articles
     *
     * @param request containing information about from which sources to retrieve articles
     *
     * @return [Flowable] reactive stream of list of articles that notify new data and has back pressure enable
     */
    fun getTranslatedArticles(request: NewsRequest<Unit>) : Flowable<List<Article>>


    /**
     * Created by darel on 08.11.17.
     *
     * Get Article
     *
     * @param request containing information about the article to retrieve
     *
     * @return [Single] reactive stream of article that provide an article or an error
     */
    fun getArticle(request: ArticleRequest<Unit>) : Single<Article>


    /**
     * Created by darel on 28.10.17.
     *
     * Get all sources available
     *
     * @return [Flowable] reactive stream of list of source that provide all the sources available
     */
    fun getAllSources(): Flowable<List<Source>>


    /**
     * Created by darel on 06.10.17.
     *
     * get subscribed sources
     *
     * @return [Flowable] reactive stream of list of source that provide list of sources that were subscribed
     */
    fun getSubscribedSources() : Flowable<List<Source>>


    /**
     * Created by darel on 18.11.17.
     *
     * Get Source
     *
     * @param request containing information about the source to retrieve
     *
     * @return [Single] reactive stream of source that provide a source or an error
     */
    fun getSource(request: SourceRequest<Unit>) : Single<Source>


    /**
     * Created by darel on 28.10.17.
     *
     * subscribe to a source
     *
     * @param request containing information about the source to subscribe
     *
     * @return [Completable] reactive stream that notify completion of the task
     */
    fun subscribeToSource(request: SourceRequest<Source>) : Completable


    /**
     * Created by darel on 28.10.17.
     *
     * UnSubscribe To Source
     *
     * @param request containing information about the source to unSubscribe
     *
     * @return [Completable] reactive stream that notify completion of the task
     */
    fun unSubscribeToSource(request: SourceRequest<Source>) : Completable


    /**
     * Created by darel on 07.10.17.
     *
     * Define default subscribed sources
     *
     * @param sourcesId list of source ids to subscribe to
     * @return [Completable] reactive stream that notify completion of the task
     */
    fun defineDefaultSubscribedSources(sourcesId: List<String>): Completable


    /**
     * Created by darel on 18.10.17.
     *
     * Get All Lives Stream available
     *
     * @return [Flowable] reactive stream of list of lives that provide currently available lives
     */
    fun getAllLives() : Flowable<List<Live>>


    /**
     * Created by darel on 20.10.17.
     *
     * Get Lives
     *
     * @param names of lives to retrieve
     *
     * @return [Flowable] reactive stream of list of lives that provide requested lives
     */
    fun getLives(names: List<String>) : Flowable<List<Live>>


    /**
     * Created by darel on 21.10.17.
     *
     * Get Live
     *
     * @param name of the live to retrieve
     *
     * @return [Single] reactive stream of live that provide an live or an error
     */
    fun getLive(name: String) : Single<Live>


    /**
     * Created by darel on 19.10.17.
     *
     * Get Favorite Lives
     *
     * @return [Flowable] reactive stream of list of string that provide name of favorite lives
     */
    fun getFavoriteLives(): Flowable<List<String>>


    /**
     * Created by darel on 19.10.17.
     *
     * Add Live to favorites use case
     *
     * @param request containing information about the live to add to favorites
     * @return [Completable] reactive stream that notify his completion
     */
    fun addLiveToFavorites(request: LiveRequest<Unit>): Completable


    /**
     * Created by darel on 20.10.17.
     *
     * Remove Live from favorites
     *
     * @param request containing information about the live to remove from favorites
     * @return [Completable] reactive stream that notify his completion
     */
    fun removeLiveFromFavorites(request: LiveRequest<Unit>): Completable
}