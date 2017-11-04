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

import com.dbeginc.dbweather.config.ConfigurationTabContract
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.ChangeWeatherNotificationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetNewsPaperTranslationStatus
import com.dbeginc.dbweatherdomain.usecases.configurations.GetWeatherNotificationStatus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction

/**
 * Created by darel on 24.10.17.
 *
 * Configuration Presenter Implementation
 */
class ConfigurationTabPresenterImpl(private val getWeatherNotificationStatus: GetWeatherNotificationStatus,
                                    private val getNewsPaperTranslationStatus: GetNewsPaperTranslationStatus,
                                    private val changeWeatherNotificationStatus: ChangeWeatherNotificationStatus,
                                    private val changeNewsPaperTranslationStatus: ChangeNewsPaperTranslationStatus) : ConfigurationTabContract.ConfigurationTabPresenter {

    private var view: ConfigurationTabContract.ConfigurationTabView? = null
    private val subscriptions = CompositeDisposable()

    override fun bind(view: ConfigurationTabContract.ConfigurationTabView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        subscriptions.clear()
        view = null
    }

    override fun loadConfigurations() {
        getWeatherNotificationStatus.execute(Unit)
                .zipWith(getNewsPaperTranslationStatus.execute(Unit), BiFunction<Boolean, Boolean, Pair<Boolean, Boolean>> { weatherStatus, newsPaperTranslationStatus -> weatherStatus.to(newsPaperTranslationStatus) })
                .doOnSubscribe { view?.showUpdatingStatus() }
                .doAfterTerminate { view?.hideUpdatingStatus() }
                .subscribe(
                        { status -> 
                            view?.displayWeatherNotificationStatus(status.first)
                            view?.displayNewsPaperTranslationStatus(status.second)
                        }, 
                        { error -> view?.showError(error.localizedMessage) }
                ).addTo(subscriptions)
    }

    override fun onManageLocation() {
        view?.goToManageLocationScreen()
    }

    override fun onManageSources() {
        view?.goToManageSourcesScreen()
    }

    override fun onHelp() {
        view?.goToHelpScreen()
    }

    override fun onWeatherNotification(isOn: Boolean) {
        changeWeatherNotificationStatus.execute(isOn)
                .doOnSubscribe { view?.showUpdatingStatus() }
                .doAfterTerminate { view?.hideUpdatingStatus() }
                .subscribe(
                        { view?.showStatusChanged() },
                        { error ->
                            view?.displayWeatherNotificationStatus(isOn.not())
                            view?.showError(error.localizedMessage)
                        }
                ).addTo(subscriptions)
    }

    override fun onNewsPaperTranslation(isOn: Boolean) {
        changeNewsPaperTranslationStatus.execute(isOn)
                .doOnSubscribe { view?.showUpdatingStatus() }
                .doAfterTerminate { view?.hideUpdatingStatus() }
                .subscribe(
                        { view?.showStatusChanged() },
                        { error ->
                            view?.displayNewsPaperTranslationStatus(isOn.not())
                            view?.showError(error.localizedMessage)
                        }
                ).addTo(subscriptions)
    }
}