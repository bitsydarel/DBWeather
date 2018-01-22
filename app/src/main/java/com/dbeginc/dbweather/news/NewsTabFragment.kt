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

package com.dbeginc.dbweather.news

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentNewsTabBinding
import com.dbeginc.dbweather.utils.utility.Injector
import com.google.android.gms.ads.AdRequest
import com.roughike.bottombar.OnTabSelectListener


/**
 * Created by darel on 28.05.17.
 * News Tab Fragment
 */
class NewsTabFragment : BaseFragment(), OnTabSelectListener {
    private lateinit var binding: FragmentNewsTabBinding
    private val pagerAdapter by lazy { NewsTabPagerAdapter(childFragmentManager) }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectNewsTabDep(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_Main_NewsTab)),
                R.layout.fragment_news_tab,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (binding.newsTabContent.adapter == null) binding.newsTabContent.adapter = pagerAdapter

        binding.newsTopTabs.setOnTabSelectListener(this)

        setupAds()
    }

    override fun onTabSelected(id: Int) {
        when(id) {
            R.id.tab_articles -> binding.newsTabContent.setCurrentItem(0, true)
            R.id.tab_lives -> binding.newsTabContent.setCurrentItem(1, true)
        }
    }

    private fun setupAds() {
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("687D1ACC5C0ACF7F698DBA9A4E258FFA")
                .build()
        binding.newsTabAds.loadAd(adRequest)
    }
}
