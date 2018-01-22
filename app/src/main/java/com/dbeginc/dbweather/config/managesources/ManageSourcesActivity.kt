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
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.config.managesources.adapter.SourcesAdapter
import com.dbeginc.dbweather.databinding.ActivityManageSourcesBinding
import com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_PAPERS
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweather.utils.utility.putList
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweatherdomain.usecases.news.SubscribeToSource
import com.dbeginc.dbweatherdomain.usecases.news.UnSubscribeToSource
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerPresenter
import com.dbeginc.dbweathernews.sourcesmanager.contract.SourcesManagerView
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import javax.inject.Inject

/**
 * Created by darel on 27.10.17.
 *
 * Manage Sources Activity
 */
class ManageSourcesActivity : BaseActivity(), SourcesManagerView {
    @Inject lateinit var presenter: SourcesManagerPresenter
    @Inject lateinit var subscribeToSource: SubscribeToSource
    @Inject lateinit var unSubscribeToSource: UnSubscribeToSource
    private lateinit var binding: ActivityManageSourcesBinding
    private lateinit var adapter: SourcesAdapter

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectManageSourcesDep(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_sources)

        adapter = if (savedState == null) SourcesAdapter(emptyList(), subscribeToSource, unSubscribeToSource) else SourcesAdapter(savedState.getList(NEWS_PAPERS), subscribeToSource, unSubscribeToSource)

        presenter.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        cleanState()
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

        binding.manageSourceToolbar.setNavigationOnClickListener { presenter.onExitAction() }

        binding.sourcesList.adapter = adapter

        binding.sourcesList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.manageSourcesUpdateStatus.setOnRefreshListener { presenter.loadSources() }

        presenter.loadSources()
    }

    override fun cleanState() = presenter.unBind()

    override fun showLoading() {
        binding.manageSourcesUpdateStatus.isRefreshing = true
    }

    override fun hideLoading() {
        binding.manageSourcesUpdateStatus.isRefreshing = false
    }

    override fun displaySources(sources: List<SourceModel>) = adapter.update(sources)

    override fun close() = finish()

    override fun showError(error: String) = binding.manageSourcesLayout.snack(error)

    private fun handleSearchQuery(query: String) = if (query.isNotEmpty()) presenter.findSource(query.toLowerCase()) else presenter.loadSources()

}