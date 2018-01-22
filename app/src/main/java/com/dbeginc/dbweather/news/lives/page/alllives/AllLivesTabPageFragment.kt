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

package com.dbeginc.dbweather.news.lives.page.alllives

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.LivesFeatureBinding
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.LiveAdapter
import com.dbeginc.dbweather.utils.holder.ConstantHolder.LIVES_DATA
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweather.utils.utility.putList
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweatherdomain.usecases.news.AddLiveToFavorite
import com.dbeginc.dbweatherdomain.usecases.news.RemoveLiveToFavorite
import com.dbeginc.dbweathernews.lives.contract.LivesPresenter
import com.dbeginc.dbweathernews.lives.contract.LivesView
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import javax.inject.Inject

/**
 * Created by darel on 18.10.17.
 *
 * Lives Page Fragment
 */
class AllLivesTabPageFragment : BaseFragment(), LivesView {
    @Inject lateinit var addLiveToFavorite: AddLiveToFavorite
    @Inject lateinit var removeToFavorite: RemoveLiveToFavorite
    @Inject lateinit var presenter: LivesPresenter
    private lateinit var adapter: LiveAdapter
    private lateinit var binding: LivesFeatureBinding

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectLivesPageDep(this)

        adapter = if(savedState == null) LiveAdapter(emptyList(), addLiveToFavorite, removeToFavorite) else LiveAdapter(savedState.getList(LIVES_DATA), addLiveToFavorite, removeToFavorite)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.lives_feature, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bind(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putList(LIVES_DATA, adapter.getData())
    }

    override fun onDestroyView() {
        super.onDestroyView()

        cleanState()
    }

    /********************* All Lives Tab Page Part *********************/
    override fun setupView() {
        binding.livesLayout.setOnRefreshListener { presenter.loadAllLives() }

        binding.livesList.adapter = adapter

        binding.livesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        presenter.loadAllLives()
    }

    override fun cleanState() = presenter.unBind()

    override fun displayLives(lives: List<LiveModel>) = adapter.updateData(lives)

    override fun showLoading() {
        binding.livesLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.livesLayout.isRefreshing = false
    }

    override fun showError(error: String) = binding.livesLayout.snack(error)

}