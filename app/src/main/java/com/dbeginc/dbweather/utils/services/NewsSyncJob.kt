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

import android.util.Log
import com.evernote.android.job.DailyJob
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import java.util.concurrent.TimeUnit

class NewsSyncJob : DailyJob() {

    companion object {
        const val DBWEATHER_NEWS_JOB_MORNING = "dbweather_news_job_morning"
        const val DBWEATHER_NEWS_JOB_EVENING = "dbweather_news_job_evening"

        @JvmStatic fun schedule() {
            with(JobManager.instance()) {
                if (getAllJobRequestsForTag(DBWEATHER_NEWS_JOB_MORNING).isEmpty()) {
                    val jobBuilder = JobRequest
                            .Builder(DBWEATHER_NEWS_JOB_MORNING)
                            .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                            .setRequiresBatteryNotLow(true)
                            .setUpdateCurrent(true)

                    DailyJob.scheduleAsync(
                            jobBuilder,
                            TimeUnit.HOURS.toMillis(6),
                            TimeUnit.HOURS.toMillis(9)
                    )
                }

                if (getAllJobRequestsForTag(DBWEATHER_NEWS_JOB_EVENING).isEmpty()) {
                    val jobBuilder = JobRequest
                            .Builder(DBWEATHER_NEWS_JOB_EVENING)
                            .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                            .setRequiresBatteryNotLow(true)
                            .setUpdateCurrent(true)

                    DailyJob.scheduleAsync(
                            jobBuilder,
                            TimeUnit.HOURS.toMillis(16),
                            TimeUnit.HOURS.toMillis(19)
                    )
                }
            }
        }

        @JvmStatic fun scheduleForNow() {
            DailyJob.startNowOnce(JobRequest.Builder(DBWEATHER_NEWS_JOB_MORNING))

            DailyJob.startNowOnce(JobRequest.Builder(DBWEATHER_NEWS_JOB_MORNING))
        }

    }

    override fun onRunDailyJob(params: Params): DailyJobResult {
        Log.d(params.tag,  "${params.tag} has been run")
        return DailyJobResult.SUCCESS
    }
}