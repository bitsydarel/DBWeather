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

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.HourlyListItemBinding
import com.dbeginc.dbweatherweather.viewmodels.HourWeatherModel

/**
 * Created by Darel Bitsy on 12/01/17.
 * Hourly Weather Data RecyclerView Adapter
 */

class HourAdapter : RecyclerView.Adapter<HourAdapter.HourViewHolder>() {
    private var container: RecyclerView? = null
    private var hours: Array<HourWeatherModel> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        return HourViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.hourly_list_item,
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.bindHour(hours[position])
    }

    override fun getItemCount(): Int = hours.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        this.container = recyclerView
    }

    @Synchronized
    fun updateData(newData: List<HourWeatherModel>) {
        val sortedData = newData.sorted().toTypedArray()

        val diffResult = DiffUtil.calculateDiff(HourDiffCallback(hours, sortedData))

        hours = sortedData

        diffResult.dispatchUpdatesTo(this)
    }

    inner class HourViewHolder(private val itemBinding: HourlyListItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        fun bindHour(hour: HourWeatherModel) {
            itemBinding.hour = hour
            itemBinding.executePendingBindings()
        }
    }
}
