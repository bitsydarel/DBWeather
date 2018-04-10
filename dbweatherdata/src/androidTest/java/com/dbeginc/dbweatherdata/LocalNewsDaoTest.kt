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

package com.dbeginc.dbweatherdata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import com.dbeginc.dbweatherdata.implementations.datasources.local.news.RoomNewsDatabase
import com.dbeginc.dbweatherdata.proxies.local.lives.LocalYoutubeLive
import com.dbeginc.dbweatherdata.proxies.local.news.LocalArticle
import com.dbeginc.dbweatherdata.proxies.local.news.LocalNewsPaper
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFailsWith

/**
 * Created by darel on 20.10.17.
 *
 * Local News Dao Test
 */
class LocalNewsDaoTest {
    @Rule @JvmField val instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var db: RoomNewsDatabase

    @Before
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), RoomNewsDatabase::class.java).allowMainThreadQueries().build()
    }

    @After
    fun closeDB() { db.close() }

    @Test
    fun should_insert_articles_get_inserted_articles_and_get_specific_article() {
        val androidSource = LocalNewsPaper("android-source", "Android NewsPaper", "News About android", "https://developer.android.com", "Development", "en", "us", false)
        val iosSource = LocalNewsPaper("ios-source", "Ios NewsPaper", "News About ios", "https://developer.apple.com/", "Development", "en", "us", false)

        val androidArticle1 = LocalArticle("Darel Bitsy", "Android spaceship", "no description", "androidUrl1", "", null, androidSource.id)
        val androidArticle2 = LocalArticle("D Bitsy", "Android spaceship", "short desc", "androidUrl2", "", "2017-10-03T15:27:02Z", androidSource.id)
        val iosArticle1 = LocalArticle("Darel Bitsy", "IOS spaceship", "", "iosUrl1", "", "2017-10-03T15:27:02Z", iosSource.id)

        /**
         * Before getting a articles we need to have the corresponding article source in the database
         */
        db.newsDao().putSources(listOf(androidSource, iosSource))

        db.newsDao().putArticles(listOf(androidArticle1, iosArticle1, androidArticle2))

        db.newsDao()
                .getArticles(listOf(androidSource.id))
                .test()
                .assertValue { articles -> articles.size == 2 }

        db.newsDao()
                .getArticle(androidSource.id, androidArticle1.url)
                .test()
                .assertValue { article -> article == androidArticle1 }

    }

    @Test
    fun should_not_insert_article_if_source_not_in_Db() {
        val sourceId = "android-source"

        val article1 = LocalArticle("Darel Bitsy", "Android spaceship", "no description", "androidUrl1", "", null, sourceId)

        assertFailsWith(SQLiteConstraintException::class) {
            db.newsDao().putArticles(listOf(article1))
        }
    }

    @Test
    fun should_insert_sources_and_get_inserted_sources() {
        val androidSource = LocalNewsPaper("android-source", "Android NewsPaper", "Android stuff", "https://developer.android.com", "Development", "en", "us", false)
        val iosSource = androidSource.copy(id = "ios-source", name = "Ios NewsPaper", description = "Ios Stuff", url = "https://developer.apple.com/")

        db.newsDao().putSources(listOf(androidSource, iosSource))

        db.newsDao()
                .getNewsPapers()
                .test()
                .assertValue { sources -> sources == listOf(androidSource, iosSource) }

        db.newsDao()
                .getSource(androidSource.id)
                .test()
                .assertValue { source -> source == androidSource }

    }

    @Test
    fun should_insert_sources_and_get_subscribed_sources() {
        val androidSource = LocalNewsPaper("android-source", "Android NewsPaper", "Android stuff", "https://developer.android.com", "Development", "en", "us", false)
        val iosSource = androidSource.copy(id = "ios-source", name = "Ios NewsPaper", description = "Ios Stuff", url = "https://developer.apple.com")
        val windowsSource = androidSource.copy(id = "windows-source", name = "Windows NewsPaper", description = "Windows Stuff", url = "https://developer.microsoft.com")

        db.newsDao().putSources(listOf(androidSource, iosSource, windowsSource))

        androidSource.subscribed = true

        db.newsDao().updateNewsPaper(androidSource)

        windowsSource.subscribed = true

        db.newsDao().updateNewsPaper(windowsSource)

        db.newsDao()
                .getSubscribedNewsPapers()
                .test()
                .assertValue { sources ->
                    sources.size == 2 && sources.contains(androidSource)
                            && sources.contains(windowsSource) && sources.indexOf(iosSource) == -1
                }
    }

    @Test
    fun should_insert_lives_and_get_lives() {
        val france24 = LocalYoutubeLive(name = "France 24", url = "hL0sEdVJs3U")
    }

}