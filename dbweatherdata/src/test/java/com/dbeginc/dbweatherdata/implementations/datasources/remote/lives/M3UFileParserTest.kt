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

import com.dbeginc.dbweatherdata.getFile
import com.dbeginc.dbweatherdata.proxies.remote.lives.RemoteIpTvLive
import org.hamcrest.Matchers.everyItem
import org.hamcrest.beans.HasPropertyWithValue
import org.hamcrest.core.Is
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import java.io.File

class M3UFileParserTest {
    private val m3UFileParser = M3UFileParser()

    @Test
    fun parseFileWithLogo() {
        val m3uWithLogoMin = getFile(fileName = "iptv_with_logo_min.m3u")

        val ipTvPlaylistWithLogoMin = m3UFileParser.parseFile(
                defaultName = m3uWithLogoMin.nameWithoutExtension,
                file = m3uWithLogoMin
        )

        assertEquals(m3uWithLogoMin.nameWithoutExtension, ipTvPlaylistWithLogoMin.name)

        assertEquals(getChannelsCount(m3uWithLogoMin), ipTvPlaylistWithLogoMin.channels.size)

        val m3uWithLogoFull = getFile(fileName = "iptv_with_logo.m3u")

        val ipTvPlaylistWithLogoFull = m3UFileParser.parseFile(
                defaultName = m3uWithLogoFull.nameWithoutExtension,
                file = m3uWithLogoFull
        )

        assertEquals(m3uWithLogoFull.nameWithoutExtension, ipTvPlaylistWithLogoFull.name)

        assertEquals(getChannelsCount(m3uWithLogoFull), ipTvPlaylistWithLogoFull.channels.size)
    }

    @Test
    fun parseFileWithoutLogo() {
        val m3uWithoutLogo = getFile(fileName = "iptv_without_logo.m3u")

        val ipTvPlaylistWithLogoMin = m3UFileParser.parseFile(defaultName = m3uWithoutLogo.nameWithoutExtension, file = m3uWithoutLogo)

        assertEquals(m3uWithoutLogo.nameWithoutExtension, ipTvPlaylistWithLogoMin.name)

        assertEquals(getChannelsCount(m3uWithoutLogo), ipTvPlaylistWithLogoMin.channels.size)

        assertThat(ipTvPlaylistWithLogoMin.channels, everyItem(HasPropertyWithValue.hasProperty(RemoteIpTvLive::channelLogo.name, Is.`is`(""))))
    }

    private fun getChannelsCount(file: File): Int =
            file.readLines().filter { it.contains("#EXTINF") }.size

}