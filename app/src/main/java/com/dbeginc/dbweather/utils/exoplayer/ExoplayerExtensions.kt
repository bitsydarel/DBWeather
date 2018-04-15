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

package com.dbeginc.dbweather.utils.exoplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.UdpDataSource
import com.google.android.exoplayer2.util.Util
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

@JvmField
val defaultBandwidthMeter = DefaultBandwidthMeter()  // Provides estimates of the currently available bandwidth.

private fun Context.getNetworkClient(): OkHttpClient = OkHttpClient
        .Builder()
        .connectTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(35, TimeUnit.SECONDS)
        .readTimeout(55, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .cache(Cache(File(cacheDir, "iptv_live_video_cache"), 100 * 1024 * 1024 /*100 MB*/))
        .followSslRedirects(true)
        .build()

fun Context.getPreferedDataSourceFactoryForUrl(url: String): DataSource.Factory {
    return when (Uri.parse(url).scheme) {
        "http" -> OkHttpDataSourceFactory(getNetworkClient(), Util.getUserAgent(applicationContext, applicationInfo.name), null)
        "udp" -> DataSource.Factory { UdpDataSource(null, UdpDataSource.DEFAULT_MAX_PACKET_SIZE, TimeUnit.SECONDS.toMillis(35).toInt()) }
        "rtmp" -> RtmpDataSourceFactory()
        else -> OkHttpDataSourceFactory(getNetworkClient(), Util.getUserAgent(applicationContext, applicationInfo.name), null)
    }
}