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

package com.dbeginc.dbweatherdata.implementations.datasources.remote.lives

import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteIpTvLive
import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteIpTvPlaylist
import java.io.File
import java.util.*
import java.util.regex.Pattern

class M3UFileParser : IpTvFileParser {
    companion object {
        private const val EXT_M3U = "#EXTM3U"
        private const val EXT_INF = "#EXTINF:"
        private const val EXT_PLAYLIST_NAME = "#PLAYLIST:"
        private const val EXT_LOGO = "tvg-logo=\""

    }

    override fun parseFile(defaultName: String, file: File): RemoteIpTvPlaylist {
        return file.useLines(charset = Charsets.UTF_8) { lines ->
            val playlist = RemoteIpTvPlaylist(defaultName)
            val channels = mutableSetOf<RemoteIpTvLive>()
            var lastIpTv = RemoteIpTvLive(playlistId = playlist.name)

            lines.forEach { line ->
                when {
                    line.contains(EXT_M3U) -> playlist.name = parsePlayListName(line = line, default = playlist.name)
                    line.contains(EXT_INF) -> lastIpTv = parseChannelDescription(line, playlistId = lastIpTv.playlistId)
                    line.isNotBlank() -> {
                        lastIpTv.url = line.substringBefore("\n")

                        channels.add(lastIpTv)
                    }
                }
            }

            playlist.channels = channels

            return@useLines playlist
        }
    }

    private fun parsePlayListName(line: String, default: String): String {
        return if (line.contains(EXT_PLAYLIST_NAME))
            line.substringAfterLast(delimiter = EXT_PLAYLIST_NAME, missingDelimiterValue = default)
        else default
    }

    private fun parseChannelDescription(line: String, playlistId: String): RemoteIpTvLive {
        val name: String = line.substringAfter(",", "Channel ${Random().nextInt()}")

        val matcher = Pattern.compile("$EXT_LOGO(.*?)\"").matcher(line)

        val logoUrl: String = if (matcher.find()) matcher.group(1) else ""

        return RemoteIpTvLive(
                channelName = name,
                channelLogo = logoUrl,
                playlistId = playlistId
        )
    }

}