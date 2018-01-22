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

package com.dbeginc.dbweather.intro.chooselocation.adapter.view

import android.support.v7.widget.RecyclerView
import com.dbeginc.dbweather.databinding.LocationItemBinding
import com.dbeginc.dbweather.intro.chooselocation.adapter.ChooseLocationItemContract
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import io.reactivex.subjects.PublishSubject

/**
 * Created by darel on 30.09.17.
 *
 * Choose Location View Holder
 */
class ChooseLocationViewHolder(val binding: LocationItemBinding, val locationEvent: PublishSubject<LocationWeatherModel>) : RecyclerView.ViewHolder(binding.root), ChooseLocationItemContract.ChooseLocationItemView {
    override fun setupView() { /******* Not needed *******/ }

    override fun cleanState() { /******* Not needed *******/ }

    override fun displayLocation(location: LocationWeatherModel) {
        binding.location = location
        binding.executePendingBindings()
    }

    override fun setupClickForwarding(presenter: ChooseLocationItemContract.ChooseLocationItemPresenter) {
        binding.root.setOnClickListener { presenter.onClick() }
    }

    override fun defineUserLocation(location: LocationWeatherModel) = locationEvent.onNext(location)

}