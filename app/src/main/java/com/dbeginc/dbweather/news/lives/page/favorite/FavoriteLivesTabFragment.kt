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

package com.dbeginc.dbweather.news.lives.page.favorite

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentFavoriteLivesTabLayoutBinding
import com.dbeginc.dbweather.news.lives.page.favorite.adapter.FavoriteLiveAdapter
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweather.utils.utility.putList
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesPresenter
import com.dbeginc.dbweathernews.favoritelives.contract.FavoriteLivesView
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import javax.inject.Inject

/**
 * Created by darel on 20.10.17.
 *
 * Favorite Lives Page Fragment
 */
class FavoriteLivesTabFragment : BaseFragment(), FavoriteLivesView {
    @Inject lateinit var presenter: FavoriteLivesPresenter
    private lateinit var adapter: FavoriteLiveAdapter
    private lateinit var binding: FragmentFavoriteLivesTabLayoutBinding

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectFavoriteLivesPageDep(this)

        adapter = if (savedState == null) FavoriteLiveAdapter(emptyList()) else FavoriteLiveAdapter(savedState.getList(ConstantHolder.LIVES_DATA))

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putList(ConstantHolder.LIVES_DATA, adapter.getData())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favorite_lives_tab_layout, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        cleanState()
    }

    /******************** Favorite Lives Tab View Part *********************/
    override fun setupView() {

        binding.favoriteLivesLayout.setOnRefreshListener { presenter.loadFavoriteLives() }

        binding.favoriteLivesList.adapter = adapter

        binding.favoriteLivesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        presenter.loadFavoriteLives()
    }

    override fun cleanState() {
        presenter.unBind()
    }

    override fun displayFavoriteLives(lives: List<LiveModel>) {
        adapter.updateData(lives)
    }

    override fun showLoading() {
        binding.favoriteLivesLayout.isRefreshing = true
    }

    override fun hideLoading() {
        binding.favoriteLivesLayout.isRefreshing = false
    }

    override fun showError(error: String) = binding.favoriteLivesLayout.snack(error)

}