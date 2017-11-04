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
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivitySplashBinding
import com.dbeginc.dbweather.splash.SplashContract
import com.dbeginc.dbweather.utils.holder.ConstantHolder.FIRST_RUN
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.toast
import com.github.florent37.viewanimator.ViewAnimator
import javax.inject.Inject

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

class SplashActivity : BaseActivity(), SplashContract.SplashView {
    @Inject lateinit var presenter: SplashContract.SplashPresenter
    private lateinit var binding: ActivitySplashBinding
    private val defaultSources by lazy { resources.getStringArray(R.array.default_sources) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.injectSplashDep(this)
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

    override fun isFirstRun(): Boolean = preferences.getBoolean(FIRST_RUN, true)

    override fun getDefaultSources(): List<String> = defaultSources.toList()

    override fun setupView() {
        ViewAnimator.animate(binding.appLogo)
                .zoomOut()
                .bounce()
                .zoomIn()
                .andAnimate(binding.poweredByDarkSky)
                .slideRight()
                .slideLeft()
                .andAnimate(binding.madeByMe)
                .slideLeft()
                .slideRight()
                .onStop { presenter.onSplashLaunched() }
                .duration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                .start()
    }

    override fun cleanState() = presenter.unBind()

    override fun showError(message: String) {
        binding.madeByMe.toast(message)
    }
}
