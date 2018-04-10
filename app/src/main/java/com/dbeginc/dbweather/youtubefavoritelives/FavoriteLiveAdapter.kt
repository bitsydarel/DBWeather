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

package com.dbeginc.dbweather.youtubefavoritelives

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.databinding.YoutubeLiveLayoutBinding
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.youtubelive.YoutubeLiveDiffUtils
import com.dbeginc.dbweather.youtubelives.YoutubeLiveActionBridge
import com.dbeginc.dbweatherlives.viewmodels.YoutubeLiveModel

/**
 * Created by darel on 20.10.17.
 *
 * Favorite YoutubeLive Adapter
 */
class FavoriteLiveAdapter(private val containerBridge: YoutubeLiveActionBridge) : BaseAdapter<YoutubeLiveModel, FavoriteLiveAdapter.FavoriteLiveViewHolder>(YoutubeLiveDiffUtils()) {
    private var container: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteLiveViewHolder {
        return FavoriteLiveViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.youtube_live_layout, parent,
                        false
                ),
                containerBridge
        )
    }

    override fun onBindViewHolder(holder: FavoriteLiveViewHolder, position: Int) {
        holder.bindLive(youtubeLiveModel = getItemForPosition(position))
    }

    inner class FavoriteLiveViewHolder(private val binding: YoutubeLiveLayoutBinding, containerBridge: YoutubeLiveActionBridge) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.liveFavorite.remove()

            binding.liveThumbnail.setOnClickListener {
                containerBridge.playYoutubeLive(binding.live!!)
            }
        }

        fun bindLive(youtubeLiveModel: YoutubeLiveModel) {
            binding.live = youtubeLiveModel

            binding.executePendingBindings()
        }
    }
}