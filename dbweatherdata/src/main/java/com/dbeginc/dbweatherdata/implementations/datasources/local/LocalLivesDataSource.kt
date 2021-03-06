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

package com.dbeginc.dbweatherdata.implementations.datasources.local

import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdomain.entities.lives.IpTvLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvPlaylist
import com.dbeginc.dbweatherdomain.entities.lives.YoutubeLive
import com.dbeginc.dbweatherdomain.entities.requests.lives.IpTvLiveRequest
import com.dbeginc.dbweatherdomain.entities.requests.lives.YoutubeLiveRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

@RestrictTo(RestrictTo.Scope.LIBRARY)
interface LocalLivesDataSource {
    fun getAllYoutubeLives(): Flowable<List<YoutubeLive>>

    fun findYoutubeLive(name: String): Maybe<List<YoutubeLive>>

    fun getYoutubeLives(names: List<String>): Flowable<List<YoutubeLive>>

    fun getFavoriteYoutubeLives(): Flowable<List<String>>

    fun getYoutubeLive(name: String): Flowable<YoutubeLive>

    fun addYoutubeLiveToFavorite(request: YoutubeLiveRequest<Unit>): Completable

    fun removeYoutubeLiveFromFavorite(request: YoutubeLiveRequest<Unit>): Completable

    fun putYoutubeLives(lives: List<YoutubeLive>): Completable

    fun getAllIpTvPlaylist(): Flowable<List<IpTvPlaylist>>

    fun getIpTvLives(request: IpTvLiveRequest<Unit>): Flowable<List<IpTvLive>>

    fun getIpTvLive(request: IpTvLiveRequest<String>): Flowable<IpTvLive>

    fun addIpTvLiveToFavorite(request: IpTvLiveRequest<IpTvLive>): Completable

    fun removeTvLiveFromFavorite(request: IpTvLiveRequest<IpTvLive>): Completable

    fun putAllIpTvPlaylist(playlists: List<IpTvPlaylist>): Completable

    fun findPlaylist(name: String): Maybe<List<IpTvPlaylist>>

    fun findIpTvLive(playlistId: String, name: String): Maybe<List<IpTvLive>>
}