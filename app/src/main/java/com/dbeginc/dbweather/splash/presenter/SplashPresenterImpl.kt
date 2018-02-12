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

package com.dbeginc.dbweather.splash.presenter

import com.dbeginc.dbweather.splash.view.SplashView
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import io.reactivex.disposables.Disposable

/**
 * Created by Darel Bitsy on 24/04/17.
 *
 * Splash Presenter Implementation
 */
class SplashPresenterImpl(private val model: NewsRepository) : SplashPresenter {
    private var subscription: Disposable? = null

    override fun bind(view: SplashView) = view.setupView()

    override fun unBind() {
        subscription?.dispose()
    }

    override fun onSplashLaunched(view: SplashView) {
        if (view.isFirstRun()) {
            model.defineDefaultSubscribedSources(view.getDefaultSources())
                    .subscribe(view::displayIntroScreen, view::onError)
                    .also { subscription = it }
        } else view.displayMainScreen()
    }
}
