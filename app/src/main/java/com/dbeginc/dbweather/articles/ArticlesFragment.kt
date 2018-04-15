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

package com.dbeginc.dbweather.articles

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentArticlesBinding
import com.dbeginc.dbweather.utils.utility.LOADING_PERIOD
import com.dbeginc.dbweather.utils.utility.goToArticleDetailScreen
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweathernews.articles.ArticlesViewModel
import com.dbeginc.dbweathernews.viewmodels.ArticleModel

/**
 * Created by darel on 10.10.17.
 *
 * Articles Page Fragment
 */
class ArticlesFragment : BaseFragment(), MVMPVView, ArticleActionBridge, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    companion object {
        private const val NEWSPAPER_ID = "source_id"
        private const val NEWSPAPER_NAME = "source_name"
        private const val PRELOAD_AHEAD_ITEMS = 5

        fun newInstance(sourceId: String, sourceName: String): ArticlesFragment {
            val fragment = ArticlesFragment()

            val args = Bundle()

            args.putString(NEWSPAPER_ID, sourceId)

            args.putString(NEWSPAPER_NAME, sourceName)

            fragment.arguments = args

            return fragment
        }
    }

    lateinit var newsPaperId: String
    private lateinit var newsPaperName: String
    private lateinit var binding: FragmentArticlesBinding
    private val sizeProvider = ViewPreloadSizeProvider<String>()

    private val viewModel: ArticlesViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[ArticlesViewModel::class.java]
    }

    private val preloader by lazy {
        RecyclerViewPreloader(this, articlesAdapter, sizeProvider, PRELOAD_AHEAD_ITEMS)
    }

    private val articlesAdapter: ArticleAdapter by lazy {
        ArticleAdapter(containerBridge = this, sizeProvider = sizeProvider)
    }

    private val articlesObserver: Observer<List<ArticleModel>> = Observer {
        articlesAdapter.updateData(newData = it!!)
    }

    override val stateObserver: Observer<RequestState> = Observer {
        onStateChanged(state = it!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getArticles().observe(this, articlesObserver)

    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        if (savedState == null) {
            newsPaperId = arguments!!.getString(NEWSPAPER_ID)
            newsPaperName = arguments!!.getString(NEWSPAPER_NAME)
        } else {
            newsPaperId = savedState.getString(NEWSPAPER_ID)
            newsPaperName = savedState.getString(NEWSPAPER_NAME)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(NEWSPAPER_ID, newsPaperId)
        outState.putString(NEWSPAPER_NAME, newsPaperName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_articles,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

    }

    override fun goToArticleDetail(article: ArticleModel) {
        activity?.let {
            goToArticleDetailScreen(
                    container = it,
                    article = article
            )
        }
    }

    override fun onRefresh() = viewModel.loadArticles(newspaperId = newsPaperId, newspaperName = newsPaperName)

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        binding.articlesLayout.isEnabled = verticalOffset == 0
    }

    override fun setupView() {
        binding.articlesLayout.setOnRefreshListener(this)

        binding.articlesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.articlesList.adapter = articlesAdapter

        binding.articlesList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        binding.articlesList.addOnScrollListener(preloader)

        binding.articlesLayout.isEnabled = false

        viewModel.loadArticles(newspaperId = newsPaperId, newspaperName = newsPaperName)

    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.articlesLayout.postDelayed(this::showLoadingStatus, LOADING_PERIOD)
            RequestState.COMPLETED -> binding.articlesLayout.postDelayed(this::hideLoadingStatus, LOADING_PERIOD)
            RequestState.ERROR -> binding.articlesLayout.postDelayed(this::onRequestFailed, LOADING_PERIOD)
        }
    }

    override fun toString(): String = super.toString() + "@${arguments!!.getString(NEWSPAPER_ID)}"

    private fun showLoadingStatus() {
        binding.articlesLayout.isRefreshing = true
    }

    private fun hideLoadingStatus() {
        binding.articlesLayout.isRefreshing = false
    }

    private fun onRequestFailed() {
        hideLoadingStatus()

        Snackbar.make(binding.articlesLayout, R.string.error_could_not_load_articles, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) { viewModel.loadArticles(newspaperId = newsPaperId, newspaperName = newsPaperName) }
                .show()

    }

}