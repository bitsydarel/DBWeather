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

package com.dbeginc.dbweather

import android.app.SearchManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.widget.Toast
import com.dbeginc.dbweather.R.id.main_content
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityMainBinding
import com.dbeginc.dbweather.utils.services.DBWeatherCustomTabManager
import com.dbeginc.dbweather.utils.utility.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    companion object {
        init {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            }
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.mainNavView.setNavigationItemSelectedListener(this)

        if (savedState == null) goToWeatherScreen(container = this, layoutId = main_content)

        if (!isNetworkAvailable()) showNetworkNotAvailable()

        if (preferences.isFirstLaunchOfApplication())
            preferences.changeFirstLaunchStatus()
    }

    override fun onStart() {
        super.onStart()

        DBWeatherCustomTabManager.initialize(applicationContext)
    }

    override fun onNewIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val currentFragment = supportFragmentManager.findFragmentById(main_content)

            (currentFragment as? WithSearchableData)?.let {
                val query = intent.getStringExtra(SearchManager.QUERY)
                currentFragment.onSearchQuery(query = query)
            }
        }
    }

    override fun onBackPressed() {
        if (binding.mainLayout.isDrawerOpen(GravityCompat.START)) binding.mainLayout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.changeDefaultLocationStatus(true)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.weather_feature -> {
                goToWeatherScreen(container = this, layoutId = main_content)
            }
            R.id.news_papers_feature -> goToNewsPapersScreen(
                    container = this,
                    layoutId = main_content
            )
            R.id.youtube_lives_feature -> goToYoutubeLivesScreen(
                    container = this,
                    layoutId = main_content
            )
            R.id.ip_tv_feature -> goToIpTvPlaylistsScreen(
                    container = this,
                    layoutId = main_content
            )
            R.id.favoriteYoutubeLives -> goToFavoriteYoutubeLivesScreen(
                    container = this,
                    layoutId = main_content
            )
            R.id.manage_locations -> goToManageLocationsScreen(container = this, layoutId = main_content)
            R.id.manage_sources -> goToManageNewsPapersScreen(
                    container = this,
                    layoutId = main_content
            )
        }

        binding.mainLayout.closeDrawer(GravityCompat.START, true)

        return true
    }

    fun openNavigationDrawer() = binding.mainLayout.openDrawer(GravityCompat.START)

    private fun showNetworkNotAvailable() = binding.mainLayout.toast(
            resId = R.string.network_unavailable_message,
            duration = Toast.LENGTH_LONG
    )
}
