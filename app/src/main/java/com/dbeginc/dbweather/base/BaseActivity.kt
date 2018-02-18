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

package com.dbeginc.dbweather.base

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.utils.helper.ApplicationPreferences
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Darel Bitsy on 27/04/17.
 * Base DBWeather Activity
 */

open class BaseActivity : DaggerAppCompatActivity(), WithDependencies {
    @Inject lateinit var applicationPreferences: ApplicationPreferences
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    protected fun isNetworkAvailable(): Boolean {
        val manager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo: NetworkInfo? = manager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }
}
