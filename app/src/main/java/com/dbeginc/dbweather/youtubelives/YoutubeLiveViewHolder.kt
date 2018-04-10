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

package com.dbeginc.dbweather.youtubelives

import android.support.v7.widget.RecyclerView
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.YoutubeLiveLayoutBinding
import com.dbeginc.dbweatherlives.viewmodels.YoutubeLiveModel

/**
 * Created by darel on 19.10.17.
 *
 * YoutubeLive View Holder
 */
class YoutubeLiveViewHolder(private val binding: YoutubeLiveLayoutBinding, containerBridge: YoutubeLiveActionBridge) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.liveFavorite.setOnClickListener {
            binding.live?.let {
                if (it.isFavorite) containerBridge.removeFromFavorite(it)
                else containerBridge.addToFavorite(it)
            }
        }

        binding.liveThumbnail.setOnClickListener {
            containerBridge.playYoutubeLive(binding.live!!)
        }
    }

    fun bindYoutubeLive(youtubeLive: YoutubeLiveModel) {
        binding.live = youtubeLive

        if (youtubeLive.isFavorite) binding.liveFavorite.setImageResource(R.drawable.ic_follow_icon_red)
        else binding.liveFavorite.setImageResource(R.drawable.ic_un_follow_icon_black)

        binding.executePendingBindings()

    }

}