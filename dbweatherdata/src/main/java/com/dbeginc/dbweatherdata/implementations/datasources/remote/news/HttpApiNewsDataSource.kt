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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.news

import android.content.Context
import android.support.annotation.RestrictTo
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.DEFAULT_NETWORK_CACHE_SIZE
import com.dbeginc.dbweatherdata.NETWORK_CACHE_NAME
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteNewsDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.translator.Translator
import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.translator.YandexTranslator
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteArticle
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteNewsResponse
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteSourceResponse
import com.dbeginc.dbweatherdomain.entities.news.Article
import com.dbeginc.dbweatherdomain.entities.news.NewsPaper
import com.dbeginc.dbweatherdomain.entities.requests.news.ArticlesRequest
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 04.10.17.
 *
 * Remote News Data NewsPaper Implementation
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class HttpApiNewsDataSource private constructor(private val translator: Translator) : RemoteNewsDataSource {
    private val deviceLanguage by lazy { Locale.getDefault().language }

    companion object {
        fun create(context: Context): RemoteNewsDataSource {
            val client = OkHttpClient.Builder()
                    .connectTimeout(35, TimeUnit.SECONDS)
                    .writeTimeout(35, TimeUnit.SECONDS)
                    .readTimeout(55, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .cache(Cache(File(context.cacheDir, NETWORK_CACHE_NAME), DEFAULT_NETWORK_CACHE_SIZE))

            AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BASIC)

            AndroidNetworking.initialize(context, client.build())

            return HttpApiNewsDataSource(translator = YandexTranslator())
        }
    }

    override fun getNewsPapers(): Single<List<NewsPaper>> {
        return Rx2AndroidNetworking
                .get("https://newsapi.org/v2/sources")
                .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
                .build()
                .getObjectSingle(RemoteSourceResponse::class.java)
                .map { response -> response.sources }
                .map { sources -> sources.map { source -> source.toDomain() } }
    }

    override fun getTranslatedArticles(request: ArticlesRequest<String>): Single<List<Article>> {
        return getArticlesFromApi(newsPaperId = request.newsPaperId)
                .flattenAsObservable { it }
                .flatMapSingle { originalArticle ->
                    translator.translate(text = originalArticle.title, language = deviceLanguage)
                            .map { translation -> originalArticle.apply { title = translation } }
                            .flatMap { mapperArticle ->
                                mapperArticle.description
                                        ?.let {
                                            translator.translate(text = it, language = deviceLanguage)
                                                    .map { translation -> originalArticle.apply { description = translation } }
                                        } ?: Single.just(mapperArticle)
                            }
                }
                .toList()
                .map { articles -> articles.map { article -> article.toDomain() } }
    }

    override fun getArticles(request: ArticlesRequest<String>): Single<List<Article>> {
        return getArticlesFromApi(newsPaperId = request.newsPaperId)
                .map { articles -> articles.map { article -> article.toDomain() } }
    }

    private fun getArticlesFromApi(newsPaperId: String): Single<List<RemoteArticle>> {
        return Rx2AndroidNetworking.get("https://newsapi.org/v2/top-headlines")
                .addQueryParameter("sources", newsPaperId)
                .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
                .build()
                .getObjectSingle(RemoteNewsResponse::class.java)
                .map { response -> response.articles }
    }

}