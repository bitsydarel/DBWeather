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

package com.dbeginc.dbweather.config.managesources.adapter

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.config.managesources.adapter.contract.SourcePresenter
import com.dbeginc.dbweather.config.managesources.adapter.presenter.SourcePresenterImpl
import com.dbeginc.dbweather.config.managesources.adapter.view.SourceViewHolder
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import java.util.*

/**
 * Created by darel on 27.10.17.
 *
 * Sources Adapter
 */
class SourcesAdapter(sources: List<SourceModel>,
                     private val model: NewsRepository) : RecyclerView.Adapter<SourceViewHolder>() {

    private var container: RecyclerView? = null
    private val presenters: LinkedList<SourcePresenter>

    init {
        presenters = LinkedList(
                sources.map { source -> SourcePresenterImpl(source, model) }
                        .sortedBy { presenter -> presenter.getData().name }
        )
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun getItemCount(): Int = presenters.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SourceViewHolder {
        return SourceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.source_item, parent, false))
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        val presenter = presenters[position]

        // cleaning state of view holder before binding it to new data
        holder.cleanState()

        holder.definePresenter(presenter)

        presenter.bind(holder)

        presenter.loadSource(holder)
    }

    fun update(newData: List<SourceModel>) {
        val result = DiffUtil.calculateDiff(SourceDiffUtil(presenters, newData.sortedBy { source -> source.name }))

        container?.post {
            presenters.clear()

            newData.mapTo(presenters) { source -> SourcePresenterImpl(source, model) }

            presenters.sortBy { presenter -> presenter.getData().name }

            result.dispatchUpdatesTo(this@SourcesAdapter)
        }
    }

    fun onSaveInstanceState(): List<SourceModel> {
        presenters.forEach { presenter -> presenter.unBind() }
        return presenters.map { presenter -> presenter.getData() }
    }
}