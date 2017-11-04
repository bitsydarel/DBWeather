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

package com.dbeginc.dbweather.news.lives.page.alllives.adapter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.news.lives.page.LiveDiffUtils
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.presenter.LivePresenterImpl
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.view.LiveViewHolder
import com.dbeginc.dbweather.viewmodels.news.LiveModel
import com.dbeginc.dbweatherdomain.usecases.news.AddLiveToFavorite
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*

/**
 * Created by darel on 18.10.17.
 *
 * Live adapter
 */
class LiveAdapter(data: MutableList<LiveModel>, private val favorites: MutableList<String>, private val addLiveToFavorite: AddLiveToFavorite) : RecyclerView.Adapter<LiveViewHolder>() {
    private var container: RecyclerView? = null
    private val presenters: LinkedList<LiveContract.LivePresenter>

    init {
        // mapping data to presenter
        presenters = LinkedList(data.map { live -> LivePresenterImpl(favorites.contains(live.name), live, addLiveToFavorite) })

        presenters.sortBy { live -> live.getData().name }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun getItemCount(): Int = presenters.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): LiveViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        return LiveViewHolder(DataBindingUtil.inflate(inflater, R.layout.live_item, parent, false))
    }

    override fun onBindViewHolder(holder: LiveViewHolder, position: Int) {
        val presenter = presenters[position]

        // cleaning state of view holder before binding it to new data
        holder.cleanState()

        holder.definePresenter(presenter)

        presenter.bind(holder)

        presenter.loadLive()
    }

    fun getData(): List<LiveModel> = presenters.map { presenter -> presenter.getData() }

    fun getFavoritesData(): List<String> = favorites

    fun updateData(newData: List<LiveModel>) {
        async(UI) {
            val result = bg { DiffUtil.calculateDiff(LiveDiffUtils(presenters.map { presenter -> presenter.getData() }, newData.sorted())) }.await()

            container?.post {
                presenters.clear()
                newData.mapTo(presenters) { live -> LivePresenterImpl(favorites.contains(live.name), live, addLiveToFavorite) }
                presenters.sortBy { presenter -> presenter.getData().name }
                result.dispatchUpdatesTo(this@LiveAdapter)
            }
        }
    }

    fun defineFavorites(newData: List<String>) {
        bg {
            favorites.clear()
            favorites.addAll(newData)
        }
    }

}