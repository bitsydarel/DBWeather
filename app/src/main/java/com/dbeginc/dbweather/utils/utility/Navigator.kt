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

package com.dbeginc.dbweather.utils.utility

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentManager
import com.dbeginc.dbweather.MainActivity
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.config.managelocations.view.ManageLocationsActivity
import com.dbeginc.dbweather.config.managesources.ManageSourcesActivity
import com.dbeginc.dbweather.config.managesources.sourcedetail.SourceDetailActivity
import com.dbeginc.dbweather.config.view.ConfigurationTabFragment
import com.dbeginc.dbweather.databinding.ArticleItemBinding
import com.dbeginc.dbweather.databinding.LiveItemBinding
import com.dbeginc.dbweather.databinding.SourceItemBinding
import com.dbeginc.dbweather.intro.IntroActivity
import com.dbeginc.dbweather.intro.chooselocation.ChooseLocationsFragment
import com.dbeginc.dbweather.intro.gpslocationfinder.view.GpsLocationFinderFragment
import com.dbeginc.dbweather.news.lives.livedetail.LiveDetailActivity
import com.dbeginc.dbweather.news.newspaper.articledetail.ArticleDetailActivity
import com.dbeginc.dbweather.splash.view.SplashActivity
import com.dbeginc.dbweather.utils.holder.ConstantHolder.*

/**
 * Created by darel on 23.09.17.
 *
 * Navigator Pattern for DBWeather
 */
object Navigator {

    @Synchronized
    fun goToArticleDetail(binding: ArticleItemBinding) {
        val context = binding.articleLayout.context
        val intent = Intent(context, ArticleDetailActivity::class.java)
        intent.putExtra(ARTICLES_DATA, binding.article)
        context.startActivity(intent)
    }

    @Synchronized
    fun goToChooseLocationScreen(fragmentManager: FragmentManager, fragment: ChooseLocationsFragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.introContent, fragment)
                .commit()
    }

    @Synchronized
    fun goToGpsLocationFinder(fragmentManager: FragmentManager, fragment: GpsLocationFinderFragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.introContent, fragment)
                .commit()
    }

    @Synchronized
    fun goToMainScreen(context: Context) {
        context.startActivity(
                Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
    }

    @Synchronized
    fun goToIntroScreen(splash: SplashActivity) {
        splash.startActivity(Intent(splash, IntroActivity::class.java))
        splash.finish()
    }

    @Synchronized
    fun goToLiveDetail(binding: LiveItemBinding) {
        val intent = Intent(binding.root.context, LiveDetailActivity::class.java)
        intent.putExtra(LIVES_DATA, binding.live)
        binding.root.context.startActivity(intent)
    }

    @Synchronized
    fun goToSourceDetail(binding: SourceItemBinding) {
        val intent = Intent(binding.root.context, SourceDetailActivity::class.java)
        intent.putExtra(SOURCE_KEY, binding.source)
        binding.root.context.startActivity(intent)
    }

    @Synchronized
    fun goToManageLocationScreen(configurationTabFragment: ConfigurationTabFragment) {
        configurationTabFragment.startActivity(Intent(configurationTabFragment.context, ManageLocationsActivity::class.java))

        configurationTabFragment.activity.finish()
    }

    @Synchronized
    fun goToManageSourcesScreen(configurationTabFragment: ConfigurationTabFragment) {
        configurationTabFragment.startActivity(Intent(configurationTabFragment.context, ManageSourcesActivity::class.java))

        configurationTabFragment.activity.finish()
    }
}