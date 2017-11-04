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

package com.dbeginc.dbweather.config.managesources.adapter.view

import android.support.v7.widget.RecyclerView
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.config.managesources.adapter.SourceContract
import com.dbeginc.dbweather.databinding.SourceItemBinding
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweather.viewmodels.news.SourceModel

/**
 * Created by darel on 27.10.17.
 *
 * Source View Holder
 */
class SourceViewHolder(private val binding: SourceItemBinding): RecyclerView.ViewHolder(binding.root), SourceContract.SourceView {
    private var presenter: SourceContract.SourcePresenter? = null

    override fun setupView() {/*** Not needed here ****/}

    override fun cleanState() {
        presenter?.unBind()
    }

    override fun displaySource(source: SourceModel) {
        binding.source = source
        binding.sourceSubscribed.setImageResource(if (source.subscribed) R.drawable.follow_icon else R.drawable.un_follow_icon)
        binding.executePendingBindings()
    }

    override fun showSubscribed() = binding.sourceSubscribed.setImageResource(R.drawable.follow_icon)

    override fun showUnSubscribed() = binding.sourceSubscribed.setImageResource(R.drawable.un_follow_icon)

    override fun goToSourceDetail() = binding.sourceLayout.toast("Going to Detail for ${binding.source?.name}")

    override fun showUpdateStatus() = binding.sourceUpdatingStatus.show()

    override fun hideUpdateStatus() = binding.sourceUpdatingStatus.remove()

    override fun showError(message: String) = binding.sourceLayout.toast(message)

    override fun definePresenter(presenter: SourceContract.SourcePresenter) {
        this.presenter = presenter
        binding.sourceLayout.setOnClickListener { this.presenter?.onAction() }
        binding.sourceSubscribed.setOnClickListener { this.presenter?.onSubscribe() }
    }
}