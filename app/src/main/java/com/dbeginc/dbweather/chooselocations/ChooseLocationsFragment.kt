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

package com.dbeginc.dbweather.chooselocations

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context.SEARCH_SERVICE
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentChooseLocationBinding
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweatherweather.chooselocations.ChooseLocationsViewModel
import com.dbeginc.dbweatherweather.viewmodels.WeatherLocationModel


/**
 * Created by darel on 29.09.17.
 *
 * Choose Location Fragment
 */
class ChooseLocationsFragment : BaseFragment(), MVMPVView, ChooseLocationBridge, SearchView.OnQueryTextListener, WithSearchableData {
    private lateinit var binding: FragmentChooseLocationBinding

    private val viewModel: ChooseLocationsViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[ChooseLocationsViewModel::class.java]
    }

    private val locationAdapter: ChooseLocationAdapter by lazy {
        ChooseLocationAdapter(actionBridge = this)
    }

    override val stateObserver: Observer<RequestState> = Observer { state ->
        state?.let { onStateChanged(state = it) }
    }

    private val locationsObserver: Observer<List<WeatherLocationModel>> = Observer { locations ->
        locations?.let { locationAdapter.updateData(newData = it) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { container ->
            binding.chooseLocationToolbar.setNavigationOnClickListener {
                goToIntroScreen(container = container, layoutId = R.id.launchContent)
            }
        }

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getFoundedLocations().observe(this, locationsObserver)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_choose_location,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.chooseLocationToolbar)

        setupView()
    }

    override fun onLocationReceived(location: WeatherLocationModel) {
        preferences.get().run {
            updateCustomCoordinates(
                    city = location.name,
                    countryCode = location.countryCode,
                    latitude = location.latitude,
                    longitude = location.longitude
            )

            changeDefaultLocationStatus(isFromGps = false)

            changeGpsPermissionStatus(isOn = false)

            activity?.let {
                goToChooseDefaultNewsPapers(
                        container = it,
                        layoutId = R.id.launchContent
                )
            }
        }
    }

    override fun onSearchQuery(query: String?) {
        if (query != null && query.isNotBlank()) {
            viewModel.findLocations(possibleLocation = query)
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        onSearchQuery(query = newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        onSearchQuery(query = query)
        return true
    }

    /******************************** Choose Location Custom View Part  ********************************/
    override fun setupView() {
        binding.chooseLocationList.adapter = locationAdapter

        binding.chooseLocationList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        activity?.let {
            val searchManager = it.getSystemService(SEARCH_SERVICE) as SearchManager

            binding.chooseLocationSearch.setSearchableInfo(searchManager.getSearchableInfo(it.componentName))

            binding.chooseLocationSearch.isSubmitButtonEnabled = false

            binding.chooseLocationSearch.setOnQueryTextListener(this)
        }
    }

    override fun onStateChanged(state: RequestState) = when (state) {
        RequestState.LOADING -> binding.chooseLocationLoading.show()
        RequestState.COMPLETED -> binding.chooseLocationLoading.remove()
        RequestState.ERROR -> onLocationsRequestFailed()
    }

    private fun onLocationsRequestFailed() {
        binding.chooseLocationLayout.snack(resId = R.string.could_not_load_your_locations)
    }
}