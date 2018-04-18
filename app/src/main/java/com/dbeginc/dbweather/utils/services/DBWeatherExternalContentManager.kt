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

package com.dbeginc.dbweather.utils.services

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsClient
import android.support.customtabs.CustomTabsServiceConnection
import android.support.customtabs.CustomTabsSession
import com.dbeginc.dbweather.utils.utility.CUSTOM_TAB_CLIENT_WAIT_TIME
import com.dbeginc.dbweather.utils.utility.getCustomTabsPackages
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.atomic.AtomicReference

object DBWeatherExternalContentManager {
    private val customTabsSession by lazy { AtomicReference<CustomTabsSession?>() }

    fun retrieveSession(): CustomTabsSession? = customTabsSession.get()

    fun initialize(context: Context) {
        launch(context = CommonPool) {
            val packages = getCustomTabsPackages(context = context.applicationContext)

            if (packages.isNotEmpty()) {

                val customTabsServiceConnection = object : CustomTabsServiceConnection() {
                    override fun onCustomTabsServiceConnected(name: ComponentName?, client: CustomTabsClient) {
                        client.warmup(CUSTOM_TAB_CLIENT_WAIT_TIME)

                        customTabsSession.set(client.newSession(null))
                    }

                    override fun onServiceDisconnected(name: ComponentName?) { /*Not interested*/
                    }
                }

                CustomTabsClient.bindCustomTabsService(
                        context.applicationContext,
                        packages.asSequence().find { it.contains("chrome") } ?: packages.first(),
                        customTabsServiceConnection
                )
            }
        }
    }

    fun prepareBrowserForUrl(url: String) {
        customTabsSession.get()?.mayLaunchUrl(
                Uri.parse(url),
                null,
                null
        )
    }

}