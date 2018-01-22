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

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentLivesTabBinding
import com.dbeginc.dbweather.utils.utility.Injector

/**
 * Created by darel on 16.10.17.
 *
 * Lives Fragment
 */
class LivesTabFragment : BaseFragment() {
    private lateinit var binding: FragmentLivesTabBinding
    private lateinit var pagerAdapter: LivesPageAdapter
    private val liveTitle by lazy { getString(R.string.lives_tab_title) }
    private val favoritesTitle by lazy { getString(R.string.favorites_tab_title) }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectLivesTabDep(this)

        pagerAdapter = LivesPageAdapter(liveTitle, favoritesTitle, childFragmentManager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lives_tab, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.livesVP.adapter = pagerAdapter

        binding.livesTabIds.setupWithViewPager(binding.livesVP)
    }
}