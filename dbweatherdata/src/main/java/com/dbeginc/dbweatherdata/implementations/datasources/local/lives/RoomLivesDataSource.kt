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

import android.content.Context
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalLivesDataSource
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalFavoriteLive
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalIpTvPlaylist
import com.dbeginc.dbweatherdata.proxies.mappers.toDomain
import com.dbeginc.dbweatherdata.proxies.mappers.toProxy
import com.dbeginc.dbweatherdomain.entities.lives.IpTvLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvPlaylist
import com.dbeginc.dbweatherdomain.entities.lives.YoutubeLive
import com.dbeginc.dbweatherdomain.entities.requests.lives.IpTvLiveRequest
import com.dbeginc.dbweatherdomain.entities.requests.lives.YoutubeLiveRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe

class RoomLivesDataSource private constructor(private val db: RoomLivesDatabase) : LocalLivesDataSource {
    companion object {
        @JvmStatic
        fun create(context: Context): RoomLivesDataSource {
            return RoomLivesDataSource(RoomLivesDatabase.createDb(context))
        }
    }

    override fun getAllYoutubeLives(): Flowable<List<YoutubeLive>> = db.livesDao().getAllYoutubeLives().map { lives -> lives.map { live -> live.toDomain() } }

    override fun getYoutubeLives(names: List<String>): Flowable<List<YoutubeLive>> = db.livesDao().getYoutubeLives(names).map { lives -> lives.map { live -> live.toDomain() } }

    override fun getYoutubeLive(name: String): Flowable<YoutubeLive> = db.livesDao().getYoutubeLive(name).map { live -> live.toDomain() }

    override fun getFavoriteYoutubeLives(): Flowable<List<String>> = db.livesDao().getFavoriteYoutubeLives().map { lives -> lives.map { live -> live.live_id } }

    override fun addYoutubeLiveToFavorite(request: YoutubeLiveRequest<Unit>): Completable =
            Completable.fromAction {
                db.livesDao().addYoutubeLiveToFavorites(
                        LocalFavoriteLive(request.channelName, request.channelName)
                )
            }

    override fun removeYoutubeLiveFromFavorite(request: YoutubeLiveRequest<Unit>): Completable =
            Completable.fromAction {
                db.livesDao().removeYoutubeLiveFromFavorites(
                        LocalFavoriteLive(request.channelName, request.channelName)
                )
            }

    override fun putYoutubeLives(lives: List<YoutubeLive>): Completable =
            Completable.fromAction {
                db.livesDao()
                        .putYoutubeLives(lives.map { live -> live.toProxy() })
            }

    override fun getAllIpTvPlaylist(): Flowable<List<IpTvPlaylist>> {
        return db.livesDao().getAllIpTvPlaylist()
                .map { ipTvs -> ipTvs.map { it.toDomain() } }
    }

    override fun getIpTvLives(request: IpTvLiveRequest<Unit>): Flowable<List<IpTvLive>> {
        return db.livesDao()
                .getIpTvLives(playlistId = request.playlist)
                .map { ipTvs -> ipTvs.map { it.toDomain() } }
    }

    override fun getIpTvLive(request: IpTvLiveRequest<String>): Flowable<IpTvLive> {
        return db.livesDao()
                .getIpTvLive(playlistId = request.playlist, ipTvLiveId = request.arg)
                .map { it.toDomain() }
    }

    override fun addIpTvLiveToFavorite(request: IpTvLiveRequest<IpTvLive>): Completable {
        return Completable.fromAction {
            db.livesDao()
                    .addIpTvLiveToFavorite(ipTvLive = request.arg.toProxy())
        }
    }

    override fun removeTvLiveFromFavorite(request: IpTvLiveRequest<IpTvLive>): Completable {
        return Completable.fromAction {
            db.livesDao()
                    .removeTvLiveFromFavorite(ipTvLive = request.arg.toProxy())
        }
    }

    override fun putAllIpTvPlaylist(playlists: List<IpTvPlaylist>): Completable {
        return Completable.fromAction {
            playlists.map { (name, channels) -> LocalIpTvPlaylist(name) to channels.map { it.toProxy() } }
                    .forEach {
                        db.livesDao().putIpTvPlaylist(playlist = it.first)
                        db.livesDao().putAllIpTvLives(channels = it.second)
                    }
        }
    }

    override fun findPlaylist(name: String): Maybe<List<IpTvPlaylist>> {
        return db.livesDao().findIpPlayLists(name)
                .map { it.map { it.toDomain() } }
    }

    override fun findIpTvLive(playlistId: String, name: String): Maybe<List<IpTvLive>> {
        return db.livesDao().findIpTvLiveWithChannelName(playlistId, name)
                .map { it.map { it.toDomain() } }
    }

}