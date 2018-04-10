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

import android.content.Context
import android.support.annotation.RestrictTo
import android.support.annotation.VisibleForTesting
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.DEFAULT_NETWORK_CACHE_SIZE
import com.dbeginc.dbweatherdata.NETWORK_CACHE_NAME
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteWeatherDataSource
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.remote.weather.RemoteWeather
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Data Provider
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class HttpApiWeatherDataSource private constructor(private val locationsHttpApi: LocationApi) : RemoteWeatherDataSource {
    private val deviceLanguage by lazy { Locale.getDefault().language }

    companion object {
        private const val RESULT_STYLE = "MEDIUM"
        private const val GEO_NAMES_API_URL = "http://api.geonames.org/"

        private val weatherSupportedLanguage = listOf("ar", "az", "be", "bs", "ca", "cs", "de", "el", "en", "es",
                "et", "fr", "hr", "hu", "id", "it", "is", "kw", "nb", "nl", "pl", "pt", "ru",
                "sk", "sl", "sr", "sv", "tet", "tr", "uk", "x-pig-latin", "zh", "zh-tw")

        private val geocodeSupportedLanguage = listOf("cs", "en", "eo", "fi", "he", "iata", "it", "nl", "no", "pl", "ru", "uk", "unlc")

        @JvmStatic
        fun create(context: Context): RemoteWeatherDataSource {
            val client = OkHttpClient.Builder()
                    .connectTimeout(35, TimeUnit.SECONDS)
                    .writeTimeout(35, TimeUnit.SECONDS)
                    .readTimeout(55, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .cache(Cache(File(context.cacheDir, NETWORK_CACHE_NAME), DEFAULT_NETWORK_CACHE_SIZE))
                    .build()

            AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BASIC)

            AndroidNetworking.initialize(context, client)

            val rxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

            val geoLocationApi = Retrofit.Builder()
                    .baseUrl(GEO_NAMES_API_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .addCallAdapterFactory(rxJava2CallAdapterFactory)
                    .client(client)
                    .build()
                    .create(LocationApi::class.java)

            return HttpApiWeatherDataSource(locationsHttpApi = geoLocationApi)
        }

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        fun create(mockLocationApi: LocationApi): RemoteWeatherDataSource =
                HttpApiWeatherDataSource(locationsHttpApi = mockLocationApi)

    }

    override fun getWeather(request: WeatherRequest<Unit>): Single<Weather> {
        val language = if (weatherSupportedLanguage.contains(deviceLanguage)) deviceLanguage else Locale.ENGLISH.language

        return Rx2AndroidNetworking.get("https://api.darksky.net/forecast/{apiKey}/{latitude},{longitude}")
                .addPathParameter("apiKey", BuildConfig.WEATHER_API_KEY)
                .addPathParameter("latitude", request.latitude.toString())
                .addPathParameter("longitude", request.longitude.toString())
                .addQueryParameter("lang", language)
                .addQueryParameter("units", "auto")
                .build()
                .getObjectSingle(RemoteWeather::class.java)
                .flatMap { weather ->
                    locationsHttpApi.getLocationByLatAndLong(
                            latitude = request.latitude,
                            longitude = request.longitude,
                            username = BuildConfig.GEONAME_USERNAME,
                            style = RESULT_STYLE,
                            maxRows = 1,
                            language = language
                    ).map {
                        it.locations.first()
                    }.map { location ->
                        weather.location = location
                        return@map weather
                    }
                }.map { proxy -> proxy.toDomain() }
    }

    override fun getLocations(name: String): Single<List<Location>> {
        val language = if (geocodeSupportedLanguage.contains(deviceLanguage)) deviceLanguage else Locale.ENGLISH.language

        return locationsHttpApi.getLocation(
                query = name,
                username = BuildConfig.GEONAME_USERNAME,
                style = RESULT_STYLE,
                maxRows = 3,
                isNameRequired = true,
                language = language
        ).map { (locations) -> locations.map { it.toDomain() } }
    }

}