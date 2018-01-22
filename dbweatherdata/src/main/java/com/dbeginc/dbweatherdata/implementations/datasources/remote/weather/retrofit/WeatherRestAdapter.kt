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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.weather.retrofit

import android.content.Context
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.ConstantHolder
import com.dbeginc.dbweatherdata.ConstantHolder.CACHE_SIZE
import com.dbeginc.dbweatherdata.ConstantHolder.WEATHER_CACHE_NAME
import com.dbeginc.dbweatherdata.proxies.remote.weather.RemoteWeather
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocation
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocations
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.jetbrains.annotations.TestOnly
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 16.09.17.
 *
 * Weather Api Rest Adapter
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class WeatherRestAdapter private constructor(client: OkHttpClient) {
    private val deviceLanguage by lazy { Locale.getDefault().language }
    private val weatherApi: WeatherApi
    private val locationApi: LocationApi

    companion object {
        private const val RESULT_STYLE = "MEDIUM"
        private const val WEATHER_URL = "https://api.darksky.net/"
        private const val GEO_NAMES_API_URL = "http://api.geonames.org/"

        private val weatherSupportedLanguage = listOf("ar","az","be","bs","ca","cs","de","el","en","es",
        "et","fr","hr","hu","id","it","is","kw","nb","nl","pl","pt","ru",
        "sk","sl","sr","sv","tet","tr","uk","x-pig-latin","zh","zh-tw")
        private val geocodeSupportedLanguage = listOf("cs", "en", "eo", "fi", "he", "iata", "it", "nl", "no", "pl", "ru", "uk", "unlc")

        fun create(context: Context): WeatherRestAdapter {
            val client = OkHttpClient.Builder()
                    .connectTimeout(35, TimeUnit.SECONDS)
                    .writeTimeout(35, TimeUnit.SECONDS)
                    .readTimeout(55, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .cache(Cache(File(context.cacheDir, WEATHER_CACHE_NAME), CACHE_SIZE))

//            if (BuildConfig.DEBUG) client.addNetworkInterceptor(StethoInterceptor())

            return WeatherRestAdapter(client.build())
        }

        @TestOnly
        @RestrictTo(RestrictTo.Scope.TESTS)
        fun create(client: OkHttpClient) : WeatherRestAdapter = WeatherRestAdapter(client)
    }

    init {
        val rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

        weatherApi = Retrofit.Builder()
                .baseUrl(WEATHER_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(client)
                .build()
                .create(WeatherApi::class.java)

        locationApi = Retrofit.Builder()
                .baseUrl(GEO_NAMES_API_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .client(client)
                .build()
                .create(LocationApi::class.java)
    }

    fun getWeather(latitude: Double, longitude: Double): Single<RemoteWeather> {
        val coordinates = latitude.toString().plus(",").plus(longitude.toString())
        val language = if (weatherSupportedLanguage.contains(deviceLanguage)) deviceLanguage else Locale.ENGLISH.language

        return weatherApi.getWeather(BuildConfig.WEATHER_API_KEY, coordinates, language, "auto")
                .flatMap { weather ->
                    getLocationByCoordinate(weather.latitude, weather.longitude)
                            .map { location ->
                                weather.location = location
                                return@map weather
                            }
                }
    }

    fun getLocationsFor(query: String) : Single<RemoteLocations> {
        val language = if (geocodeSupportedLanguage.contains(deviceLanguage)) deviceLanguage else Locale.ENGLISH.language

        return locationApi.getLocation(query, username=BuildConfig.GEONAME_USERNAME, style=RESULT_STYLE, maxRows=3, isNameRequired=true, language=language)
    }

    private fun getLocationByCoordinate(latitude: Double, longitude: Double) : Single<RemoteLocation> {
        val language = if (geocodeSupportedLanguage.contains(deviceLanguage)) deviceLanguage else Locale.ENGLISH.language

        return locationApi.getLocationByLatAndLong(latitude=latitude, longitude=longitude, username=BuildConfig.GEONAME_USERNAME, style=RESULT_STYLE, maxRows=1, language=language)
                .map { locations -> locations.locations.first() }
    }
}