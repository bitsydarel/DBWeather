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

package com.dbeginc.dbweather.managelocations

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.databinding.LocationLayoutBinding
import com.dbeginc.dbweather.utils.locations.LocationDiffUtil
import com.dbeginc.dbweatherweather.viewmodels.WeatherLocationModel

/**
 * Created by darel on 26.10.17.
 *
 * Manage Locations Adapter
 */
class ManageLocationsAdapter : BaseAdapter<WeatherLocationModel, ManageLocationsAdapter.LocationViewHolder>(LocationDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.location_layout,
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bindLocation(location = getItemForPosition(position))
    }

    inner class LocationViewHolder(private val binding: LocationLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindLocation(location: WeatherLocationModel) {
            binding.location = location

            binding.executePendingBindings()
        }

        fun getLocation(): WeatherLocationModel? = binding.location

    }
}