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

package com.dbeginc.dbweatherdata.proxies.remote

import com.dbeginc.dbweatherdata.getFileAsStringJVM
import com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.LocationApi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

/**
 * Created by darel on 03.10.17.
 *
 * Checking Xml Parsing
 */
@RunWith(JUnit4::class)
class RetrofitWeatherXmlConversions {
    private lateinit var mockServer: MockWebServer
    private lateinit var serverUrl: String
    private lateinit var fullLocations: String
    private lateinit var emptyLocations: String
    private lateinit var retrofitService: LocationApi

    @Before
    fun setup() {
        fullLocations = getFileAsStringJVM("full_locations.xml")
        emptyLocations = getFileAsStringJVM("empty_locations.xml")

        mockServer = MockWebServer()
        mockServer.start()

        serverUrl = mockServer.url("/").toString()

        retrofitService = Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkHttpClient())
                .build()
                .create(LocationApi::class.java)
    }

    @Test
    fun `testing Convertions with full Location`() {
        val response = MockResponse().apply {
            setResponseCode(200)
            addHeader("Access-Control-Allow-Origin", "*")
            addHeader("Cache-Control", "no-cache")
            addHeader("Content-Type", "text/xml;charset=UTF-8")
            setBody(fullLocations)
        }
        mockServer.enqueue(response)

        retrofitService.getLocation("Londres","bitsydarel", "MEDIUM", 3, true, "en")
                .test()
                .assertNoErrors()
                .assertValue {
                    xml -> xml.locations.size == 3
                }
    }

    @Test
    fun `test Convertions with empty location`() {
        val response = MockResponse().apply {
            setResponseCode(200)
            addHeader("Access-Control-Allow-Origin", "*")
            addHeader("Cache-Control", "no-cache")
            addHeader("Content-Type", "text/xml;charset=UTF-8")
            setBody(emptyLocations)
        }
        mockServer.enqueue(response)

        retrofitService.getLocation("Londres","bitsydarel", "MEDIUM", 3, true, "en")
                .test()
                .assertNoErrors()
                .assertValue {
                    xml -> xml.locations.size == 0
                }
    }
}