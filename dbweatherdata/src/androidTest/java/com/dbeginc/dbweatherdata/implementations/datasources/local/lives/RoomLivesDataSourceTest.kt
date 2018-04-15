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

package com.dbeginc.dbweatherdata.implementations.datasources.local.lives

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.dbeginc.dbweatherdata.generateFakeLocalIptvPlaylist
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomLivesDataSourceTest {
    private lateinit var roomLivesDataSource: RoomLivesDataSource
    private lateinit var database: RoomLivesDatabase

    @Before
    fun setUp() {
        val fakeContext = InstrumentationRegistry.getTargetContext()

        database = Room.inMemoryDatabaseBuilder(fakeContext, RoomLivesDatabase::class.java).build()

        roomLivesDataSource = RoomLivesDataSource.createForTest(testDatabase = database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getAllYoutubeLives() {
    }

    @Test
    fun getYoutubeLives() {
    }

    @Test
    fun getYoutubeLive() {
    }

    @Test
    fun getFavoriteYoutubeLives() {
    }

    @Test
    fun addYoutubeLiveToFavorite() {
    }

    @Test
    fun removeYoutubeLiveFromFavorite() {
    }

    @Test
    fun putYoutubeLives() {
    }

    @Test
    fun getAllIpTvPlaylist() {
    }

    @Test
    fun getIpTvLives() {
    }

    @Test
    fun getIpTvLive() {
    }

    @Test
    fun addIpTvLiveToFavorite() {
    }

    @Test
    fun removeTvLiveFromFavorite() {
    }

    @Test
    fun putAllIpTvPlaylist() {
    }

    @Test
    fun findPlaylist() {
        val fakePlaylists = generateFakeLocalIptvPlaylist(howMuch = 100)

        fakePlaylists.forEach {
            database.livesDao().putIpTvPlaylist(playlist = it.playlist)
            database.livesDao().putAllIpTvLives(channels = it.channels)
        }

        val (playlist, _) = fakePlaylists.first()

        roomLivesDataSource.findPlaylist(name = playlist.name.first().toString())
                .test()
                .assertValue { it.isNotEmpty() }
                .assertValue { it.any { it.name.contains(playlist.name) } }

    }

    @Test
    fun findIpTvLive() {
    }
}