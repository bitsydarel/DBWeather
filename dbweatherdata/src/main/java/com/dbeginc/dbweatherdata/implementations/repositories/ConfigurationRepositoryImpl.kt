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

package com.dbeginc.dbweatherdata.implementations.repositories

import android.content.Context
import android.content.SharedPreferences
import com.dbeginc.dbweatherdata.ThreadProvider
import com.dbeginc.dbweatherdomain.repositories.configurations.ConfigurationsRepository
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by darel on 26.10.17.
 *
 * Configuration Repository Implementation
 */
class ConfigurationRepositoryImpl private constructor(private val preferences: SharedPreferences, private val thread: ThreadProvider) : ConfigurationsRepository {

    companion object {
        private val PREFS_NAME = "dbweather_configurations"
        private val WEATHER_NOTIFICATION = "weather_notification"
        private val NEWS_PAPER_TRANSLATION = "newspaper_notification"

        fun create(context: Context): ConfigurationsRepository {
            return ConfigurationRepositoryImpl(
                    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE),
                    ThreadProvider
            )
        }
    }

    override fun getWeatherNotificationStatus(): Single<Boolean> {
        return Single.fromCallable { preferences.getBoolean(WEATHER_NOTIFICATION, false) }
                .observeOn(thread.ui)
                .subscribeOn(thread.io)
    }

    override fun getNewsPapersTranslationStatus(): Single<Boolean> {
        return Single.fromCallable { preferences.getBoolean(NEWS_PAPER_TRANSLATION, false) }
                .observeOn(thread.ui)
                .subscribeOn(thread.io)
    }

    override fun changeWeatherNotificationStatus(enable: Boolean): Completable {
        return Completable.fromAction {
            preferences.edit().putBoolean(WEATHER_NOTIFICATION, enable).apply()

        }.observeOn(thread.ui).subscribeOn(thread.io).unsubscribeOn(thread.io)
    }

    override fun changeNewsPapersTranslationStatus(shouldTranslate: Boolean): Completable {
        return Completable.fromAction {
            preferences.edit().putBoolean(NEWS_PAPER_TRANSLATION, shouldTranslate).apply()

        }.observeOn(thread.ui).subscribeOn(thread.io).unsubscribeOn(thread.io)
    }

    override fun clean() {/*** Not Needed ***/}

}