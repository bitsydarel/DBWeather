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

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.LivesFeatureBinding
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.news.lives.page.alllives.adapter.LiveAdapter
import com.dbeginc.dbweather.utils.holder.ConstantHolder.LIVES_DATA
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.lives.LivesViewModel
import com.dbeginc.dbweathernews.lives.contract.LivesView
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Created by darel on 18.10.17.
 *
 * Lives Page Fragment
 */
class AllLivesTabPageFragment : BaseFragment(), LivesView, WithDependencies, SwipeRefreshLayout.OnRefreshListener {
    @Inject
    lateinit var model: NewsRepository
    private lateinit var viewModel: LivesViewModel
    private lateinit var adapter: LiveAdapter
    private lateinit var binding: LivesFeatureBinding
    private var stateSubscription: Disposable? = null
    override val state: BehaviorSubject<RequestState> = BehaviorSubject.create()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(activity, factory)[LivesViewModel::class.java]
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        adapter = if (savedState == null) LiveAdapter(emptyList(), model) else LiveAdapter(savedState.getList(LIVES_DATA), model)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getLives().observe(this, android.arch.lifecycle.Observer {
            displayLives(it!!)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_Main_NewsTab)),
                R.layout.lives_feature,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        state.subscribe(this::onStateChanged).also { stateSubscription = it }

        viewModel.presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stateSubscription?.dispose()
    }

    /********************* All Lives Tab Page Part *********************/
    override fun setupView() {
        binding.livesLayout.setOnRefreshListener(this::onRefresh)

        binding.livesList.adapter = adapter

        binding.livesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        viewModel.loadAllLives(state)
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.livesLayout.isRefreshing = true
            RequestState.COMPLETED -> binding.livesLayout.isRefreshing = false
            RequestState.ERROR -> onLivesRequestFailed()
        }
    }

    override fun onLivesRequestFailed() {
        Snackbar.make(binding.livesLayout, R.string.lives_error_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry, { viewModel.presenter.retryLivesRequest() })
    }

    override fun onRefresh() = viewModel.loadAllLives(state)

    private fun displayLives(lives: List<LiveModel>) = adapter.updateData(lives)

}