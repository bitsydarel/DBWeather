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

import com.dbeginc.dbweather.utils.services.NewsSyncJob.Companion.DBWEATHER_NEWS_JOB_EVENING
import com.dbeginc.dbweather.utils.services.NewsSyncJob.Companion.DBWEATHER_NEWS_JOB_MORNING
import com.dbeginc.dbweather.utils.services.WeatherSyncJob.Companion.DBWEATHER_WEATHER_JOB_TAG
import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator

class DBWeatherJobCreator : JobCreator{
    override fun create(tag: String): Job? {
        return when(tag) {
            DBWEATHER_WEATHER_JOB_TAG -> WeatherSyncJob()
            DBWEATHER_NEWS_JOB_MORNING -> NewsSyncJob()
            DBWEATHER_NEWS_JOB_EVENING -> NewsSyncJob()
            else -> null
        }
    }
}