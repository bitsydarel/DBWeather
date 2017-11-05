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

package com.dbeginc.dbweather.news.newspaper.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentNewspapersTabBinding
import com.dbeginc.dbweather.news.newspaper.NewsPapersTabContract
import com.dbeginc.dbweather.news.newspaper.adapter.ArticlesPagerAdapter
import com.dbeginc.dbweather.utils.animations.ZoomOutSlideTransformer
import com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_PAPERS
import com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweather.utils.utility.putList
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweather.viewmodels.news.NewsPaperModel
import javax.inject.Inject

/**
 * Created by darel on 06.10.17.
 *
 * Articles Tab Fragment
 */
class NewsPaperTabFragment : BaseFragment(), NewsPapersTabContract.NewsPapersTabView {
    @Inject lateinit var presenter: NewsPapersTabContract.NewsPapersTabPresenter
    private lateinit var binding: FragmentNewspapersTabBinding
    private lateinit var pageAdapter: ArticlesPagerAdapter
    private val defaultAuthor by lazy { getString(R.string.default_author_name) }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectArticlesTabDep(this)

        pageAdapter = if (savedState == null) ArticlesPagerAdapter(listOf(), childFragmentManager)
        else ArticlesPagerAdapter(savedState.getList(NEWS_PAPERS), childFragmentManager)

    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_newspapers_tab, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.articlesSRL.setColorSchemeResources(R.color.newsTabPrimary)
        binding.articlesSRL.setOnRefreshListener { presenter.loadArticles() }

        if (binding.articlesPage.adapter == null) {
            binding.articlesPage.adapter = pageAdapter
            binding.newsPaperIds.setupWithViewPager(binding.articlesPage, true)
            binding.articlesPage.setPageTransformer(false, ZoomOutSlideTransformer())
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putList(NEWS_PAPERS, pageAdapter.getData())
    }

    /******************************************** Articles Tab View Part ********************************************/
    override fun setupView() {
        presenter.loadArticles()
    }

    override fun cleanState() = presenter.unBind()

    override fun displayNewsPapers(newsPapers: List<NewsPaperModel>) {
        pageAdapter.refresh(newsPapers)
    }

    override fun showUpdateStatus() {
        binding.articlesSRL.isRefreshing = true
    }

    override fun hideUpdateStatus() {
        binding.articlesSRL.isRefreshing = false
    }

    override fun getDefaultAuthorName(): String = defaultAuthor

    override fun showError(message: String) {
        binding.articlesTabLayout.snack(message)
        Log.e(TAG, message)
    }
}