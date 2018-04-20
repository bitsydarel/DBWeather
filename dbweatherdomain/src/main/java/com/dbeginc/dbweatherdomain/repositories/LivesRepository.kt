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

package com.dbeginc.dbweatherdomain.repositories

import com.dbeginc.dbweatherdomain.entities.lives.IpTvLive
import com.dbeginc.dbweatherdomain.entities.lives.IpTvPlaylist
import com.dbeginc.dbweatherdomain.entities.lives.YoutubeLive
import com.dbeginc.dbweatherdomain.entities.requests.lives.IpTvLiveRequest
import com.dbeginc.dbweatherdomain.entities.requests.lives.YoutubeLiveRequest
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by darel on 15.09.17.
 * Lives Repository
 *
 * Interface following repository pattern.
 *
 * Each method in this interface are threaded as Use Cases (Interactor in terms of Clean Architecture).
 */
interface LivesRepository : Cleanable {
    /**
     * Created by darel on 18.10.17.
     *
     * Get All Youtube Lives Stream available
     *
     * @return [Flowable] of youtube lives that provide currently available youtube lives
     */
    fun getAllYoutubeLives(): Flowable<List<YoutubeLive>>

    fun findYoutubeLive(possibleName: String): Maybe<List<YoutubeLive>>

    /**
     * Created by darel on 20.10.17.
     *
     * Get Youtube Lives
     *
     * @param names of youtube lives to retrieve
     *
     * @return [Flowable] of list youtube lives channel name that provide requested lives
     */
    fun getYoutubeLives(names: List<String>): Flowable<List<YoutubeLive>>


    /**
     * Created by darel on 21.10.17.
     *
     * Get YoutubeLive
     *
     * @param name of the youtube live to retrieve
     *
     * @return [Single] reactive stream of live that provide an live or an error
     */
    fun getYoutubeLive(name: String): Flowable<YoutubeLive>

    /**
     * Created by darel on 19.10.17.
     *
     * Get Favorite Lives
     *
     * @return [Flowable] reactive stream of list of string that provide channelName of favorite lives
     */
    fun getYoutubeFavoriteLiveNames(): Flowable<List<String>>


    /**
     * Created by darel on 19.10.17.
     *
     * Add YoutubeLive to favorites use case
     *
     * @param request containing information about the live to add to favorites
     * @return [Completable] reactive stream that notify his completion
     */
    fun addYoutubeLiveToFavorites(request: YoutubeLiveRequest<Unit>): Completable


    /**
     * Created by darel on 20.10.17.
     *
     * Remove YoutubeLive from favorites
     *
     * @param request containing information about the live to remove from favorites
     * @return [Completable] reactive stream that notify his completion
     */
    fun removeYoutubeLiveFromFavorites(request: YoutubeLiveRequest<Unit>): Completable

    /**
     * Created by darel on 03.04.2018
     *
     * @author bitsydarel@gmail.com
     *
     * @return [Flowable] of list of IpTv playlist
     */
    fun getAllIpTvPlaylist(): Flowable<List<IpTvPlaylist>>

    /**
     * Created by darel on 03.04.2018
     *
     * @author bitsydarel@gmail.com
     *
     * @param request containing information required to get IpTv lives
     * from a specific playlist
     *
     * @return [Flowable] of list of IpTv lives
     */
    fun getIpTvLives(request: IpTvLiveRequest<Unit>): Flowable<List<IpTvLive>>

    /**
     * Created by darel on 03.04.2018
     *
     * @author bitsydarel@gmail.com
     *
     * @param request containing information required to get IpTv live
     * from a specific playlist
     *
     * @return [Flowable] of list of IpTv lives
     */
    fun getIpTvLive(request: IpTvLiveRequest<String>): Flowable<IpTvLive>

    /**
     * Created by darel on 03.04.2018
     *
     * @author bitsydarel@gmail.com
     *
     * @param request containing information required to add IpTv live
     * from a specific playlist to the user favorite list
     *
     * @return [Completable] notifying the completion of the task
     */
    fun addIpTvLiveToFavorite(request: IpTvLiveRequest<IpTvLive>): Completable

    /**
     * Created by darel on 03.04.2018
     *
     * @author bitsydarel@gmail.com
     *
     * @param request containing information required to remove live IpTv
     * from a specific playlist to the user favorite list
     *
     * @return [Completable] notifying the completion of the task
     */
    fun removeTvLiveFromFavorite(request: IpTvLiveRequest<IpTvLive>): Completable

    fun findPlaylist(name: String): Maybe<List<IpTvPlaylist>>

    fun findIpTvLive(playlistId: String, name: String): Maybe<List<IpTvLive>>

}