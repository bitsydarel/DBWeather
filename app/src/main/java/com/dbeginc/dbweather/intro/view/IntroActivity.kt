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

package com.dbeginc.dbweather.intro.view

import android.app.SearchManager
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityIntroBinding
import com.dbeginc.dbweather.intro.IntroContract
import com.dbeginc.dbweather.intro.chooselocation.view.ChooseLocationFragment
import com.dbeginc.dbweather.intro.gpslocationfinder.view.GpsLocationFinderFragment
import com.dbeginc.dbweather.utils.utility.*
import javax.inject.Inject

/**
 * Introduction View Implementation
 */
class IntroActivity : BaseActivity(), IntroContract.IntroView {
    @Inject lateinit var presenter: IntroContract.IntroPresenter
    private lateinit var binding: ActivityIntroBinding
    private val chooseLocation: ChooseLocationFragment = ChooseLocationFragment()
    private val gpsLocationFinder : GpsLocationFinderFragment = GpsLocationFinderFragment()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectIntroDep(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent?.action) {
            chooseLocation.onSearchQuery(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    /********************************  Intro View Custom Part ********************************/

    override fun setupView() {
        if (!binding.introContent.isVisible()) {
            binding.locationAnimation.show()
            binding.locationAnimation.playAnimation()
        }
        binding.chooseLocationBtn.setOnClickListener { presenter.onUserChoose(0) }
        binding.useGpsLocationBtn.setOnClickListener { presenter.onUserChoose(1) }
    }

    override fun cleanState() {
        binding.locationAnimation.cancelAnimation()
        binding.locationAnimation.hide()
        presenter.unBind()
    }

    override fun displayChooseLocation() {
        hideSelectionView()
        Navigator.goToChooseLocationScreen(supportFragmentManager, chooseLocation)
    }

    override fun displayGpsLocationFinder() {
        hideSelectionView()
        Navigator.goToGpsLocationFinder(supportFragmentManager, gpsLocationFinder)
    }

    private fun hideSelectionView() {
        binding.introContent.show()
        binding.locationAnimation.hide()
        binding.useGpsLocationBtn.hide()
        binding.chooseLocationBtn.hide()
    }
}
