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

package com.dbeginc.dbweatherdata.implementations.repositories

import android.content.Context
import android.util.Log
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.CrashlyticsLogger
import com.dbeginc.dbweatherdata.RxThreadProvider
import com.dbeginc.dbweatherdata.TAG
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalNewsDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.local.news.RoomNewsDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteNewsDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.HttpApiNewsDataSource
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.NewsPaper
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticleRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticlesRequest
import com.dbeginc.dbweatherdomain.entities.requests.news.NewsPaperRequest
import com.dbeginc.dbweatherdomain.repositories.NewsRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 06.10.17.
 *
 * News Repository Implementation
 */
class NewsRepositoryImpl private constructor(private val threads: ThreadProvider,
                                             private val logger: Logger,
                                             private val localSource: LocalNewsDataSource,
                                             private val remoteSource: RemoteNewsDataSource) : NewsRepository {
    companion object {
        @JvmStatic
        fun create(context: Context): NewsRepository {
            return NewsRepositoryImpl(
                    threads = RxThreadProvider,
                    logger = CrashlyticsLogger,
                    localSource = RoomNewsDataSource.create(context),
                    remoteSource = HttpApiNewsDataSource.create(context)
            )
        }
    }

    private val subscriptions = CompositeDisposable()

    override fun getArticles(request: ArticlesRequest<String>): Flowable<List<Article>> {
        return localSource.getArticles(request)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remoteSource.getArticles(request)
                            .subscribeOn(threads.IO)
                            .subscribe(this::addArticles, logger::logError)
                }
    }

    override fun getTranslatedArticles(request: ArticlesRequest<String>): Flowable<List<Article>> {
        return localSource.getArticles(request)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remoteSource.getTranslatedArticles(request)
                            .subscribeOn(threads.IO)
                            .subscribe(this::addArticles, logger::logError)
                }
    }

    override fun getArticle(request: ArticleRequest<Unit>): Flowable<Article> {
        return localSource.getArticle(request)
                .subscribeOn(threads.CP)
    }

    override fun getAllNewsPapers(): Flowable<List<NewsPaper>> {
        return localSource.getNewsPapers()
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remoteSource.getNewsPapers()
                            .subscribeOn(threads.IO)
                            .flatMap { remoteResponse ->
                                localSource
                                        .getNewsPapers()
                                        .subscribeOn(threads.CP)
                                        .timeout(5, TimeUnit.SECONDS)
                                        .onErrorReturn { remoteResponse }
                                        .first(remoteResponse)
                                        .map { localResponse ->
                                            localResponse.forEach { localNews ->
                                                remoteResponse.find { it.name == localNews.name }?.subscribed = localNews.subscribed
                                            }
                                            return@map remoteResponse
                                        }
                            }
                            .subscribe(this::addNewsPapers, logger::logError)
                }
    }

    override fun getSubscribedNewsPapers(): Flowable<List<NewsPaper>> {
        return localSource.getSubscribedNewsPapers()
                .subscribeOn(threads.CP)
    }

    override fun subscribeToNewsPaper(request: NewsPaperRequest<NewsPaper>): Completable {
        return localSource.updateNewsPaper(request.arg)
                .subscribeOn(threads.CP)
    }

    override fun unSubscribeToNewsPaper(request: NewsPaperRequest<NewsPaper>): Completable {
        return localSource.updateNewsPaper(request.arg)
                .subscribeOn(threads.CP)
    }

    override fun getNewsPaper(request: NewsPaperRequest<Unit>): Flowable<NewsPaper> {
        return localSource.getNewsPaper(request.sourceId)
                .subscribeOn(threads.CP)
    }

    override fun defineDefaultSubscribedSources(sourcesId: List<String>): Completable {
        return remoteSource.getNewsPapers()
                .subscribeOn(threads.IO)
                .map { sources ->
                    sources.map { source ->
                        source.apply { if (sourcesId.contains(source.id)) subscribed = true }
                    }
                }
                .flatMapCompletable { sources ->
                    localSource.defineDefaultSubscribedNewsPapers(sources)
                            .subscribeOn(threads.CP)
                }
    }

    override fun clean() = subscriptions.clear()

    private fun addNewsPapers(newsPapers: List<NewsPaper>) {
        subscriptions.add(
                localSource.putNewsPapers(newsPapers)
                        .subscribeOn(threads.CP)
                        .subscribeWith(TaskObserver())
        )
    }

    private fun addArticles(articles: List<Article>) {
        subscriptions.add(
                localSource.putArticles(articles)
                        .subscribeOn(threads.CP)
                        .subscribeWith(TaskObserver())
        )
    }

    private inner class TaskObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Update of data done in ${NewsRepository::class.java.simpleName}")
            }
        }

        override fun onError(e: Throwable) = logger.logError(e)
    }
}
