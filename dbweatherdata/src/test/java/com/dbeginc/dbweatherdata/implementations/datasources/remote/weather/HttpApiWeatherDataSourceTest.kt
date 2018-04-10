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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.weather

import com.dbeginc.dbweatherdata.getFileAsStringJVM
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteWeatherDataSource
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class HttpApiWeatherDataSourceTest {
    private lateinit var server: MockWebServer
    private lateinit var weatherDataSource: RemoteWeatherDataSource

    @Before
    fun setup() {
        server = MockWebServer()

        server.start()

        val client = OkHttpClient.Builder().build()

        val rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

        val geoLocationApi = Retrofit.Builder()
                .baseUrl(server.url("/").toString())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(client)
                .build()
                .create(LocationApi::class.java)

        weatherDataSource = HttpApiWeatherDataSource.create(mockLocationApi = geoLocationApi)
    }

    @Test
    fun `get random locations and check if xml parsing done properly`() {
        val serverResponse = MockResponse()

        serverResponse.apply {
            setResponseCode(200)
            setBody(getFileAsStringJVM("random_locations.xml"))
        }

        server.enqueue(serverResponse)

        weatherDataSource.getLocations("random")
                .test()
                .assertValue { it.size == 100 }
                .assertValue { it.any { it.name.isNotBlank() && it.countryCode.isNotBlank() } }
                .assertNoErrors()
                .assertComplete()

        server.enqueue(serverResponse.clone().setBody(getFileAsStringJVM("empty_locations.xml")))

        weatherDataSource.getLocations("random")
                .test()
                .assertValue { it.isEmpty() }
                .assertNoErrors()
                .assertComplete()

        server.enqueue(serverResponse.clone().setBody(getFileAsStringJVM("full_locations.xml")))

        weatherDataSource.getLocations("random")
                .test()
                .assertValue { it.size == 3 }
                .assertValue { it.any { it.name.isNotBlank() && it.countryCode.isNotBlank() } }
                .assertNoErrors()
                .assertComplete()
    }
}