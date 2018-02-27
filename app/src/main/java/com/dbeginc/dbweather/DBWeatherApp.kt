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

package com.dbeginc.dbweather

import android.os.StrictMode
import android.support.annotation.IntDef
import com.crashlytics.android.Crashlytics
import com.dbeginc.dbweather.di.components.DaggerApplicationComponent
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweathercommon.utils.LogDispatcher
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Darel Bitsy on 24/04/17.
 *
 * Class representing the Application class
 */
class DBWeatherApp : DaggerApplication(), HasActivityInjector {

    companion object {
        val WEATHER_SEARCH_RESULTS: BehaviorSubject<List<LocationWeatherModel>> = BehaviorSubject.create()

        @IntDef(R.id.tab_weather.toLong(), R.id.tab_news.toLong(), R.id.tab_config.toLong())
        @Retention(AnnotationRetention.SOURCE)
        annotation class AppScreen

        @AppScreen
        var LAST_SCREEN: Long = R.id.tab_weather.toLong()
    }

    override fun onCreate() {
        super.onCreate()

        Fabric.with(this, Crashlytics())

        AndroidThreeTen.init(this)

        FirebaseApp.initializeApp(this)

        MobileAds.initialize(this, BuildConfig.MobileAds)

        RxJavaPlugins.setErrorHandler { LogDispatcher.logError(it) }

        if (BuildConfig.DEBUG) {
            StrictMode.noteSlowCall("dbweather")

            StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog()
                            .penaltyDeath()
                            .build()
            )

            StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder()
                            .setClassInstanceLimit(MainActivity::class.java, 2)
                            .detectAll()
                            .penaltyLog()
                            .penaltyDeath()
                            .build()
            )
        }

        Injector.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>? {
        return DaggerApplicationComponent.builder().create(this)
    }
}
