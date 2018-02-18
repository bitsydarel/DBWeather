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

package com.dbeginc.dbweather.weather.adapters.daily

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.weather.adapters.daily.presenter.DayPresenter
import com.dbeginc.dbweather.weather.adapters.daily.presenter.DayPresenterImpl
import com.dbeginc.dbweather.weather.adapters.daily.view.DayViewHolder
import com.dbeginc.dbweatherweather.viewmodels.DayWeatherModel

/**
 * Created by darel on 23.09.17.
 *
 * Day Adapter
 */
class DayAdapter : RecyclerView.Adapter<DayViewHolder>() {
    private var container: RecyclerView? = null
    private var presenters: Array<DayPresenter> = emptyArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DayViewHolder(DataBindingUtil.inflate(inflater, R.layout.daily_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        // We get the presenter for the view at this position
        val presenter = presenters[position]

        // Bind the presenter to it's view
        presenter.bind(holder)

        // configure the view to forward the click event
        // to it's presenter
        holder.setupActionListener(presenter)

        presenter.loadDay()
    }

    override fun getItemCount(): Int = presenters.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    @Synchronized
    fun updateData(newData: List<DayWeatherModel>) {
        val sortedData: Array<DayPresenter> = newData.map { day -> DayPresenterImpl(day) }.sorted().toTypedArray()

        val diffResult = DiffUtil.calculateDiff(DayDiffCallback(presenters, sortedData), true)

        presenters = sortedData

        diffResult.dispatchUpdatesTo(this)
    }
}