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

package com.dbeginc.dbweather.main.view

import android.app.SearchManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatDelegate
import android.view.WindowManager
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.config.view.ConfigurationTabFragment
import com.dbeginc.dbweather.databinding.ActivityMainBinding
import com.dbeginc.dbweather.main.MainContract
import com.dbeginc.dbweather.main.presenter.MainPresenterImpl
import com.dbeginc.dbweather.news.view.NewsTabFragment
import com.dbeginc.dbweather.utils.holder.ConstantHolder.FIRST_RUN
import com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_CURRENT_LOCATION
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweather.weather.WeatherTabFragment
import com.roughike.bottombar.OnTabSelectListener
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class MainActivity : BaseActivity(), MainContract.MainView, OnTabSelectListener {

    private lateinit var presenter: MainContract.MainPresenter
    private lateinit var binding: ActivityMainBinding
    private val weatherTabFragment = WeatherTabFragment()
    private val newsTabFragment = NewsTabFragment()
    private val configurationFragment = ConfigurationTabFragment()
    private var lastVisibleFragment: Int = R.id.tab_weather

    companion object {
        init {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
            }
        }

        private val LAST_VISIBLE_PAGE = "last_visible_page"
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        presenter = MainPresenterImpl()

        binding.bottomBar.setOnTabSelectListener(this)
        binding.bottomBar.selectTabWithId(R.id.tab_weather)
        binding.bottomBar.setDefaultTab(R.id.tab_weather)

        if (savedState != null) {
            lastVisibleFragment = savedState.getInt(LAST_VISIBLE_PAGE, R.id.tab_weather)
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            if (query.isNotEmpty()) weatherTabFragment.onVoiceQuery(query)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(LAST_VISIBLE_PAGE, lastVisibleFragment)
    }

    override fun onTabSelected(id: Int) {
        async(UI) {
            val statusBarColor = bg {

                var color = ResourcesCompat.getColor(resources, R.color.weatherTabPrimary, theme)

                val currentVisiblePage: Fragment = when(lastVisibleFragment) {
                    R.id.tab_weather -> weatherTabFragment
                    R.id.tab_news -> newsTabFragment
                    R.id.tab_config -> configurationFragment
                    else -> weatherTabFragment
                }

                when (id) {
                    R.id.tab_weather -> {
                        showWeatherPage(currentVisiblePage)
                        lastVisibleFragment = R.id.tab_weather
                    }
                    R.id.tab_news -> {
                        showNewsPage(currentVisiblePage)
                        color = ResourcesCompat.getColor(resources, R.color.newsTabPrimaryDark, theme)
                        lastVisibleFragment = R.id.tab_news
                    }
                    R.id.tab_config -> {
                        showConfigurationPage(currentVisiblePage)
                        color = ResourcesCompat.getColor(resources, R.color.configTabPrimaryDark, theme)
                        lastVisibleFragment = R.id.tab_config
                    }
                }

                return@bg color
            }.await()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = statusBarColor
            }
        }
    }

    /************************** Main View Custom Part **************************/

    override fun setupView() {
        if (!isNetworkAvailable) showNetworkNotAvailable()

        if (preferences.getBoolean(FIRST_RUN, true)) {
            preferences.edit().putBoolean(FIRST_RUN, false).apply()
        }
    }

    override fun cleanState() {
        preferences.edit().putBoolean(IS_CURRENT_LOCATION, true).apply()
        presenter.unBind()
    }

    override fun showNetworkNotAvailable() = binding.mainLayout.toast(getString(R.string.network_unavailable_message))

    private fun showWeatherPage(currentVisiblePage: Fragment) {
        val currentPage = supportFragmentManager.findFragmentByTag(WeatherTabFragment::class.java.simpleName)

        if (currentPage == weatherTabFragment && currentVisiblePage != weatherTabFragment) {
            supportFragmentManager.beginTransaction()
                    .hide(currentVisiblePage)
                    .show(weatherTabFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()

        } else if (currentPage == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.tabContent, weatherTabFragment, WeatherTabFragment::class.java.simpleName)
                    .show(weatherTabFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
    }

    private fun showNewsPage(currentVisiblePage: Fragment) {
        val currentPage = supportFragmentManager.findFragmentByTag(NewsTabFragment::class.java.simpleName)

        if (currentPage == newsTabFragment && currentVisiblePage != newsTabFragment) {
            supportFragmentManager.beginTransaction()
                    .hide(currentVisiblePage)
                    .show(newsTabFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()

        } else if (currentPage == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.tabContent, newsTabFragment, NewsTabFragment::class.java.simpleName)
                    .hide(currentVisiblePage)
                    .show(newsTabFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
    }

    private fun showConfigurationPage(currentVisiblePage: Fragment) {
        val currentPage = supportFragmentManager.findFragmentByTag(ConfigurationTabFragment::class.java.simpleName)

        if (currentPage == configurationFragment && currentVisiblePage != configurationFragment) {
            supportFragmentManager.beginTransaction()
                    .hide(currentVisiblePage)
                    .show(configurationFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        } else if (currentPage == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.tabContent, configurationFragment, ConfigurationTabFragment::class.java.simpleName)
                    .hide(currentVisiblePage)
                    .show(configurationFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
    }
}
