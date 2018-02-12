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

package com.dbeginc.dbweather.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dbeginc.dbweather.config.view.ConfigurationTabFragment
import com.dbeginc.dbweather.news.NewsTabFragment
import com.dbeginc.dbweather.weather.WeatherTabFragment

/**
 * Created by darel on 06.11.17.
 *
 * Main pager adapter
 */
class MainPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WeatherTabFragment()
            1 -> NewsTabFragment()
            2 -> ConfigurationTabFragment()
            else -> WeatherTabFragment()
        }
    }

    override fun getCount(): Int = 3

}