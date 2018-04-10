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

import android.support.v7.widget.RecyclerView
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.NewspaperLayoutBinding
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel

/**
 * Created by darel on 27.10.17.
 *
 * NewsPaper View Holder
 */
class NewsPaperViewHolder(private val binding: NewspaperLayoutBinding, managerBridge: NewsPapersManagerBridge) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.newsPaperLayout.setOnClickListener {
            binding.newsPaper?.let {
                managerBridge.goToNewsPaperDetail(newsPaper = it)
            }
        }

        binding.sourceSubscribed.setOnClickListener {
            binding.newsPaper?.let {
                managerBridge.unSubscribe(newsPaper = it.apply { subscribed = !subscribed }, position = adapterPosition)

                if (it.subscribed) binding.sourceSubscribed.setImageResource(R.drawable.ic_follow_icon_red)
                else binding.sourceSubscribed.setImageResource(R.drawable.ic_un_follow_icon_black)

            }
        }
    }

    fun bindNewsPaper(newsPaper: NewsPaperModel) {
        binding.newsPaper = newsPaper

        binding.sourceSubscribed.setImageResource(
                if (newsPaper.subscribed) R.drawable.ic_follow_icon_red
                else R.drawable.ic_un_follow_icon_black
        )

        binding.executePendingBindings()
    }

}