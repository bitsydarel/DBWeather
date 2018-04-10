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

package com.dbeginc.dbweather.iptvplaylists

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.databinding.PlaylistLayoutBinding
import com.dbeginc.dbweather.utils.utility.getInflater
import com.dbeginc.dbweatherlives.viewmodels.IpTvPlayListModel

class IpTvPlayListAdapter(private val onItemClick: (IpTvPlayListModel) -> Unit) : BaseAdapter<IpTvPlayListModel, IpTvPlayListAdapter.IpTvPlayListViewHolder>(IpTvPlayListDifferenceCalculator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IpTvPlayListViewHolder {
        return IpTvPlayListViewHolder(DataBindingUtil.inflate(
                parent.getInflater(),
                R.layout.playlist_layout,
                parent,
                false
        ), onItemClick = onItemClick)
    }

    override fun onBindViewHolder(holder: IpTvPlayListViewHolder, position: Int) {
        holder.bindIpTvPlayList(getItemForPosition(position = position))
    }

    inner class IpTvPlayListViewHolder(private val binding: PlaylistLayoutBinding, onItemClick: (IpTvPlayListModel) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.openPlaylist.setOnClickListener { onItemClick(binding.playlist!!) }
        }

        fun bindIpTvPlayList(playList: IpTvPlayListModel) {
            binding.playlist = playList

            binding.executePendingBindings()
        }
    }
}