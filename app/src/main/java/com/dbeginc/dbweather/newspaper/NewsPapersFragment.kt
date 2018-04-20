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

package com.dbeginc.dbweather.newspaper

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.MainActivity
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentNewspapersBinding
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweathernews.newspapers.NewsPapersViewModel
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel

/**
 * Created by darel on 06.10.17.
 *
 * Articles Tab Fragment
 */
class NewsPapersFragment : BaseFragment(), MVMPVView {
    private lateinit var binding: FragmentNewspapersBinding

    private val viewModel: NewsPapersViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[NewsPapersViewModel::class.java]
    }

    private val pageAdapter: ArticlesPagerAdapter by lazy {
        return@lazy ArticlesPagerAdapter(childFragmentManager)
    }

    private val sourcesObserver: Observer<List<NewsPaperModel>> = Observer { newspapers ->
        newspapers?.let { pageAdapter.refresh(it) }
    }

    override val stateObserver: Observer<RequestState> = Observer { state ->
        state?.let { onStateChanged(state = it) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as? MainActivity)?.let { container ->
            binding.newsPapersToolbar.setNavigationOnClickListener { container.openNavigationDrawer() }
        }

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getNewsPapers().observe(this, sourcesObserver)

        getNewsPapers()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.support.v7.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_newspapers,
                container,
                false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setSupportActionBar(binding.newsPapersToolbar)

        setupView()
    }

    /******************************************** Articles Tab View Part ********************************************/
    override fun setupView() {
        binding.newsPapersArticles.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                // First we add the new page to offset change listener
                binding.newsPapersAppbar.addOnOffsetChangedListener(pageAdapter.getRegisteredFragment(position))
                // Second remove previous one, no problem if its not inside or if it's null source code of appbar check for null
                binding.newsPapersAppbar.removeOnOffsetChangedListener(pageAdapter.getRegisteredFragment(position - 1))
                binding.newsPapersAppbar.removeOnOffsetChangedListener(pageAdapter.getRegisteredFragment(position + 1))
            }
        })

        binding.newsPapersArticles.offscreenPageLimit = 3

        binding.newsPapersArticles.adapter = pageAdapter

        binding.newsPaperIds.setupWithViewPager(binding.newsPapersArticles, true)

    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> return
            RequestState.COMPLETED -> return
            RequestState.ERROR -> onRequestNewsPapersFailed()
        }
    }

    private fun onRequestNewsPapersFailed() {
        Snackbar.make(binding.newsPapersLayout, R.string.news_error_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry) { getNewsPapers() }
    }

    private fun getNewsPapers() {
        val currentSortingPreferences = preferences.get().getNewsPaperPreferredOrder()

        viewModel.loadNewspaperSources(sortBy = currentSortingPreferences)
    }

}