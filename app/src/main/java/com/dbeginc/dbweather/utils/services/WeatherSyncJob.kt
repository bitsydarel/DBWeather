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

package com.dbeginc.dbweather.utils.services

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.dbeginc.dbweather.DBWeatherApp
import com.dbeginc.dbweather.notifications.NotificationActivity
import com.dbeginc.dbweather.utils.preferences.ApplicationPreferences
import com.dbeginc.dbweather.utils.utility.NOTIFICATION_KEY
import com.dbeginc.dbweather.utils.utility.findDefaultLocation
import com.dbeginc.dbweather.utils.utility.fullName
import com.dbeginc.dbweather.viewmodels.WeatherNotificationModel
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.repositories.WeatherRepository
import com.dbeginc.dbweatherweather.viewmodels.WeatherModel
import com.dbeginc.dbweatherweather.viewmodels.toUi
import com.evernote.android.job.DailyJob
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WeatherSyncJob : DailyJob() {
    @Inject lateinit var weatherRepository: dagger.Lazy<WeatherRepository>
    @Inject lateinit var preferences: dagger.Lazy<ApplicationPreferences>

    companion object {
        const val DBWEATHER_WEATHER_JOB_TAG = "dbweather_weather_job_tag"

        @JvmStatic fun schedule() {
            if (JobManager.instance().getAllJobRequestsForTag(DBWEATHER_WEATHER_JOB_TAG).isEmpty()) {
                val jobBuilder = JobRequest.Builder(DBWEATHER_WEATHER_JOB_TAG)
                        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                        .setRequiresBatteryNotLow(true)
                        .setUpdateCurrent(true)

                DailyJob.scheduleAsync(jobBuilder, TimeUnit.HOURS.toMillis(6), TimeUnit.HOURS.toMillis(9))
            }
        }

        @JvmStatic fun scheduleForNow() {
            DailyJob.startNowOnce(JobRequest.Builder(DBWEATHER_WEATHER_JOB_TAG))
        }
    }

    init {
        DBWeatherApp.applicationGraph?.injectAndroidJob(this)
    }

    override fun onRunDailyJob(params: Params): DailyJobResult {
        return if (!preferences.get().isWeatherNotificationOn()) DailyJobResult.SUCCESS
        else {

            val weather: WeatherModel = preferences
                    .get()
                    .findDefaultLocation()
                    .let { (name, latitude, longitude, countryCode) ->
                        WeatherRequest(
                                city = name,
                                arg = countryCode,
                                latitude = latitude,
                                longitude = longitude
                        )
                    }.let {  weatherRequest ->
                        weatherRepository
                                .get()
                                .getWeather(request = weatherRequest)
                                .limit(2)
                                .blockingLast()
                                .toUi()
                    }

            val notificationData = WeatherNotificationModel(
                    location = weather.location.fullName(),
                    temperature = "%d%s%s".format(weather.current.temperature, "°", weather.current.temperatureUnit),
                    icon = weather.current.icon,
                    summary = weather.current.summary
            )

            context.startActivity(Intent(context, NotificationActivity::class.java).apply {
                putExtra(NOTIFICATION_KEY, notificationData)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            })

            DailyJobResult.SUCCESS

        }
    }
}