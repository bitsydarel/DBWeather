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

package com.dbeginc.dbweather.choosedefaultnewspapers


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentChooseNewsPapersBinding
import com.dbeginc.dbweather.managenewspapers.NewsPapersAdapter
import com.dbeginc.dbweather.managenewspapers.NewsPapersManagerBridge
import com.dbeginc.dbweather.utils.utility.goToIntroScreen
import com.dbeginc.dbweather.utils.utility.goToMainScreen
import com.dbeginc.dbweather.utils.utility.goToNewsPaperDetailScreen
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweathernews.managenewspapers.ManageNewsPapersViewModel
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

/**
 * A simple [Fragment] subclass.
 *
 */
class ChooseNewsPapersFragment : BaseFragment(), NewsPapersManagerBridge, MVMPVView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentChooseNewsPapersBinding

    private val newsPapersAdapter: NewsPapersAdapter by lazy {
        return@lazy NewsPapersAdapter(managerBridge = this)
    }

    private val viewModel: ManageNewsPapersViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[ManageNewsPapersViewModel::class.java]
    }

    override val stateObserver: Observer<RequestState> = Observer { state ->
        state?.let { onStateChanged(state = it) }
    }

    private val newsPapersObserver: Observer<List<NewsPaperModel>> = Observer { newsPapers ->
        newsPapers?.let { newsPapersAdapter.updateData(newData = it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.choose_news_papers_menu, menu)

        val searchView = menu.findItem(R.id.action_find_newspapers).actionView as? android.support.v7.widget.SearchView

        searchView?.let {
            it.isSubmitButtonEnabled = false

            it.queryHint = getString(R.string.enter_the_name)

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


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_done) {
            launch(UI) {
                val subscribedItemSize: Int = withContext(context = CommonPool) {
                    newsPapersAdapter.getData()
                            .filter { it.subscribed }
                            .size
                }

                activity?.let {
                    if (subscribedItemSize >= 3) goToMainScreen(currentScreen = it)
                    else binding.chooseNewsPapersLayout.snack(resId = R.string.please_subscribe_to_3_newspapers)
                }

            }
        }

        return true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_choose_news_papers,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.chooseNewsPapersToolbar)

        setupView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let { attachedActivity ->
            binding.chooseNewsPapersToolbar.setNavigationOnClickListener {
                goToIntroScreen(container = attachedActivity, layoutId = R.id.launchContent)
            }
        }

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getNewsPapers().observe(this, newsPapersObserver)

        onRefresh()
    }

    override fun onRefresh() = viewModel.loadNewspapers()

    override fun setupView() {
        binding.chooseNewsPapersList.adapter = newsPapersAdapter

        binding.chooseNewsPapersList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.chooseNewsPapersList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.chooseNewsPapersContainer.setOnRefreshListener(this)

    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.chooseNewsPapersContainer.isRefreshing = true
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
        binding.chooseNewsPapersContainer.isRefreshing = false
    }

    private fun onRequestFailed() {
        hideLoadingAnimation()

        Snackbar.make(binding.chooseNewsPapersContainer, R.string.newspapers_error_message, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) { onRefresh() }
                .show()

    }

}
