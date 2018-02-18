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

package com.dbeginc.dbweather.news.lives.page.favorite.adapter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.LiveItemBinding
import com.dbeginc.dbweather.news.lives.page.LiveDiffUtils
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweathernews.viewmodels.LiveModel

/**
 * Created by darel on 20.10.17.
 *
 * Favorite Live Adapter
 */
class FavoriteLiveAdapter(private var favorites: Array<LiveModel> = emptyArray()) : RecyclerView.Adapter<FavoriteLiveAdapter.FavoriteLiveViewHolder>() {
    private var container: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteLiveViewHolder {
        return FavoriteLiveViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.live_item, parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: FavoriteLiveViewHolder?, position: Int) {
        holder?.bindLive(favorites[position])
    }

    override fun getItemCount(): Int = favorites.size

    fun getData(): Array<LiveModel> = favorites

    @Synchronized
    fun updateData(newData: List<LiveModel>) {
        val sorted = newData.toTypedArray().sortedArray()

        val result = DiffUtil.calculateDiff(LiveDiffUtils(favorites, sorted))

        favorites = sorted

        result.dispatchUpdatesTo(this@FavoriteLiveAdapter)
    }

    inner class FavoriteLiveViewHolder(private val binding: LiveItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.liveFavorite.remove()

            binding.liveThumbnail.setOnClickListener { Navigator.goToLiveDetail(binding) }
        }

        fun bindLive(liveModel: LiveModel) {
            binding.live = liveModel

            binding.executePendingBindings()
        }
    }
}