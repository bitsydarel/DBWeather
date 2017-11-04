/*
 *  Copyright (C) 2017 Darel Bitsy
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweatherdata.implementations.datasources.local.news.room

import android.arch.persistence.room.*
import android.support.annotation.RestrictTo
import com.dbeginc.dbweatherdata.ConstantHolder.ARTICLES_TABLE
import com.dbeginc.dbweatherdata.ConstantHolder.FAVORITE_LIVE_TABLE
import com.dbeginc.dbweatherdata.ConstantHolder.LIVE_TABLE
import com.dbeginc.dbweatherdata.ConstantHolder.SOURCE_TABLE
import com.dbeginc.dbweatherdata.proxies.local.news.LocalArticle
import com.dbeginc.dbweatherdata.proxies.local.news.LocalFavoriteLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalSource
import io.reactivex.Maybe
import io.reactivex.Single

/**
 * Created by darel on 04.10.17.
 *
 * News Data access object
 */
@Dao
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface LocalNewsDao {

    @Query("SELECT * FROM $ARTICLES_TABLE WHERE source_id IN (:sources)")
    fun getArticles(sources: List<String>) : Maybe<List<LocalArticle>>

    @Query("SELECT * FROM $ARTICLES_TABLE WHERE source_id LIKE :sourceId AND url LIKE :articleUrl")
    fun getArticle(sourceId: String, articleUrl: String) : Single<LocalArticle>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun putArticles(articles: List<LocalArticle>)

    @Query("SELECT * FROM $SOURCE_TABLE")
    fun getSources() : Maybe<List<LocalSource>>

    @Query("SELECT * FROM $SOURCE_TABLE WHERE subscribed LIKE :value")
    fun getSubscribedSources(value: Boolean = true) : Maybe<List<LocalSource>>

    @Query("SELECT * FROM $SOURCE_TABLE WHERE id LIKE :id")
    fun getSource(id: String) : Single<LocalSource>

    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun putSources(sources: List<LocalSource>)

    @Update(onConflict=OnConflictStrategy.REPLACE)
    fun updateSource(source: LocalSource)

    @Query("SELECT * FROM $LIVE_TABLE")
    fun getAllLives() : Maybe<List<LocalLive>>

    @Query("SELECT * FROM $LIVE_TABLE WHERE name IN (:names)")
    fun getLives(names: List<String>): Maybe<List<LocalLive>>

    @Query("SELECT * FROM $FAVORITE_LIVE_TABLE")
    fun getFavoriteLives(): Maybe<List<LocalFavoriteLive>>

    @Query("SELECT * FROM $LIVE_TABLE WHERE name LIKE :liveId")
    fun getLive(liveId: String) : Single<LocalLive>

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun putLives(lives: List<LocalLive>)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun addToFavorites(live: LocalFavoriteLive)

    @Delete
    fun removeFromFavorites(live: LocalFavoriteLive)

}