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

package com.dbeginc.dbweather.news.newspaper

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentNewspapersTabBinding
import com.dbeginc.dbweather.di.WithChildDependencies
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.news.newspaper.adapter.ArticlesPagerAdapter
import com.dbeginc.dbweather.utils.views.animations.ZoomOutSlideTransformer
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathernews.newspapers.NewsPapersViewModel
import com.dbeginc.dbweathernews.newspapers.contract.NewsPapersView
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by darel on 06.10.17.
 *
 * Articles Tab Fragment
 */
class NewsPaperTabFragment : BaseFragment(), NewsPapersView, WithDependencies, WithChildDependencies, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentNewspapersTabBinding
    private lateinit var viewModel: NewsPapersViewModel
    private val defaultAuthor by lazy { getString(R.string.default_author_name) }
    private val pageAdapter by lazy { ArticlesPagerAdapter(childFragmentManager) }
    private var stateSubscription: Disposable? = null
    override val state: BehaviorSubject<RequestState> = BehaviorSubject.create()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(activity, factory)[NewsPapersViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getNewsPapers().observe(this, android.arch.lifecycle.Observer {
            pageAdapter.refresh(it!!)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_Main_NewsTab)),
                R.layout.fragment_newspapers_tab,
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

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.articlesSRL.isRefreshing = true
            RequestState.COMPLETED -> binding.articlesSRL.isRefreshing = false
            RequestState.ERROR -> onRequestNewsFailed()
        }
    }

    /******************************************** Articles Tab View Part ********************************************/
    override fun setupView() {
        binding.articlesSRL.setColorSchemeResources(R.color.newsTabPrimaryTextColor)

        binding.articlesSRL.setOnRefreshListener(this::onRefresh)

        binding.articlesPage.offscreenPageLimit = 3

        binding.articlesPage.setPageTransformer(false, ZoomOutSlideTransformer())

        binding.articlesPage.adapter = pageAdapter

        binding.newsPaperIds.setupWithViewPager(binding.articlesPage, true)

        viewModel.loadArticles(state, defaultAuthor)
    }

    override fun displayNewsPapers(newsPapers: List<NewsPaperModel>) {
        if(userVisibleHint) pageAdapter.refresh(newsPapers)
    }

    override fun onRequestNewsFailed() {
        Snackbar.make(binding.articlesSRL, R.string.news_error_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry, { viewModel.presenter.retryNewsRequest() })
    }

    override fun onRefresh() = viewModel.loadArticles(state, defaultAuthor)

}