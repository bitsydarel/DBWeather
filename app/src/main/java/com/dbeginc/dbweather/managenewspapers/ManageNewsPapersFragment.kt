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


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.dbeginc.dbweather.MainActivity
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentManageNewsPapersBinding
import com.dbeginc.dbweather.utils.utility.goToNewsPaperDetailScreen
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweathernews.managenewspapers.ManageNewsPapersViewModel
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel


/**
 * A ManageNewsPapersFragment [BaseFragment] subclass.
 */
class ManageNewsPapersFragment : BaseFragment(), MVMPVView, NewsPapersManagerBridge, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentManageNewsPapersBinding

    private val viewModel: ManageNewsPapersViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[ManageNewsPapersViewModel::class.java]
    }

    private val newsPapersAdapter: NewsPapersAdapter by lazy {
        return@lazy NewsPapersAdapter(managerBridge = this)
    }

    override val stateObserver: Observer<RequestState> = Observer {
        onStateChanged(state = it!!)
    }

    private val newsPapersObserver: Observer<List<NewsPaperModel>> = Observer {
        newsPapersAdapter.updateData(newData = it!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getNewsPapers().observe(this, newsPapersObserver)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.manage_sources_menu, menu)

        val searchView = menu.findItem(R.id.findNewsPaperSearch).actionView as? SearchView

        searchView?.let {
            it.isSubmitButtonEnabled = false

            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null && newText.isNotBlank()) viewModel.findNewspaper(query = newText)
                    else viewModel.loadNewspapers()
                    return true
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_manage_news_papers,
                container,
                false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.let { container ->
            container.setSupportActionBar(binding.manageNewsPapersToolbar)
            binding.manageNewsPapersToolbar.setNavigationOnClickListener { container.openNavigationDrawer() }
        }

        setupView()
    }

    override fun onRefresh() = viewModel.loadNewspapers()

    override fun setupView() {
        binding.manageNewsPapersList.adapter = newsPapersAdapter

        binding.manageNewsPapersList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.manageNewsPapersList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.manageNewsPapersContainer.setOnRefreshListener(this)

        viewModel.loadNewspapers()
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.manageNewsPapersContainer.isRefreshing = true
            RequestState.COMPLETED -> hideLoadingAnimation()
            RequestState.ERROR -> onRequestFailed()
        }
    }

    override fun subscribe(newsPaper: NewsPaperModel, position: Int) {
        viewModel.subscribeTo(newsPaper)
    }

    override fun unSubscribe(newsPaper: NewsPaperModel, position: Int) {
        viewModel.unSubscribe(newsPaper)
    }

    override fun goToNewsPaperDetail(newsPaper: NewsPaperModel) {
        activity?.let {
            goToNewsPaperDetailScreen(
                    container = it,
                    newsPaper = newsPaper
            )
        }
    }

    private fun hideLoadingAnimation() {
        binding.manageNewsPapersContainer.isRefreshing = false
    }

    private fun onRequestFailed() {
        hideLoadingAnimation()

        Snackbar.make(binding.manageNewsPapersLayout, R.string.newspapers_error_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) { viewModel.loadNewspapers() }
                .show()

    }

}