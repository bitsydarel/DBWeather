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

package com.dbeginc.dbweather.intro

import android.app.SearchManager
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ActivityIntroBinding
import com.dbeginc.dbweather.di.WithChildDependencies
import com.dbeginc.dbweather.intro.chooselocation.ChooseLocationsFragment
import com.dbeginc.dbweather.intro.gpslocationfinder.view.GpsLocationFinderFragment
import com.dbeginc.dbweather.utils.helper.ApplicationPreferences
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.hide
import com.dbeginc.dbweather.utils.utility.isNotVisible
import com.dbeginc.dbweather.utils.utility.show
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Introduction View Implementation
 */
class IntroActivity : DaggerAppCompatActivity(), WithChildDependencies {
    @Inject
    lateinit var applicationPreferences: ApplicationPreferences
    private lateinit var binding: ActivityIntroBinding
    private val chooseLocation: ChooseLocationsFragment = ChooseLocationsFragment()
    private val gpsLocationFinder : GpsLocationFinderFragment = GpsLocationFinderFragment()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)

        if (binding.introContent.isNotVisible()) {
            binding.locationAnimation.show()

            binding.locationAnimation.playAnimation()
        }

        binding.chooseLocationBtn.setOnClickListener { displayChooseLocation() }

        binding.useGpsLocationBtn.setOnClickListener { displayGpsLocationFinder() }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.locationAnimation.cancelAnimation()

        binding.locationAnimation.hide()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (Intent.ACTION_SEARCH == intent?.action) {
            chooseLocation.onSearchQuery(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    /********************************  Intro View Custom Part ********************************/
    private fun displayChooseLocation() {
        hideSelectionView()

        Navigator.goToChooseLocationScreen(supportFragmentManager, chooseLocation)
    }

    private fun displayGpsLocationFinder() {
        hideSelectionView()

        Navigator.goToGpsLocationFinder(supportFragmentManager, gpsLocationFinder)
    }

    private fun hideSelectionView() {
        binding.introContent.show()

        binding.locationAnimation.hide()

        binding.useGpsLocationBtn.hide()

        binding.chooseLocationBtn.hide()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ConstantHolder.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            applicationPreferences.changeGpsPermissionStatus(grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED)
            gpsLocationFinder.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
