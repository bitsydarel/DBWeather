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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ArticleItemBinding
import com.dbeginc.dbweather.databinding.DailyListItemBinding
import com.dbeginc.dbweather.databinding.LiveItemBinding
import com.dbeginc.dbweather.intro.chooselocation.view.ChooseLocationFragment
import com.dbeginc.dbweather.intro.gpslocationfinder.view.GpsLocationFinderFragment
import com.dbeginc.dbweather.intro.view.IntroActivity
import com.dbeginc.dbweather.main.view.MainActivity
import com.dbeginc.dbweather.news.lives.LivesTabFragment
import com.dbeginc.dbweather.news.lives.livedetail.view.LiveDetailActivity
import com.dbeginc.dbweather.news.newspaper.articledetail.view.ArticleDetailActivity
import com.dbeginc.dbweather.news.newspaper.view.NewsPaperTabFragment
import com.dbeginc.dbweather.splash.view.SplashActivity
import com.dbeginc.dbweather.utils.holder.ConstantHolder.*
import com.dbeginc.dbweather.weather.daydetail.DayDetailActivity

/**
 * Created by darel on 23.09.17.
 *
 * Navigator Pattern for DBWeather
 */
object Navigator {
    fun goToDayDetailScreen(binding: DailyListItemBinding) {
        val context = binding.dayLayout.context
        val intent = Intent(context, DayDetailActivity::class.java)
        intent.putExtra(DAY_DATA, binding.day)
        context.startActivity(intent)
    }

    fun goToArticleDetail(binding: ArticleItemBinding) {
        val context = binding.articleLayout.context
        val intent = Intent(context, ArticleDetailActivity::class.java)
        intent.putExtra(ARTICLES_DATA, binding.article)
        context.startActivity(intent)
    }

    fun goToChooseLocationScreen(fragmentManager: FragmentManager, fragment: ChooseLocationFragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.introContent, fragment)
                .commit()
    }

    fun goToGpsLocationFinder(fragmentManager: FragmentManager, fragment: GpsLocationFinderFragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.introContent, fragment)
                .commit()
    }

    fun goToMainScreen(context: Context) {
        val mainIntent = Intent(context, MainActivity::class.java)
        context.startActivity(mainIntent)
    }

    fun goToIntroScreen(splash: SplashActivity) {
        val intro = Intent(splash, IntroActivity::class.java)
        splash.startActivity(intro)
        splash.finish()
    }

    fun goToNewsPaper(childFragmentManager: FragmentManager?, currentVisibleFragment: Fragment, articlesTabFragment: NewsPaperTabFragment, tag: String) {
        val fragment = childFragmentManager?.findFragmentByTag(tag)
        val transaction = childFragmentManager?.beginTransaction()

        if (fragment == articlesTabFragment ) {
            transaction
                    ?.show(articlesTabFragment)
                    ?.hide(currentVisibleFragment)

        } else if (fragment == null) {
            transaction
                    ?.add(R.id.newsTabContent, articlesTabFragment, tag)
                    ?.show(articlesTabFragment)
        }

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)?.commit()
    }

    fun goToLives(childFragmentManager: FragmentManager?, currentVisibleFragment: Fragment, livesTabFragment: LivesTabFragment, tag: String) {
        val fragment = childFragmentManager?.findFragmentByTag(tag)
        val transaction = childFragmentManager?.beginTransaction()

        if (fragment == livesTabFragment ) {
            transaction
                    ?.show(livesTabFragment)
                    ?.hide(currentVisibleFragment)

        } else if (fragment == null) {
            transaction
                    ?.add(R.id.newsTabContent, livesTabFragment, tag)
                    ?.hide(currentVisibleFragment)
                    ?.show(livesTabFragment)
        }

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)?.commit()
    }

    fun goToLiveDetail(binding: LiveItemBinding) {
        val intent = Intent(binding.root.context, LiveDetailActivity::class.java)
        intent.putExtra(LIVES_DATA, binding.live)
        binding.root.context.startActivity(intent)
    }
}