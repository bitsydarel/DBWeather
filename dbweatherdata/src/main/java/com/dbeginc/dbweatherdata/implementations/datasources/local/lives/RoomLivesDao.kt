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

import android.arch.persistence.room.*
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.FAVORITE_LIVE_TABLE
import com.dbeginc.dbweatherdata.IPTV_LIVE_TABLE
import com.dbeginc.dbweatherdata.IPTV_PLAYLIST_TABLE
import com.dbeginc.dbweatherdata.YOUTUBE_LIVE_TABLE
import com.dbeginc.dbweatherdata.proxies.local.lives.*
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY)
interface RoomLivesDao {
    @Query("SELECT * FROM $YOUTUBE_LIVE_TABLE")
    fun getAllYoutubeLives(): Flowable<List<LocalYoutubeLive>>

    @Query("SELECT * FROM $YOUTUBE_LIVE_TABLE WHERE name IN (:names)")
    fun getYoutubeLives(names: List<String>): Flowable<List<LocalYoutubeLive>>

    @Query("SELECT * FROM $FAVORITE_LIVE_TABLE")
    fun getFavoriteYoutubeLives(): Flowable<List<LocalFavoriteLive>>

    @Query("SELECT * FROM $YOUTUBE_LIVE_TABLE WHERE name LIKE :liveId")
    fun getYoutubeLive(liveId: String): Flowable<LocalYoutubeLive>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putYoutubeLives(lives: List<LocalYoutubeLive>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addYoutubeLiveToFavorites(live: LocalFavoriteLive)

    @Delete
    fun removeYoutubeLiveFromFavorites(live: LocalFavoriteLive)

    @Transaction
    @Query("SELECT * FROM $IPTV_PLAYLIST_TABLE")
    fun getAllIpTvPlaylist(): Flowable<List<LocalIpTvPlaylistWithChannels>>

    @Query("SELECT * FROM $IPTV_LIVE_TABLE WHERE playlist_id LIKE :playlistId")
    fun getIpTvLives(playlistId: String): Flowable<List<LocalIpTvLive>>

    @Query("SELECT * FROM $IPTV_LIVE_TABLE WHERE playlist_id LIKE :playlistId AND channel_name LIKE :ipTvLiveId")
    fun getIpTvLive(playlistId: String, ipTvLiveId: String): Flowable<LocalIpTvLive>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addIpTvLiveToFavorite(ipTvLive: LocalIpTvLive)

    @Delete
    fun removeTvLiveFromFavorite(ipTvLive: LocalIpTvLive)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putIpTvPlaylist(playlist: LocalIpTvPlaylist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putAllIpTvLives(channels: List<LocalIpTvLive>)

    @Transaction
    @Query("SELECT * FROM $IPTV_PLAYLIST_TABLE WHERE name LIKE :name || '%'")
    fun findIpPlayLists(name: String): Maybe<List<LocalIpTvPlaylistWithChannels>>

    @Query("SELECT * FROM $IPTV_LIVE_TABLE WHERE playlist_id LIKE :playlistId AND channel_name LIKE :name || '%'")
    fun findIpTvLiveWithChannelName(playlistId: String, name: String): Maybe<List<LocalIpTvLive>>

}