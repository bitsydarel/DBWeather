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

package com.dbeginc.dbweather.iptvplaylistdetail

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.base.BaseDataDiff
import com.dbeginc.dbweather.databinding.IptvLiveLayoutBinding
import com.dbeginc.dbweather.utils.utility.getInflater
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweatherlives.viewmodels.IpTvLiveModel

class IpTvLivesAdapter(private val onItemClick: (IpTvLiveModel) -> Unit) : BaseAdapter<IpTvLiveModel, IpTvLivesAdapter.IpTvLiveViewHolder>(IpTvLiveDifferenceCalculator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IpTvLiveViewHolder {
        return IpTvLiveViewHolder(
                DataBindingUtil.inflate(
                        parent.getInflater(),
                        R.layout.iptv_live_layout,
                        parent,
                        false
                ),
                onItemClick = onItemClick
        )
    }

    override fun onBindViewHolder(holder: IpTvLiveViewHolder, holderPosition: Int) {
        return holder.bindIpTvLive(getItemForPosition(holderPosition))
    }

    inner class IpTvLiveViewHolder(private val binding: IptvLiveLayoutBinding, onItemClick: (IpTvLiveModel) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.iptvLiveLayout.setOnClickListener {
                onItemClick(binding.ipTvLive!!)
            }
        }

        fun bindIpTvLive(iptvLive: IpTvLiveModel) {
            binding.ipTvLive = iptvLive

            if (iptvLive.channelLogo.isBlank()) binding.iptvLiveName.show()

            binding.executePendingBindings()
        }
    }

    class IpTvLiveDifferenceCalculator : BaseDataDiff<IpTvLiveModel>() {
        override fun areItemsTheSame(oldItem: IpTvLiveModel?, newItem: IpTvLiveModel?): Boolean {
            return oldItem?.channelName == newItem?.channelName
        }
    }
}