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

package com.dbeginc.dbweather.weather

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.base.BaseDataDiff
import com.dbeginc.dbweather.databinding.DayLayoutBinding
import com.dbeginc.dbweatherweather.viewmodels.DayWeatherModel

/**
 * Created by darel on 23.09.17.
 *
 * Day Adapter
 */
class DayAdapter : BaseAdapter<DayWeatherModel, DayAdapter.DayViewHolder>(DayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DayViewHolder(DataBindingUtil.inflate(
                inflater,
                R.layout.day_layout,
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bindDay(getItemForPosition(position))
    }

    inner class DayViewHolder(private val binding: DayLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindDay(day: DayWeatherModel) {
            binding.day = day

            binding.executePendingBindings()
        }
    }

    class DayDiffCallback : BaseDataDiff<DayWeatherModel>() {
        override fun areItemsTheSame(oldItem: DayWeatherModel?, newItem: DayWeatherModel?): Boolean {
            return oldItem?.dayName == newItem?.dayName
        }
    }

}