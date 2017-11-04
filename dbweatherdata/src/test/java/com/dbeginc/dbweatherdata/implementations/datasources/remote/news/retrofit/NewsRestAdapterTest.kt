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

import com.dbeginc.dbweatherdata.getFileAsStringJVM
import com.dbeginc.dbweatherdata.implementations.datasources.remote.news.translator.Translator
import com.google.firebase.database.DatabaseReference
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

/**
 * Created by darel on 04.10.17.
 *
 * News Rest Adapter Test
 */
@RunWith(MockitoJUnitRunner::class)
class NewsRestAdapterTest {
    private lateinit var mockServer: MockWebServer
    private lateinit var newsRestAdapter: NewsRestAdapter
    private lateinit var sourcesJson: String
    private lateinit var articlesJson: String

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start()

        NewsRestAdapter.NEWS_API_URL = mockServer.url("/").toString()
        newsRestAdapter = NewsRestAdapter.create(OkHttpClient(), Mockito.mock(DatabaseReference::class.java), Mockito.mock(Translator::class.java))

        sourcesJson = getFileAsStringJVM("news_source.json")
        articlesJson = getFileAsStringJVM("news_articles.json")

    }

    @Test
    fun `Test of getting sources and articles`() {
        val responseSource = MockResponse().apply {
            setResponseCode(200)
            addHeader("Cache-Control", "no-cache")
            addHeader("Content-Length", "22386")
            addHeader("Content-Type", "application/json; charset=utf-8")
            setBody(sourcesJson)
        }

        mockServer.enqueue(responseSource)

        val sourcesResponse = newsRestAdapter.getSources().blockingFirst().subList(0, 3)

        for (index in IntRange(0, 3)) {
            mockServer.enqueue(
                    MockResponse().apply {
                        setResponseCode(200)
                        addHeader("Cache-Control", "no-cache")
                        addHeader("Content-Length", "22386")
                        addHeader("Content-Type", "application/json; charset=utf-8")
                        setBody(articlesJson)
                    }
            )
        }

        newsRestAdapter.getArticles(sourcesResponse)
                .test()
                .assertValue { result -> result.size == 30 }
    }
}