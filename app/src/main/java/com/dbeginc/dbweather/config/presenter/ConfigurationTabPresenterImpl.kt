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

package com.dbeginc.dbweather.config.presenter

import com.dbeginc.dbweather.config.view.ConfigurationTabView
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweathercommon.utils.onError
import com.dbeginc.dbweatherdomain.repositories.configurations.ConfigurationsRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction

/**
 * Created by darel on 24.10.17.
 *
 * Configuration Presenter Implementation
 */
class ConfigurationTabPresenterImpl(private val model: ConfigurationsRepository) : ConfigurationTabPresenter {
    private val subscriptions = CompositeDisposable()

    override fun bind(view: ConfigurationTabView) = view.setupView()

    override fun unBind() = subscriptions.clear()

    override fun loadConfigurations(view: ConfigurationTabView) {
        model.getWeatherNotificationStatus()
                .zipWith(
                        model.getNewsPapersTranslationStatus(),
                        BiFunction<Boolean, Boolean, Pair<Boolean, Boolean>> { weatherStatus, newsPaperTranslationStatus ->
                            weatherStatus to newsPaperTranslationStatus
                        }
                )
                .subscribe(
                        { status ->
                            view.displayWeatherNotificationStatus(status.first)
                            view.displayNewsPaperTranslationStatus(status.second)
                        },
                        view::onError
                ).addTo(subscriptions)
    }

    override fun onManageLocation(view: ConfigurationTabView) = view.goToManageLocationScreen()

    override fun onManageSources(view: ConfigurationTabView) = view.goToManageSourcesScreen()

    override fun onWeatherNotification(view: ConfigurationTabView, isOn: Boolean) {
        model.changeWeatherNotificationStatus(isOn)
                .doOnError { view.displayWeatherNotificationStatus(isOn.not()) }
                .subscribe({ view.displayWeatherNotificationStatus(isOn) }, view::onError)
                .addTo(subscriptions)
    }

    override fun onNewsPaperTranslation(view: ConfigurationTabView, isOn: Boolean) {
        model.changeNewsPapersTranslationStatus(isOn)
                .doOnError { view.displayNewsPaperTranslationStatus(isOn.not()) }
                .subscribe({ view.displayNewsPaperTranslationStatus(isOn) }, view::onError)
                .addTo(subscriptions)
    }
}