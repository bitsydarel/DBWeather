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

package com.dbeginc.dbweather.intro.chooselocation.adapter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.intro.chooselocation.adapter.presenter.ChooseLocationItemPresenter
import com.dbeginc.dbweather.intro.chooselocation.adapter.presenter.ChooseLocationItemPresenterImpl
import com.dbeginc.dbweather.intro.chooselocation.adapter.view.ChooseLocationViewHolder
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * Created by darel on 30.09.17.
 *
 * Choose Location Adapter
 */
class ChooseLocationAdapter(locations: List<LocationWeatherModel>) : RecyclerView.Adapter<ChooseLocationViewHolder>() {
    private var container: RecyclerView? = null
    private val presenters: LinkedList<ChooseLocationItemPresenter>
    val locationSelectionEvent: PublishSubject<LocationWeatherModel> = PublishSubject.create()

    init {
        presenters = LinkedList(locations.map { location -> ChooseLocationItemPresenterImpl(location) })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChooseLocationViewHolder {
        val inflater = LayoutInflater.from(parent?.context)

        return ChooseLocationViewHolder(
                DataBindingUtil.inflate(inflater, R.layout.location_item, container, false),
                locationSelectionEvent
        )
    }

    override fun onBindViewHolder(holder: ChooseLocationViewHolder, position: Int) {
        // We get the presenter for the view at this position
        val presenter = presenters[position]

        // Bind the presenter to it's view
        presenter.bind(holder)

        // configure the view to forward the click event
        // to it's presenter
        holder.setupClickForwarding(presenter)

        presenter.loadLocation(holder)
    }

    override fun getItemCount(): Int = presenters.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    fun getData() : List<LocationWeatherModel> = presenters.map { presenter -> presenter.getModel() }

    fun updateData(newData: List<LocationWeatherModel>) {
        if (presenters.isEmpty()) {
            //Fix for view holder invalid when view is empty
            container?.recycledViewPool?.clear()

            fillMe(newData)
            // Posting update on RecyclerView Thread
            container?.post { notifyDataSetChanged() }

        } else {
            val diffResult = DiffUtil.calculateDiff(ChooseLocationDiffCallback(presenters.map { presenter -> presenter.getModel() }, newData))

            fillMe(newData)
            // Posting diffUtils result on the recyclerView queue
            // Which fix the Illegal statement Layout computing
            // And updating the layout changes on the container queue
            container?.post { diffResult.dispatchUpdatesTo(this) }
        }
    }

    private fun fillMe(newData: List<LocationWeatherModel>) {
        presenters.clear()
        newData.mapTo(presenters) { location -> ChooseLocationItemPresenterImpl(location) }
    }
}