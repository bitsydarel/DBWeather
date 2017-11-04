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

package com.dbeginc.dbweather.intro.chooselocation.view

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentChooseLocationBinding
import com.dbeginc.dbweather.intro.chooselocation.ChooseLocationContract
import com.dbeginc.dbweather.intro.chooselocation.adapter.ChooseLocationAdapter
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.dbeginc.dbweather.utils.holder.ConstantHolder.IS_GPS_PERMISSION_GRANTED
import com.dbeginc.dbweather.utils.holder.ConstantHolder.LOCATIONS
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweather.viewmodels.weather.LocationWeatherModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


/**
 * Created by darel on 29.09.17.
 *
 * Choose Location Fragment
 */
class ChooseLocationFragment : BaseFragment(), ChooseLocationContract.ChooseLocationView{
    @Inject lateinit var presenter: ChooseLocationContract.ChooseLocationPresenter
    private lateinit var binding: FragmentChooseLocationBinding
    private lateinit var adapter: ChooseLocationAdapter
    private val locationEvent: PublishSubject<LocationWeatherModel> = PublishSubject.create()
    private lateinit var disposable: Disposable

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectChooseLocation(this)

        val locations = savedState?.getList<LocationWeatherModel>(LOCATIONS)?.toMutableList() ?: mutableListOf()

        adapter = ChooseLocationAdapter(locations, locationEvent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_location, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putList(LOCATIONS, adapter.getData())
    }

    override fun onSearchQuery(query: String?) {
        if (query != null && query.isNotEmpty()) presenter.loadLocations(query)
    }

    /******************************** Choose Location Custom View Part  ********************************/
    override fun setupView() {
        if(adapter.getData().isEmpty() && !binding.chooseLocationAnimation.isAnimating) {
            binding.chooseLocationAnimation.playAnimation()
        }
        setupLocationsRecyclerView()
        setupSearchView()
        disposable = locationEvent.subscribe(
                { location -> presenter.onLocationSelected(location) },
                { error -> showError(error.localizedMessage) }
        )
    }

    override fun cleanState() {
        binding.chooseLocationAnimation.cancelAnimation()
        disposable.dispose()
        presenter.unBind()
    }

    override fun displayLocations(locations: List<LocationWeatherModel>) {
        adapter.updateData(locations)
        binding.chooseLocationAnimation.pauseAnimation()
        binding.chooseLocationAnimation.remove()
    }

    override fun defineCurrentLocation(latitude: Double, longitude: Double, locationName: String) {
        preferences.putDouble(ConstantHolder.LATITUDE, latitude)
                .putDouble(ConstantHolder.LONGITUDE, longitude)
                .edit()
                .putString(ConstantHolder.CURRENT_LOCATION, locationName)
                .apply()
        preferences.edit().putBoolean(IS_GPS_PERMISSION_GRANTED, false).apply()
        preferences.edit().putBoolean(ConstantHolder.IS_CURRENT_LOCATION, true).apply()
    }

    override fun showLoadingStatus() = binding.chooseLocationLoading.show()

    override fun hideLoadingStatus() = binding.chooseLocationLoading.remove()

    override fun showLocationAnimation() {
        binding.chooseLocationAnimation.playAnimation()
        binding.chooseLocationAnimation.show()
    }

    override fun hideLocationAnimation() {
        binding.chooseLocationAnimation.pauseAnimation()
        binding.chooseLocationAnimation.remove()
    }

    override fun showError(message: String) {
        if (isVisible) binding.chooseLocationLayout.snack(message)
    }

    override fun goToMainScreen() {
        Navigator.goToMainScreen(context)
        activity.finish()
    }

    private fun setupSearchView() {
        val searchManager = context.getSystemService(SEARCH_SERVICE) as SearchManager
        binding.chooseLocationSearch.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        binding.chooseLocationSearch.isSubmitButtonEnabled = true
    }

    private fun setupLocationsRecyclerView() {
        binding.chooseLocationList.adapter = adapter
        binding.chooseLocationList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }
}