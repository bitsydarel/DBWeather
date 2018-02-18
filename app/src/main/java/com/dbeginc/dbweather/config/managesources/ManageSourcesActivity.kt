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

package com.dbeginc.dbweather.config.managesources

import android.app.SearchManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.config.managesources.adapter.SourcesAdapter
import com.dbeginc.dbweather.databinding.ActivityManageSourcesBinding
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_PAPERS
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweather.utils.utility.putList
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweatherdomain.repositories.news.NewsRepository
import com.dbeginc.dbweathernews.sourcesmanager.SourcesManagerViewModel
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerView
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

/**
 * Created by darel on 27.10.17.
 *
 * Manage Sources Activity
 */
class ManageSourcesActivity : BaseActivity(), SourcesManagerView, WithDependencies {
    @Inject
    lateinit var model: NewsRepository
    private lateinit var viewModel: SourcesManagerViewModel
    private lateinit var binding: ActivityManageSourcesBinding
    private lateinit var adapter: SourcesAdapter
    override val state: BehaviorSubject<RequestState> = BehaviorSubject.create()
    private var stateSubscription: Disposable? = null

    override fun onBackPressed() {
        Navigator.goToMainScreen(this)
        super.onBackPressed()
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_sources)

        adapter = if (savedState == null) SourcesAdapter(emptyList(), model) else SourcesAdapter(savedState.getList(NEWS_PAPERS), model)

        viewModel = ViewModelProviders.of(this, factory)[SourcesManagerViewModel::class.java]

        viewModel.getSources().observe(
                this,
                android.arch.lifecycle.Observer {
                    displaySources(it!!)
                }
        )

        state.subscribe(this::onStateChanged).also { stateSubscription = it }

        viewModel.presenter.bind(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_manage_sources_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = menu?.findItem(R.id.findSourceSearch)?.actionView as? SearchView

        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView?.isSubmitButtonEnabled = false

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(newText: String): Boolean {
                handleSearchQuery(newText)
                return true
            }
        })

        return true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent.action) {
            handleSearchQuery(intent.getStringExtra(SearchManager.QUERY))
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putList(NEWS_PAPERS, adapter.onSaveInstanceState())
    }

    /******************** Manage Sources Custom View ********************/
    override fun setupView() {
        setSupportActionBar(binding.manageSourceToolbar)

        binding.manageSourceToolbar.setNavigationOnClickListener { viewModel.presenter.onExitAction(this) }

        binding.sourcesList.adapter = adapter

        binding.sourcesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.manageSourcesUpdateStatus.setOnRefreshListener { viewModel.loadSources(state) }

        viewModel.loadSources(state)
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.manageSourcesUpdateStatus.isRefreshing = true
            RequestState.COMPLETED -> binding.manageSourcesUpdateStatus.isRefreshing = false
            RequestState.ERROR -> onSourcesRequestFailed()
        }
    }

    override fun close() = onBackPressed()

    override fun onSourcesRequestFailed() {
        Snackbar.make(binding.manageSourcesLayout, R.string.sources_error_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry, { viewModel.presenter.retrySourcesRequest() })
    }

    private fun handleSearchQuery(query: String) = if (query.isNotEmpty()) viewModel.findSource(state, query.toLowerCase()) else viewModel.loadSources(state)

    private fun displaySources(sources: List<SourceModel>) = adapter.update(sources)

}