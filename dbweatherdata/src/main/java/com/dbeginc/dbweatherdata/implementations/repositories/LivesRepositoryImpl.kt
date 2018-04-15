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

package com.dbeginc.dbweatherdata.implementations.repositories

import android.content.Context
import android.util.Log
import com.dbeginc.dbweatherdata.BuildConfig
import com.dbeginc.dbweatherdata.CrashlyticsLogger
import com.dbeginc.dbweatherdata.RxThreadProvider
import com.dbeginc.dbweatherdata.TAG
import com.dbeginc.dbweatherdata.implementations.datasources.local.LocalLivesDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.local.lives.RoomLivesDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.RemoteLivesDataSource
import com.dbeginc.dbweatherdata.implementations.datasources.remote.lives.FirebaseLivesDataSource
import com.dbeginc.dbweatherdomain.Logger
import com.dbeginc.dbweatherdomain.ThreadProvider
import com.dbeginc.dbweatherdomain.entities.lives.IpTvLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvPlaylist
import com.dbeginc.dbweatherdomain.entities.lives.YoutubeLive
import com.dbeginc.dbweatherdomain.entities.requests.lives.IpTvLiveRequest
import com.dbeginc.dbweatherdomain.entities.requests.lives.YoutubeLiveRequest
import com.dbeginc.dbweatherdomain.repositories.LivesRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import java.util.*

class LivesRepositoryImpl private constructor(private val threads: ThreadProvider,
                                              private val logger: Logger,
                                              private val localSource: LocalLivesDataSource,
                                              private val remoteSource: RemoteLivesDataSource) : LivesRepository {

    companion object {
        @JvmStatic
        fun create(context: Context): LivesRepository {
            return LivesRepositoryImpl(
                    threads = RxThreadProvider,
                    logger = CrashlyticsLogger,
                    localSource = RoomLivesDataSource.create(context),
                    remoteSource = FirebaseLivesDataSource.create(context)
            )
        }
    }

    private val subscriptions = CompositeDisposable()

    override fun getAllYoutubeLives(): Flowable<List<YoutubeLive>> {
        return localSource.getAllYoutubeLives()
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remoteSource.getAllYoutubeLives()
                            .subscribeOn(threads.IO)
                            .subscribe(this::addYoutubeLives, logger::logError)
                }
    }

    override fun getYoutubeLives(names: List<String>): Flowable<List<YoutubeLive>> {
        return localSource.getYoutubeLives(names)
                .subscribeOn(threads.CP)
    }

    override fun getYoutubeLive(name: String): Flowable<YoutubeLive> {
        return localSource.getYoutubeLive(name)
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remoteSource.getYoutubeLive(name = name)
                            .subscribeOn(threads.IO)
                            .map { live -> Collections.singletonList(live) }
                            .subscribe(this::addYoutubeLives, logger::logError)
                }
    }

    override fun getYoutubeFavoriteLiveNames(): Flowable<List<String>> {
        return localSource.getFavoriteYoutubeLives()
                .subscribeOn(threads.CP)
    }

    override fun addYoutubeLiveToFavorites(request: YoutubeLiveRequest<Unit>): Completable {
        return localSource.addYoutubeLiveToFavorite(request)
                .subscribeOn(threads.CP)
    }

    override fun removeYoutubeLiveFromFavorites(request: YoutubeLiveRequest<Unit>): Completable {
        return localSource.removeYoutubeLiveFromFavorite(request)
                .subscribeOn(threads.CP)
    }

    override fun getAllIpTvPlaylist(): Flowable<List<IpTvPlaylist>> {
        return localSource.getAllIpTvPlaylist()
                .subscribeOn(threads.CP)
                .doOnSubscribe {
                    remoteSource.getAllIpTvPlaylist()
                            .subscribeOn(threads.IO)
                            .subscribe(this::addIpTvPlayLists, logger::logError)
                }
    }

    override fun getIpTvLives(request: IpTvLiveRequest<Unit>): Flowable<List<IpTvLive>> {
        return localSource.getIpTvLives(request)
                .subscribeOn(threads.CP)
    }

    override fun getIpTvLive(request: IpTvLiveRequest<String>): Flowable<IpTvLive> {
        return localSource.getIpTvLive(request)
                .subscribeOn(threads.CP)
    }

    override fun addIpTvLiveToFavorite(request: IpTvLiveRequest<IpTvLive>): Completable {
        return localSource.addIpTvLiveToFavorite(request)
                .subscribeOn(threads.CP)
    }

    override fun removeTvLiveFromFavorite(request: IpTvLiveRequest<IpTvLive>): Completable {
        return localSource.removeTvLiveFromFavorite(request)
                .subscribeOn(threads.CP)
    }

    override fun findPlaylist(name: String): Maybe<List<IpTvPlaylist>> {
        return localSource.findPlaylist(name = name)
                .subscribeOn(threads.CP)
    }

    override fun findIpTvLive(playlistId: String, name: String): Maybe<List<IpTvLive>> {
        return localSource.findIpTvLive(playlistId, name)
                .subscribeOn(threads.CP)
    }

    override fun clean() = subscriptions.clear()

    private fun addYoutubeLives(youtubeLives: List<YoutubeLive>) {
        subscriptions.add(
                localSource.putYoutubeLives(youtubeLives)
                        .subscribeOn(threads.CP)
                        .subscribeWith(TaskObserver())
        )
    }

    private fun addIpTvPlayLists(playlists: List<IpTvPlaylist>) {
        subscriptions.add(
                localSource.putAllIpTvPlaylist(playlists)
                        .subscribeOn(threads.CP)
                        .subscribeWith(TaskObserver())
        )
    }

    private inner class TaskObserver : DisposableCompletableObserver() {
        override fun onComplete() {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "Update of data done in ${LivesRepositoryImpl::class.java.simpleName}")
            }
        }

        override fun onError(e: Throwable) = logger.logError(e)
    }

}