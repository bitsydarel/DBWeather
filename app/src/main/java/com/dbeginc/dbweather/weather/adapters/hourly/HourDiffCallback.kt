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

package com.dbeginc.dbweather.weather.adapters.hourly

import android.support.v7.util.DiffUtil
import com.dbeginc.dbweather.viewmodels.weather.HourWeatherModel

/**
 * Created by Bitsy Darel on 16.05.17.
 * HourlyData update handler
 */
class HourDiffCallback internal constructor(private val oldData: Array<HourWeatherModel>, private val newData: Array<HourWeatherModel>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldData.size

    override fun getNewListSize(): Int = newData.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = oldData[oldItemPosition].hourlyTime == newData[newItemPosition].hourlyTime

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition] == newData[newItemPosition]
    }
}
