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

package com.dbeginc.dbweather.news.lives.page.alllives.adapter.view

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageButton
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.LiveItemBinding
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.LiveContract
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.viewmodels.news.LiveModel

/**
 * Created by darel on 19.10.17.
 *
 * Live View Holder
 */
class LiveViewHolder(private val binding: LiveItemBinding) : RecyclerView.ViewHolder(binding.root), LiveContract.LiveView {
    private var presenter: LiveContract.LivePresenter? = null

    override fun setupView() {}

    override fun cleanState() {
        presenter?.unBind()
    }

    override fun displayLive(live: LiveModel, isFavorite: Boolean) {
        binding.live = live
        binding.liveFavorite.apply { if (isFavorite) bookmark() else unBookmark() }
        binding.executePendingBindings()
    }

    override fun showBookmarkAnimation() {
        binding.liveFavorite.bookmark()
    }

    override fun showUnBookmarkAnimation() {
        binding.liveFavorite.unBookmark()
    }

    override fun showError(error: Throwable) {
        Log.e(ConstantHolder.TAG, error.localizedMessage, error)
    }

    override fun definePresenter(presenter: LiveContract.LivePresenter) {
        this.presenter = presenter
        binding.liveFavorite.setOnClickListener { presenter.addToFavorite() }
        binding.liveThumbnail.setOnClickListener { play() }
    }

    override fun play() {
        Navigator.goToLiveDetail(binding)
    }

    private fun ImageButton.bookmark() = setImageResource(R.drawable.ic_bookmarked)

    private fun ImageButton.unBookmark() = setImageResource(R.drawable.ic_not_bookmark_black)

}