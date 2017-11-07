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
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatDelegate
import android.view.WindowManager
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityMainBinding
import com.dbeginc.dbweather.main.MainContract
import com.dbeginc.dbweather.main.adapter.MainPagerAdapter
import com.dbeginc.dbweather.main.presenter.MainPresenterImpl
import com.dbeginc.dbweather.utils.holder.ConstantHolder.FIRST_RUN
import com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_CURRENT_LOCATION
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweather.weather.WeatherTabFragment
import com.roughike.bottombar.OnTabSelectListener
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class MainActivity : BaseActivity(), MainContract.MainView, OnTabSelectListener {

    private lateinit var presenter: MainContract.MainPresenter
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

        presenter = MainPresenterImpl()

        adapter = MainPagerAdapter(supportFragmentManager)

        binding.mainNavigationBar.setDefaultTab(R.id.tab_weather)
        binding.mainNavigationBar.setOnTabSelectListener(this)

        binding.tabContent.adapter = adapter

    }

    override fun onNewIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            async(UI) {
                supportFragmentManager
                        .fragments
                        .filterIsInstance(WeatherTabFragment::class.java)
                        .firstOrNull()
                        ?.onVoiceQuery(intent.getStringExtra(SearchManager.QUERY))
            }
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

    override fun onTabSelected(tabId: Int) {
        var color = ResourcesCompat.getColor(resources, R.color.weatherTabPrimaryDark, theme)

        when (tabId) {
            R.id.tab_weather -> binding.tabContent.setCurrentItem(0, true)

            R.id.tab_news -> {
                binding.tabContent.setCurrentItem(1, true)
                color = ResourcesCompat.getColor(resources, R.color.newsTabPrimaryDark, theme)
            }

            R.id.tab_config -> {
                binding.tabContent.setCurrentItem(2, true)
                color = ResourcesCompat.getColor(resources, R.color.configTabPrimaryDark, theme)
            }
        }

        async(UI) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.apply {
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = color
                }
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

}
