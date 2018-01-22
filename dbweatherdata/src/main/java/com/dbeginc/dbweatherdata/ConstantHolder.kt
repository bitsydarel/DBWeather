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

package com.dbeginc.dbweatherdata

import android.support.annotation.RestrictTo

/**
 * Created by darel on 15.09.17.
 *
 * Constant Holder class
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object ConstantHolder {
    const val TAG = "dbweather"
    const val WEATHER_TABLE = "weather"
    const val ARTICLES_TABLE = "articles"
    const val SOURCE_TABLE = "sources"
    const val LIVE_TABLE = "live"
    const val FAVORITE_LIVE_TABLE = "favorite_live"
    const val LIVE_SOURCE_REFERENCE = "live_source"
    const val NEWS_CACHE_NAME = "news_network_cache"
    const val WEATHER_CACHE_NAME = "weather_network_cache"
    const val CACHE_SIZE: Long = 50 * 1024 * 1024
}