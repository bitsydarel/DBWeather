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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.news.retrofit

import android.content.Context
import android.support.annotation.RestrictTo
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.ConstantHolder
import com.dbeginc.dbweatherdata.ConstantHolder.CACHE_SIZE
import com.dbeginc.dbweatherdata.ConstantHolder.NETWORK_CACHE_NAME
import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.translator.GoogleTranslate
import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.translator.Translator
import com.dbeginc.dbweatherdata.proxies.remote.news.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.rx2androidnetworking.Rx2ANRequest
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.jetbrains.annotations.TestOnly
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 04.10.17.
 *
 * News Rest Adapter
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class NewsRestAdapter private constructor(private val liveApi: DatabaseReference, private val translator: Translator) {
    private val deviceLanguage by lazy { Locale.getDefault().language }

    companion object {
        fun create(context: Context): NewsRestAdapter {
            val client = OkHttpClient.Builder()
                    .connectTimeout(35, TimeUnit.SECONDS)
                    .writeTimeout(35, TimeUnit.SECONDS)
                    .readTimeout(55, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .cache(Cache(File(context.cacheDir, NETWORK_CACHE_NAME), CACHE_SIZE))

            AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BASIC)
            AndroidNetworking.initialize(context, client.build())

            val liveApi = FirebaseDatabase.getInstance(FirebaseApp.initializeApp(context)).reference.child(ConstantHolder.LIVE_SOURCE_REFERENCE)

            return NewsRestAdapter(liveApi, GoogleTranslate())
        }

        @TestOnly
        @RestrictTo(RestrictTo.Scope.TESTS)
        fun create(liveApi: DatabaseReference, translator: Translator): NewsRestAdapter {
            return NewsRestAdapter(liveApi, translator)
        }
    }

    fun getTranslatedArticles(sources: List<RemoteSource>) : Flowable<List<RemoteArticle>> {
        return getArticles(sources)
                .map {
                    articles -> articles.map { article ->
                    article.apply {
                        title = title.translate()
                        description = description?.translate()
                    }
                } }
    }

    fun getArticles(sources: List<RemoteSource>) : Flowable<List<RemoteArticle>> {
        return Flowable.fromIterable(sources)
                .buffer(5)
                .map { fiveSources -> fiveSources.joinToString(separator = ",", transform = { (id) -> id }) }
                .flatMap({ joinedSources -> articlesRequest(joinedSources).build().getObjectFlowable(RemoteNewsResponse::class.java) }, true)
                .map { response -> response.articles }
                .collect({ mutableListOf<RemoteArticle>() }, { container, articles -> container.addAll(articles) })
                .flatMapPublisher { articles -> Flowable.just(articles.toList()) }
    }

    fun getSources() : Flowable<List<RemoteSource>> {
        return sourceRequest()
                .build()
                .getObjectFlowable(RemoteSourceResponse::class.java)
                .map { response -> response.sources }
    }

    fun getLives(): Flowable<List<RemoteLive>> {
        return Flowable.create({ emitter ->
            liveApi.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) = emitter.onError(databaseError.toException())

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val lives: List<RemoteLive>? = (dataSnapshot.value as? Map<String, String>)
                            ?.map { (key, value) -> RemoteLive(key, value) }

                    if (lives != null) emitter.onNext(lives)
                    emitter.onComplete()
                }
            })
        }, BackpressureStrategy.LATEST)
    }

    private fun String.translate(language: String = deviceLanguage) : String = translator.translate(this, language)

    private fun sourceRequest() = Rx2AndroidNetworking.get("https://newsapi.org/v2/sources").addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)

    private fun articlesRequest(sources: String): Rx2ANRequest.GetRequestBuilder {
        return Rx2AndroidNetworking.get("https://newsapi.org/v2/top-headlines")
                .addQueryParameter("sources", sources)
                .addQueryParameter("apiKey", BuildConfig.NEWS_API_KEY)
    }

}