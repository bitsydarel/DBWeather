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

package com.dbeginc.dbweather.news.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentNewsTabBinding
import com.dbeginc.dbweather.news.NewsTabContract
import com.dbeginc.dbweather.news.lives.LivesTabFragment
import com.dbeginc.dbweather.news.newspaper.view.NewsPaperTabFragment
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.snack
import com.google.android.gms.ads.AdRequest
import com.roughike.bottombar.OnTabSelectListener
import javax.inject.Inject


/**
 * Created by darel on 28.05.17.
 * News Tab Fragment
 */
class NewsTabFragment : BaseFragment(), NewsTabContract.NewsTabView, OnTabSelectListener {
    @Inject lateinit var presenter: NewsTabContract.NewsTabPresenter
    private lateinit var binding: FragmentNewsTabBinding
    private val newsPaperTabFragment = NewsPaperTabFragment()
    private val livesTabFragment = LivesTabFragment()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectNewsTabDep(this)
        retainInstance = true
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_Main_NewsTab)),
                R.layout.fragment_news_tab,
                container,
                false)

        return binding.root
    }

    override fun onTabSelected(id: Int) {
        when(id) {
            R.id.tab_articles -> presenter.selectArticles()
            R.id.tab_lives -> presenter.selectLives()
        }
    }

    /************************************  News Tab View Part ************************************/

    override fun setupView() {
        binding.newsTopTabs.setOnTabSelectListener(this)
        setupAds()
    }

    override fun cleanState() = presenter.unBind()

    override fun showArticles() = Navigator.goToNewsPaper(childFragmentManager, livesTabFragment, newsPaperTabFragment, newsPaperTabFragment::class.java.simpleName)

    override fun showLives() = Navigator.goToLives(childFragmentManager, newsPaperTabFragment, livesTabFragment, livesTabFragment::class.java.simpleName)

    override fun showError(message: String) = binding.newsTabLayout.snack(message)

    private fun setupAds() {
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        binding.newsTabAds.loadAd(adRequest)
    }
}
