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

import android.app.SearchManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatDelegate
import android.widget.Toast
import com.dbeginc.dbweather.adapter.MainPagerAdapter
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityMainBinding
import com.dbeginc.dbweather.di.WithChildDependencies
import com.dbeginc.dbweather.utils.utility.changeStatusBarColor
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweather.weather.WeatherTabFragment
import com.roughike.bottombar.OnTabSelectListener

class MainActivity : BaseActivity(), OnTabSelectListener, WithChildDependencies {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainPagerAdapter

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

        adapter = MainPagerAdapter(supportFragmentManager)

        binding.mainNavigationBar.setDefaultTab(R.id.tab_weather)

        binding.mainNavigationBar.setOnTabSelectListener(this, false)

        binding.mainNavigationBar.selectTabWithId(DBWeatherApp.LAST_SCREEN.toInt())

        binding.tabContent.adapter = adapter

        if (!isNetworkAvailable()) showNetworkNotAvailable()

        if (applicationPreferences.isFirstLaunchOfApplication()) applicationPreferences.changeFirstLaunchStatus()
    }

    override fun onNewIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            supportFragmentManager.fragments.forEach {
                (it as? WeatherTabFragment)?.onVoiceQuery(
                        intent.getStringExtra(SearchManager.QUERY)
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        applicationPreferences.changeDefaultLocationStatus(true)
    }

    override fun onTabSelected(tabId: Int) {
        changeVisibleTab(tabId)
        changeTabColor(tabId)
    }

    /************************** Main View Custom Part **************************/
    private fun changeVisibleTab(tabToShow: Int) {
        when (tabToShow) {
            R.id.tab_weather -> goToWeatherScreen()
            R.id.tab_news -> goToNewsScreen()
            R.id.tab_config -> goToConfigurationScreen()
        }
    }

    private fun changeTabColor(tabToShow: Int) {
        var color = ResourcesCompat.getColor(resources, R.color.weatherTabPrimaryDark, theme)

        when (tabToShow) {
            R.id.tab_news -> color = ResourcesCompat.getColor(resources, R.color.newsTabPrimaryDark, theme)
            R.id.tab_config -> color = ResourcesCompat.getColor(resources, R.color.configTabPrimaryDark, theme)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.changeStatusBarColor(color)
    }

    private fun goToWeatherScreen() {
        binding.tabContent.post {
            binding.tabContent.setCurrentItem(0, true)
        }

        DBWeatherApp.LAST_SCREEN = R.id.tab_weather.toLong()
    }

    private fun goToNewsScreen() {
        binding.tabContent.post {
            binding.tabContent.setCurrentItem(1, true)
        }

        DBWeatherApp.LAST_SCREEN = R.id.tab_news.toLong()
    }

    private fun goToConfigurationScreen() {
        binding.tabContent.post {
            binding.tabContent.setCurrentItem(2, true)
        }

        DBWeatherApp.LAST_SCREEN = R.id.tab_config.toLong()
    }

    private fun showNetworkNotAvailable() = binding.mainLayout.toast(getString(R.string.network_unavailable_message), duration = Toast.LENGTH_LONG)

}
