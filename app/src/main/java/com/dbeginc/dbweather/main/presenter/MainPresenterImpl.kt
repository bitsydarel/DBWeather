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

package com.dbeginc.dbweather.main.presenter

import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.main.MainContract
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by darel on 29.05.17.
 * Main Presenter Presenter
 */
class MainPresenterImpl() : MainContract.MainPresenter{
    private var view: MainContract.MainView? = null
    private val subscriptions = CompositeDisposable()


    override fun bind(view: MainContract.MainView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscriptions.clear()
        view = null
    }

    override fun onNavigation(screenId: Int) {
        when(screenId) {
            R.id.tab_weather -> view?.goToWeatherScreen()
            R.id.tab_news -> view?.goToNewsScreen()
            R.id.tab_config -> view?.goToConfigurationScreen()
        }
    }
}
