/*
 *  Copyright (C) 2017 Darel Bitsy
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.dbeginc.dbweather.notifications

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ActivityNotificationBinding
import com.dbeginc.dbweather.utils.utility.NOTIFICATION_KEY
import com.dbeginc.dbweather.utils.utility.goToMainScreen

/**
 * Created by Darel Bitsy on 01/02/17.
 *
 * Notification activity that show
 * User about notification
 */
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_notification
        )

        binding.notification = if (savedState == null) intent.getParcelableExtra(NOTIFICATION_KEY)
        else savedState.getParcelable(NOTIFICATION_KEY)

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(NOTIFICATION_KEY, binding.notification)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_notification, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.temperature)?.title = binding.notification?.temperature ?: ""
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.openFullWeather -> goToMainScreen(currentScreen = this)
            R.id.closeWindow -> supportFinishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

}
