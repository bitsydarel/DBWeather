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

package com.dbeginc.dbweather.news.lives.page.alllives.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentLivesPageBinding
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.LiveAdapter
import com.dbeginc.dbweather.news.lives.page.alllives.AllLivesTabContract
import com.dbeginc.dbweather.utils.holder.ConstantHolder.FAVORITE_LIVES
import com.dbeginc.dbweather.utils.holder.ConstantHolder.LIVES_DATA
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweather.utils.utility.putList
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweather.viewmodels.news.LiveModel
import com.dbeginc.dbweatherdomain.usecases.news.AddLiveToFavorite
import java.util.*
import javax.inject.Inject

/**
 * Created by darel on 18.10.17.
 *
 * Lives Page Fragment
 */
class AllLivesTabPageFragment : BaseFragment(), AllLivesTabContract.AllLivesTabView {
    @Inject lateinit var addLiveToFavorite: AddLiveToFavorite
    @Inject lateinit var presenter: AllLivesTabContract.AllLivesTabPresenter
    private lateinit var adapter: LiveAdapter
    private lateinit var binding: FragmentLivesPageBinding

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectLivesPageDep(this)

        adapter = if (savedState == null) LiveAdapter(mutableListOf(), mutableListOf(), addLiveToFavorite)
        else LiveAdapter(savedState.getList<LiveModel>(LIVES_DATA).toMutableList(), savedState.getStringArrayList(FAVORITE_LIVES), addLiveToFavorite)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lives_page, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.livesLayout.setOnRefreshListener { presenter.loadAllLives() }
        binding.livesList.adapter = adapter
        binding.livesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putList(LIVES_DATA, adapter.getData())
        outState?.putStringArrayList(FAVORITE_LIVES, adapter.getFavoritesData() as ArrayList<String>?)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    /********************* All Lives Tab Page Part *********************/
    override fun setupView() {
        presenter.loadAllLives()
        presenter.getFavorite()
    }

    override fun cleanState() {
        presenter.unBind()
    }

    override fun displayAllLives(lives: List<LiveModel>) {
        adapter.updateData(lives)
    }

    override fun defineFavorites(favorites: List<String>) {
        adapter.defineFavorites(favorites)
    }

    override fun showUpdateStatus() {
        binding.livesLayout.isRefreshing = true
    }

    override fun hideUpdateStatus() {
        binding.livesLayout.isRefreshing = false
    }

    override fun showError(message: String) {
        binding.livesLayout.snack(message)
    }
}