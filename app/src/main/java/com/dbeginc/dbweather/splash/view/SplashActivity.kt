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

package com.dbeginc.dbweather.splash.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ActivitySplashBinding
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.splash.presenter.SplashPresenter
import com.dbeginc.dbweather.utils.helper.ApplicationPreferences
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.toast
import com.github.florent37.viewanimator.ViewAnimator
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

/**
 * Created by Darel Bitsy on 13/02/17.
 *
 * Welcome screen and initializer
 */
class SplashActivity : DaggerAppCompatActivity(), SplashView, WithDependencies {
    private val defaultSources by lazy { resources.getStringArray(R.array.default_sources) }
    @Inject
    lateinit var applicationPreferences: ApplicationPreferences
    private lateinit var binding: ActivitySplashBinding
    @Inject
    lateinit var presenter: SplashPresenter

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()

        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()

        cleanState()
    }

    /********************************** Splash Custom View Part **********************************/

    override fun displayMainScreen() {
        Navigator.goToMainScreen(this)

        finish()
    }

    override fun displayIntroScreen() = Navigator.goToIntroScreen(this)

    override fun isFirstRun(): Boolean = applicationPreferences.isFirstLaunchOfApplication()

    override fun getDefaultSources(): List<String> = defaultSources.toList()

    override fun setupView() {
        ViewAnimator.animate(binding.appLogo)
                .zoomOut()
                .bounce()
                .zoomIn()
                .onStop { presenter.onSplashLaunched(this) }
                .duration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .start()
    }

    override fun cleanState() = presenter.unBind()

    override fun showMessage(message: String) = binding.madeByMe.toast(message)
}
