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

package com.dbeginc.dbweatherlives.viewmodels

import com.dbeginc.dbweatherdomain.entities.lives.IpTvLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvPlaylist
import com.dbeginc.dbweatherdomain.entities.lives.YoutubeLive

fun YoutubeLive.toUi(isFavorite: Boolean = false): YoutubeLiveModel = YoutubeLiveModel(channelName, url, isFavorite)

fun IpTvLive.toUi(): IpTvLiveModel = IpTvLiveModel(
        channelLogo = channelLogo ?: "",
        channelName = channelName,
        url = url,
        playlistId = playlistId
)

fun IpTvPlaylist.toUi(): IpTvPlayListModel = IpTvPlayListModel(
        name = name,
        channels = channels.map { channel -> channel.toUi() }
)