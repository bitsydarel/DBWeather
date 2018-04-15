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

package com.dbeginc.dbweatherdata

import android.support.test.InstrumentationRegistry
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvLive
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvPlaylist
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvPlaylistWithChannels
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalYoutubeLive
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalLocation
import com.dbeginc.dbweatherdata.proxies.local.weather.LocalWeather
import com.dbeginc.dbweatherdata.proxies.remote.weather.RemoteWeather
import com.dbeginc.dbweatherdata.proxies.remote.weather.locations.RemoteLocation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.security.SecureRandom
import java.util.*

/**
 * Created by darel on 02.10.17.
 *
 * Android Instrumentation Test helper
 */

private val Alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()

fun generateFakeLocalYoutubeLives(howMuch: Int): List<LocalYoutubeLive> {
    val random = SecureRandom()

    return IntRange(1, howMuch)
            .map {
                LocalYoutubeLive(
                        name = Alphabet[random.nextInt(Alphabet.size)].toString(),
                        url = "github.com/bitsydarel$it"
                )
            }
}

fun generateFakeLocalIptvPlaylist(howMuch: Int): List<LocalIpTvPlaylistWithChannels> {
    val random = SecureRandom()

    return IntRange(1, howMuch)
            .map {
                val name = StringBuilder()

                for (i in 0..random.nextInt(7)) {
                    name.append(Alphabet[random.nextInt(Alphabet.size)])
                }

                LocalIpTvPlaylistWithChannels(
                        playlist = LocalIpTvPlaylist(name.toString()),
                        channels = generateFakeLocalIptvLive(
                                playlistId = name.toString(),
                                howMuch = random.nextInt(10)
                        )
                )
            }
}

fun generateFakeLocalIptvLive(playlistId: String, howMuch: Int): List<LocalIpTvLive> {
    val random = SecureRandom()

    return IntRange(1, howMuch)
            .map {
                LocalIpTvLive(
                        channelLogo = null,
                        channelName = Alphabet[random.nextInt(Alphabet.size)] + Alphabet[random.nextInt(Alphabet.size)].toString(),
                        url = "github.com/bitsydarel/$it",
                        playlistId = playlistId
                )
            }
}

fun getWeatherAndroid(): RemoteWeather {
    val builder = StringBuilder()
    Scanner(InstrumentationRegistry.getInstrumentation().context.assets.open("weather.json"))
            .forEach { line -> builder.append(line) }

    val weather: RemoteWeather = Gson().fromJson(builder.toString(), object : TypeToken<List<RemoteWeather>>() {}.type)

    weather.location = RemoteLocation("Ternopil", weather.latitude, weather.longitude, "UA", "Ukraine")
    return weather
}

fun getFullWeatherAndroid(): LocalWeather {
    val builder = StringBuilder()
    Scanner(InstrumentationRegistry.getInstrumentation().context.assets.open("full_weather.json"))
            .forEach { line -> builder.append(line) }

    val weather: LocalWeather = Gson().fromJson(builder.toString(), object : TypeToken<List<LocalWeather>>() {}.type)

    weather.location = LocalLocation("Miami Beach", weather.latitude, weather.longitude, "US", "United States")
    return weather
}