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

package com.dbeginc.dbweather.config.managelocations.adapter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.LocationItemBinding
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
/**
 * Created by darel on 26.10.17.
 *
 * Manage Locations Adapter
 */
class LocationsAdapter(var locations: MutableList<LocationWeatherModel>) : RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {
    private var container: RecyclerView? = null

    init {
        locations.sort()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun getItemCount(): Int = locations.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LocationViewHolder {
        return LocationViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context), R.layout.location_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: LocationViewHolder?, position: Int) {
        holder?.bindLocation(locations[position])
    }

    @Synchronized
    fun update(newData: List<LocationWeatherModel>) {
        val result = DiffUtil.calculateDiff(LocationDiffUtil(locations, newData.sorted()))

        locations = ArrayList(newData)

        locations.sort()

        result.dispatchUpdatesTo(this@LocationsAdapter)
    }

    fun remove(position: Int): LocationWeatherModel {
        val removedLocation = locations.removeAt(position)

        container?.post { notifyItemRemoved(position) }

        return removedLocation
    }

    inner class LocationViewHolder(private val binding: LocationItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindLocation(location: LocationWeatherModel) {
            binding.location = location
            binding.executePendingBindings()
        }
    }
}