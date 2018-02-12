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

package com.dbeginc.dbweather.intro.chooselocation

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Context.SEARCH_SERVICE
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentChooseLocationBinding
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.intro.chooselocation.adapter.ChooseLocationAdapter
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.utils.addTo
import com.dbeginc.dbweatherweather.chooselocations.ChooseLocationsViewModel
import com.dbeginc.dbweatherweather.chooselocations.contract.ChooseLocationsView
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject


/**
 * Created by darel on 29.09.17.
 *
 * Choose Location Fragment
 */
class ChooseLocationsFragment : BaseFragment(), ChooseLocationsView, WithDependencies {
    private var subscriptions = CompositeDisposable()
    private lateinit var viewModel: ChooseLocationsViewModel
    private lateinit var binding: FragmentChooseLocationBinding
    private val locationAdapter = ChooseLocationAdapter(emptyList())
    override val state: BehaviorSubject<RequestState> = BehaviorSubject.create()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(activity, factory)[ChooseLocationsViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getLocations().observe(this,
                android.arch.lifecycle.Observer {
                    displayLocations(it!!)
                }
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_location, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationAdapter.locationSelectionEvent.subscribe { location ->
            viewModel.presenter.onLocationSelected(this, location)
        }.addTo(subscriptions)

        viewModel.presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding.chooseLocationAnimation.cancelAnimation()

        subscriptions.clear()
    }

    override fun onStateChanged(state: RequestState) = when (state) {
        RequestState.LOADING -> binding.chooseLocationLoading.show()
        RequestState.COMPLETED -> binding.chooseLocationLoading.remove()
        RequestState.ERROR -> onLocationsRequestFailed()
    }

    fun onSearchQuery(query: String?) {
        if (query != null && query.isNotEmpty()) viewModel.findLocations(state, query)
    }

    /******************************** Choose Location Custom View Part  ********************************/
    override fun setupView() {
        if (locationAdapter.getData().isEmpty() and binding.chooseLocationAnimation.isAnimating.not()) {
            showLocationAnimation()
        }

        binding.chooseLocationList.setup()

        binding.chooseLocationSearch.setup()
    }

    override fun defineCurrentLocation(location: LocationWeatherModel) {
        preferences.updateDefaultCoordinates(location.name, location.latitude, location.longitude)

        preferences.changeGpsPermissionStatus(false)

        preferences.changeDefaultLocationStatus(true)

        viewModel.presenter.onExitAction(this)
    }

    override fun onLocationsRequestFailed() = viewModel.presenter.retryLocationsRequest()

    override fun onNavigationAction() {
        Navigator.goToMainScreen(context)
        activity?.finish()
    }

    private fun displayLocations(locations: List<LocationWeatherModel>) {
        hideLocationAnimation()
        locationAdapter.updateData(locations)
    }

    private fun showLocationAnimation() {
        binding.chooseLocationAnimation.playAnimation()
        binding.chooseLocationAnimation.show()
    }

    private fun hideLocationAnimation() {
        binding.chooseLocationAnimation.pauseAnimation()
        binding.chooseLocationAnimation.remove()
    }

    private fun SearchView.setup() {
        val searchManager = context.getSystemService(SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        isSubmitButtonEnabled = true
    }

    private fun RecyclerView.setup() {
        adapter = locationAdapter
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}