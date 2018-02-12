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

package com.dbeginc.dbweather.weather.adapters.daily.view

import android.support.v7.widget.RecyclerView
import com.dbeginc.dbweather.databinding.DailyListItemBinding
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweather.weather.adapters.daily.presenter.DayPresenter
import com.dbeginc.dbweatherweather.viewmodels.DayWeatherModel

/**
 * Created by darel on 23.09.17.
 *
 * Day View Implementation
 */
class DayViewHolder(val binding: DailyListItemBinding) : RecyclerView.ViewHolder(binding.root), DayView {
    override fun setupView() { /* Not needed for now */}

    override fun cleanState() { /* No resource to clean */ }

    override fun displayDay(day: DayWeatherModel) {
        binding.day = day
    }

    override fun showDetail() {
        binding.dayLayout.toast("Opened Detail for day ${binding.day?.dayName}")
//        Navigator.goToDayDetailScreen(binding)
    }

    override fun setupActionListener(presenter: DayPresenter) {
        binding.dayLayout.setOnClickListener { presenter.goToDayDetail() }
    }

    override fun showMessage(message: String) = binding.root.toast(message)
}