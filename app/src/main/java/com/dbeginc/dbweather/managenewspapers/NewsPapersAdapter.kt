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

package com.dbeginc.dbweather.managenewspapers

import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.base.BaseDataDiff
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel

/**
 * Created by darel on 27.10.17.
 *
 * Sources Adapter
 */
class NewsPapersAdapter(private val managerBridge: NewsPapersManagerBridge) : BaseAdapter<NewsPaperModel, NewsPaperViewHolder>(differenceCalculator = NewsPapersDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsPaperViewHolder {
        return NewsPaperViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.newspaper_layout,
                parent,
                false
        ), managerBridge)
    }

    override fun onBindViewHolder(holder: NewsPaperViewHolder, position: Int) {
        val newsPaper = getItemForPosition(position)

        holder.bindNewsPaper(newsPaper)
    }

    class NewsPapersDiff : BaseDataDiff<NewsPaperModel>() {
        override fun areItemsTheSame(oldItem: NewsPaperModel?, newItem: NewsPaperModel?): Boolean {
            return oldItem?.id == newItem?.id
        }
    }

}