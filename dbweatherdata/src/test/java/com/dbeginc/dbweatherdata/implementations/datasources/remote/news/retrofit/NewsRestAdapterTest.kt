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

import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.translator.Translator
import com.dbeginc.dbweatherdata.proxies.remote.news.RemoteSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 04.10.17.
 *
 * News Rest Adapter Test
 */
@RunWith(MockitoJUnitRunner::class)
class NewsRestAdapterTest {
    private lateinit var adapter: NewsRestAdapter
    @Mock
    lateinit var liveDB: DatabaseReference
    @Mock
    lateinit var translator: Translator
    private lateinit var sources: List<RemoteSource>
    private lateinit var livesSnapshot: DataSnapshot

    @Before
    fun setUp() {
        val client = OkHttpClient.Builder()
                .connectTimeout(35, TimeUnit.SECONDS)
                .writeTimeout(35, TimeUnit.SECONDS)
                .readTimeout(55, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()

        adapter = NewsRestAdapter.create(client, liveDB, translator)

        sources = adapter.getSources().blockingFirst()

        livesSnapshot = Mockito.mock(DataSnapshot::class.java)

        /*val mockServer = MockWebServer()

        mockServer.start()

        NewsRestAdapter.NEWS_API_URL = mockServer.url("/").toString()

        val mockedAdapter = NewsRestAdapter.create(OkHttpClient(), liveDB, translator)

        val responseSource = MockResponse().apply {
            setResponseCode(200)
            addHeader("Cache-Control", "no-cache")
            addHeader("Content-Length", "22386")
            addHeader("Content-Type", "application/json; charset=utf-8")
            setBody(getFileAsStringJVM("news_source.json"))
        }

        mockServer.enqueue(responseSource)

        sources =  mockedAdapter.getSources().blockingFirst().subList(0, 3)*/
    }

    @Test
    fun getTranslatedArticles() {
        Mockito.`when`(translator.translate(Mockito.anyString(), Mockito.anyString())).thenReturn("Translated value")

        adapter.getTranslatedArticles(sources)
                .test()
                .assertValue { articles -> articles.all { article -> article.title == "Translated value" } }
                .assertNoErrors()
                .assertComplete()

        Mockito.verify(translator, Mockito.atLeast(sources.size)).translate(Mockito.anyString(), Mockito.anyString())

        Mockito.verifyZeroInteractions(liveDB)
    }

    @Test
    fun getArticles() {
        adapter.getArticles(sources)
                .test()
                .assertValue { articles -> articles.isNotEmpty() && articles.size > sources.size }
                .assertNoErrors()
                .assertComplete()

        Mockito.verifyZeroInteractions(translator)

        Mockito.verifyZeroInteractions(liveDB)
    }

    @Test
    fun getSources() {
        adapter.getSources()
                .test()
                .assertValue { sources -> sources.isNotEmpty() }
                .assertNoErrors()
                .assertNoTimeout()
                .assertComplete()

        Mockito.verifyZeroInteractions(translator)

        Mockito.verifyZeroInteractions(liveDB)
    }

    @Test
    fun getLives() {
        val lives = mapOf(
                "France 24" to "adsadasd",
                "CNN" to "https://cnn"
        )

        Mockito.`when`(livesSnapshot.value).thenReturn(lives)

        Mockito.`when`(liveDB.addListenerForSingleValueEvent(Mockito.any())).then {
            (it.arguments[0] as ValueEventListener).onDataChange(livesSnapshot)
        }

        adapter.getLives()
                .test()
                .assertValue { it.size == 2 }
                .assertNoErrors()
                .assertNoTimeout()
                .assertComplete()

        Mockito.verify(liveDB, Mockito.only()).addListenerForSingleValueEvent(Mockito.any())

        Mockito.verify(livesSnapshot, Mockito.only()).value
    }
}