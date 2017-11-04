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

package com.dbeginc.dbweather.news.lives

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dbeginc.dbweather.news.lives.page.alllives.view.AllLivesTabPageFragment
import com.dbeginc.dbweather.news.lives.page.favorite.view.FavoriteLivesTabFragment

/**
 * Created by darel on 18.10.17.
 *
 * Lives Page Adapter
 */
class LivesPageAdapter(private val liveTitle: String, private val favoriteTitle:String, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> AllLivesTabPageFragment()
            1 -> FavoriteLivesTabFragment()
            else -> AllLivesTabPageFragment()
        }
    }

    override fun getCount(): Int = 2

    override fun getItemId(position: Int): Long {
        return when(position) {
            0 -> 0
            1 -> 1
            else -> 0
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when(position) {
            0 -> liveTitle
            1 -> favoriteTitle
            else -> liveTitle
        }
    }
}