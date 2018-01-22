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

package com.dbeginc.dbweather.config.managelocations.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.config.managelocations.ManageLocationsContract
import com.dbeginc.dbweather.config.managelocations.adapter.LocationsAdapter
import com.dbeginc.dbweather.databinding.ActivityManageLocationsBinding
import com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATIONS
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import javax.inject.Inject


/**
 * Created by darel on 26.10.17.
 *
 * Manage Locations Activity
 */
class ManageLocationsActivity: BaseActivity(), ManageLocationsContract.ManageLocationsView {
    @Inject lateinit var presenter: ManageLocationsContract.ManageLocationsPresenter
    private lateinit var adapter: LocationsAdapter
    private lateinit var binding: ActivityManageLocationsBinding
    private lateinit var itemTouchHelper: ItemTouchHelper
    private val locationRemoved by lazy { getString(R.string.location_removed) }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectManageLocationsDep(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_locations)

        adapter = if (savedState == null) LocationsAdapter(mutableListOf()) else LocationsAdapter(savedState.getList<LocationWeatherModel>(LOCATIONS).toMutableList())

        itemTouchHelper = ItemTouchHelper(SwipeToRemove(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT))

        presenter.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putList(LOCATIONS, adapter.locations)
    }

    /******************* Manage Locations Custom Part *******************/
    override fun setupView() {
        binding.manageLocationsToolbar.setNavigationOnClickListener { presenter.goBack() }

        binding.manageLocations.adapter = adapter

        binding.manageLocations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        itemTouchHelper.attachToRecyclerView(binding.manageLocations)

        presenter.loadUserLocations()
    }

    override fun cleanState() = presenter.unBind()

    override fun displayLocations(locations: List<LocationWeatherModel>) {
        adapter.update(locations)
        binding.emptyList.remove()
    }

    override fun displayNoLocations() = binding.emptyList.show()

    override fun showLocationRemovedMessage() = binding.manageLocationsLayout.snack(locationRemoved, Snackbar.LENGTH_SHORT)

    override fun showUpdateStatus() = binding.manageLocationsUpdateStatus.show()

    override fun hideUpdateStatus() = binding.manageLocationsUpdateStatus.hide()

    override fun goBackToConfiguration() = finish()

    override fun showError(message: String) = binding.manageLocationsLayout.snack(message)

    private inner class SwipeToRemove internal constructor(dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = presenter.removeLocation(adapter.remove(viewHolder.adapterPosition))

    }
}