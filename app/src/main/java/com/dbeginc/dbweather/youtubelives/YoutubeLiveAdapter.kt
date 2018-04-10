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

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.utils.youtubelive.YoutubeLiveDiffUtils
import com.dbeginc.dbweatherlives.viewmodels.YoutubeLiveModel

/**
 * Created by darel on 18.10.17.
 *
 * YoutubeLive adapter
 */
class YoutubeLiveAdapter(private val containerBridge: YoutubeLiveActionBridge) : BaseAdapter<YoutubeLiveModel, YoutubeLiveViewHolder>(YoutubeLiveDiffUtils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YoutubeLiveViewHolder {
        return YoutubeLiveViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.youtube_live_layout,
                        parent,
                        false
                ),
                containerBridge
        )
    }

    override fun onBindViewHolder(holder: YoutubeLiveViewHolder, position: Int) {
        holder.bindYoutubeLive(youtubeLive = getItemForPosition(position))
    }
}