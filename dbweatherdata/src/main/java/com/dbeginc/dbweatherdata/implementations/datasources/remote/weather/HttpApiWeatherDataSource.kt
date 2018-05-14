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
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.DEFAULT_NETWORK_CACHE_SIZE
import com.dbeginc.dbweatherdata.NETWORK_CACHE_NAME
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteWeatherDataSource
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.remote.weather.RemoteWeather
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.GeonamesItem
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocations
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.entities.weather.Weather
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Single
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by darel on 16.09.17.
 *
 * Remote Weather Data Provider
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
class HttpApiWeatherDataSource internal constructor(val locationApiUrl: String, val weatherApiUrl: String): RemoteWeatherDataSource {
    private val deviceLanguage by lazy { Locale.getDefault().language }

    companion object {
        private const val RESULT_STYLE = "MEDIUM"

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

            return HttpApiWeatherDataSource(
                    weatherApiUrl = "https://api.darksky.net/",
                    locationApiUrl = "http://api.geonames.org/"
            )
        }
    }

    override fun getWeather(request: WeatherRequest<Unit>): Single<Weather> {
        val language = if (weatherSupportedLanguage.contains(deviceLanguage)) deviceLanguage else Locale.ENGLISH.language

        return Rx2AndroidNetworking.get("${weatherApiUrl}forecast/{apiKey}/{latitude},{longitude}")
                .addPathParameter("apiKey", BuildConfig.WEATHER_API_KEY)
                .addPathParameter("latitude", "%f".format(Locale.US, request.latitude))
                .addPathParameter("longitude", "%f".format(Locale.US, request.longitude))
                .addQueryParameter("lang", language)
                .addQueryParameter("units", "auto")
                .build()
                .getObjectSingle(RemoteWeather::class.java)
                .flatMap { weather ->
                    getLocationByLatAndLong(
                            latitude = request.latitude,
                            longitude = request.longitude,
                            username = BuildConfig.GEONAME_USERNAME,
                            style = RESULT_STYLE,
                            maxRows = 1,
                            language = language
                    ).map { location ->
                        weather.location = location.first()
                        return@map weather
                    }
                }.map { proxy -> proxy.toDomain() }
    }

    override fun getLocations(name: String): Single<List<Location>> {
        val language: String = if (geocodeSupportedLanguage.contains(deviceLanguage)) deviceLanguage else Locale.ENGLISH.language

        return Rx2AndroidNetworking.get("${locationApiUrl}searchJSON")
                .addQueryParameter("q", name)
                .addQueryParameter("username", BuildConfig.GEONAME_USERNAME)
                .addQueryParameter("style", RESULT_STYLE)
                .addQueryParameter("maxRows", "3")
                .addQueryParameter("isNameRequired", true.toString())
                .addQueryParameter("lang", language)
                .build()
                .getObjectSingle(RemoteLocations::class.java)
                .map { locations -> locations.geonames?.map { it.toDomain() } ?: emptyList() }
    }

    private fun getLocationByLatAndLong(
            latitude: Double,
            longitude: Double,
            username: String,
            style: String,
            maxRows: Int,
            language: String
    ) : Single<List<GeonamesItem>>{
        return Rx2AndroidNetworking.get("${locationApiUrl}findNearbyJSON")
                .addQueryParameter("lat", "%f".format(Locale.US, latitude))
                .addQueryParameter("lng", "%f".format(Locale.US, longitude))
                .addQueryParameter("username", username)
                .addQueryParameter("style", style)
                .addQueryParameter("isNameRequired", true.toString())
                .addQueryParameter("maxRows", maxRows.toString())
                .addQueryParameter("lang", language)
                .build()
                .getObjectSingle(RemoteLocations::class.java)
                .map { locations -> locations.geonames ?: emptyList() }
    }

}