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

package com.dbeginc.dbweather.news.newspaper.adapter.page

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ArticlesFeatureBinding
import com.dbeginc.dbweather.news.newspaper.adapter.page.adapter.ArticleAdapter
import com.dbeginc.dbweather.utils.holder.ConstantHolder.ARTICLES_DATA
import com.dbeginc.dbweather.utils.utility.getArray
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathercommon.utils.UpdatableContainer
import com.dbeginc.dbweathernews.articles.contract.ArticlesPresenter
import com.dbeginc.dbweathernews.articles.contract.ArticlesView
import com.dbeginc.dbweathernews.articles.presenter.ArticlesPresenterImpl
import com.dbeginc.dbweathernews.viewmodels.ArticleModel
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel

/**
 * Created by darel on 10.10.17.
 *
 * Articles Page Fragment
 */
class ArticlesPageFragment : Fragment(), ArticlesView, UpdatableContainer {

    companion object {
        private const val SOURCE_ID = "source_id"
        private const val PRELOAD_AHEAD_ITEMS = 5

        fun newInstance(sourceId: String, articles: List<ArticleModel>) : ArticlesPageFragment {
            val fragment = ArticlesPageFragment()

            fragment.arguments = Bundle().apply {
                putParcelableArray(ARTICLES_DATA, articles.toTypedArray())
                putString(SOURCE_ID, sourceId)
            }

            return fragment
        }
    }

    private lateinit var presenter: ArticlesPresenter
    private lateinit var preloader: RecyclerViewPreloader<ArticleModel>
    private lateinit var binding: ArticlesFeatureBinding
    private lateinit var adapter: ArticleAdapter
    private lateinit var pageId: String
    private val sizeProvider = ViewPreloadSizeProvider<ArticleModel>()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        val articles: Array<ArticleModel>

        if(savedState == null) {
            articles = arguments.getArray(ARTICLES_DATA)
            pageId = arguments.getString(SOURCE_ID)

        } else {
            articles = savedState.getArray(ARTICLES_DATA)
            pageId = savedState.getString(SOURCE_ID)
        }

        presenter = ArticlesPresenterImpl(articles.asList())

        adapter = ArticleAdapter(articles, sizeProvider)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.articles_feature, container, false)

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArray(ARTICLES_DATA, adapter.getData())

        outState.putString(SOURCE_ID, pageId)
    }

    override fun getUpdatableId(): String = pageId

    override fun <T> update(data: T) {
        val casted = data as? NewsPaperModel

        if (casted != null) presenter.updateModel(this, casted.children)
    }

    /************************* Articles Page View Part *************************/
    override fun setupView() {
        preloader = RecyclerViewPreloader(this, adapter, sizeProvider, PRELOAD_AHEAD_ITEMS)

        binding.articlesList.adapter = adapter

        binding.articlesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.articlesList.addOnScrollListener(preloader)

        presenter.loadArticles(this)
    }

    override fun cleanState() = presenter.unBind()

    override fun displayArticles(articles: List<ArticleModel>) {
        if (userVisibleHint) adapter.update(articles)
    }

    override fun showMessage(message: String) = binding.articlesList.snack(message)

    override fun toString(): String = super.toString() + "@${arguments.getString(SOURCE_ID)}"
}