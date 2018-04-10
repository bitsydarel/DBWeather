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

package com.dbeginc.dbweatherdata.proxies.mappers

import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvLive
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvPlaylist
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvPlaylistWithChannels
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalYoutubeLive
import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteIpTvLive
import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteIpTvPlaylist
import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteYoutubeLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvPlaylist
import com.dbeginc.dbweatherdomain.entities.lives.YoutubeLive

internal fun LocalYoutubeLive.toDomain(): YoutubeLive = YoutubeLive(
        channelName = name,
        url = url
)

internal fun RemoteYoutubeLive.toDomain(): YoutubeLive = YoutubeLive(
        channelName = name,
        url = url
)

internal fun YoutubeLive.toProxy(): LocalYoutubeLive = LocalYoutubeLive(
        name = channelName,
        url = url
)

internal fun LocalIpTvPlaylistWithChannels.toDomain(): IpTvPlaylist = IpTvPlaylist(
        name = playlist.name,
        channels = channels.map { it.toDomain() }.toSet()
)

internal fun LocalIpTvLive.toDomain(): IpTvLive = IpTvLive(
        channelLogo = channelLogo,
        channelName = channelName,
        url = url,
        playlistId = playlistId
)

internal fun IpTvLive.toProxy(): LocalIpTvLive = LocalIpTvLive(
        channelLogo = channelLogo,
        channelName = channelName,
        url = url,
        playlistId = playlistId
)

internal fun IpTvPlaylist.toProxy(): LocalIpTvPlaylistWithChannels = LocalIpTvPlaylistWithChannels(
        playlist = LocalIpTvPlaylist(name),
        channels = channels.map { it.toProxy() }
)

internal fun RemoteIpTvLive.toDomain(): IpTvLive = IpTvLive(
        channelLogo = channelLogo,
        channelName = channelName,
        url = url,
        playlistId = playlistId
)

internal fun RemoteIpTvPlaylist.toDomain(): IpTvPlaylist = IpTvPlaylist(
        name = name,
        channels = channels.map { it.toDomain() }.toSet()
)