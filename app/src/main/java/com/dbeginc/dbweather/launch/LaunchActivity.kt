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

package com.dbeginc.dbweather.launch

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.utils.utility.WithSearchableData
import com.dbeginc.dbweather.utils.utility.goToSplashScreen

/**
 * Introduction View Implementation
 */
class LaunchActivity : BaseActivity() {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        setContentView(R.layout.activity_launch)

        if (savedState == null)
            goToSplashScreen(
                    container = this,
                    layoutId = R.id.launchContent
            )

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (Intent.ACTION_SEARCH == intent?.action) {
            val foundedFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.launchContent)

            if (foundedFragment is WithSearchableData) {
                foundedFragment.onSearchQuery(intent.getStringExtra(SearchManager.QUERY))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        supportFragmentManager.findFragmentById(R.id.launchContent)
                ?.onRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults
                )
    }
}
