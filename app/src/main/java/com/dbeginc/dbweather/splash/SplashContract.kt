package com.dbeginc.dbweather.splash

import com.dbeginc.dbweather.base.IPresenter
import com.dbeginc.dbweather.base.IView

/**
 * Created by Darel Bitsy on 24/04/17.
 *
 * Splash Contract
 */

interface SplashContract {

    interface SplashView : IView {
        fun displayMainScreen()

        fun isFirstRun() : Boolean

        fun displayIntroScreen()

        fun getDefaultSources(): List<String>

        fun showError(message: String)
    }

    interface SplashPresenter : IPresenter<SplashView> {
        fun onSplashLaunched()
    }
}
