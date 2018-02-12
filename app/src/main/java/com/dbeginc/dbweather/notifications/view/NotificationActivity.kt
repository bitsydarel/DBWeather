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

package com.dbeginc.dbweather.notifications.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ActivityNotificationBinding
import com.dbeginc.dbweather.notifications.NotificationContract
import com.dbeginc.dbweather.notifications.presenter.NotificationPresenterImpl
import com.dbeginc.dbweather.utils.holder.ConstantHolder.NOTIFICATION_KEY
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweather.viewmodels.notifications.WeatherNotificationModel

/**
 * Created by Darel Bitsy on 01/02/17.
 *
 * Notification activity that show
 * User about notification
 */
class NotificationActivity : AppCompatActivity(), NotificationContract.NotificationView {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var presenter: NotificationContract.NotificationPresenter

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)

        presenter = if (savedState == null) NotificationPresenterImpl(intent.getParcelableExtra(NOTIFICATION_KEY)) else NotificationPresenterImpl(savedState.getParcelable(NOTIFICATION_KEY))

    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(NOTIFICATION_KEY, presenter.getData())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_notification, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.openFullWeather -> presenter.onExpand()
            R.id.closeWindow -> presenter.onClose()
        }
        return super.onOptionsItemSelected(item)
    }

    /******************* Notification Custom view *******************/
    override fun setupView() = presenter.loadNotification()

    override fun cleanState() = presenter.unBind()

    override fun displayNotification(notification: WeatherNotificationModel) {
        binding.notification = notification
    }

    override fun openMainScreen() {
        Navigator.goToMainScreen(this)

        finish()
    }

    override fun closeScreen() = finish()

    override fun showMessage(message: String) = binding.root.toast(message)
}
