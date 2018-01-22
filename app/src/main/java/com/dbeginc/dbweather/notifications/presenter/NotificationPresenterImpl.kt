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

package com.dbeginc.dbweather.notifications.presenter

import com.dbeginc.dbweather.notifications.NotificationContract
import com.dbeginc.dbweather.viewmodels.notifications.WeatherNotificationModel

/**
 * Created by darel on 11.11.17.
 *
 * Notification Presenter
 */
class NotificationPresenterImpl(private val notification: WeatherNotificationModel) : NotificationContract.NotificationPresenter {
    private var view: NotificationContract.NotificationView? = null

    override fun bind(view: NotificationContract.NotificationView) {
        this.view = view
        this.view?.setupView()
    }

    override fun unBind() {
        view = null
    }

    override fun loadNotification() {
        view?.displayNotification(notification)
    }

    override fun getData(): WeatherNotificationModel = notification

    override fun onExpand() {
        view?.openMainScreen()
    }

    override fun onClose() {
        view?.closeScreen()
    }
}
