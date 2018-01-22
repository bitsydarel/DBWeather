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
import com.dbeginc.dbweather.config.managesources.adapter.contract.SourcePresenter
import com.dbeginc.dbweather.config.managesources.adapter.contract.SourceView
import com.dbeginc.dbweather.databinding.SourceItemBinding
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweathernews.viewmodels.SourceModel

/**
 * Created by darel on 27.10.17.
 *
 * Source View Holder
 */
class SourceViewHolder(private val binding: SourceItemBinding): RecyclerView.ViewHolder(binding.root), SourceView {
    private var presenter: SourcePresenter? = null

    init {
        binding.sourceLayout.setOnClickListener { presenter?.onAction() }

        binding.sourceSubscribed.setOnClickListener { presenter?.onSubscribe() }
    }

    override fun setupView() {  }

    override fun cleanState() {
        presenter?.unBind()
    }

    override fun displaySource(source: SourceModel) {
        binding.source = source
        binding.sourceSubscribed.setImageResource(if (source.subscribed) R.drawable.ic_follow_icon_red else R.drawable.ic_un_follow_icon_grey)
        binding.executePendingBindings()
    }

    override fun definePresenter(newPresenter: SourcePresenter) {
        this.presenter = newPresenter
    }

    override fun showSubscribed() = binding.sourceSubscribed.setImageResource(R.drawable.ic_follow_icon_red)

    override fun showUnSubscribed() = binding.sourceSubscribed.setImageResource(R.drawable.ic_un_follow_icon_grey)

    override fun goToSourceDetail() = Navigator.goToSourceDetail(binding)

    override fun showLoading() = binding.sourceUpdatingStatus.show()

    override fun hideLoading() = binding.sourceUpdatingStatus.remove()

    override fun showError(error: String) = binding.sourceLayout.toast(error)

}