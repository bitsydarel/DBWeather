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

package com.dbeginc.dbweather.news.newspaper.adapter.page.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.FragmentArticlesPageBinding
import com.dbeginc.dbweather.news.UpdatableContainer
import com.dbeginc.dbweather.news.newspaper.adapter.page.ArticlesPageContract
import com.dbeginc.dbweather.news.newspaper.adapter.page.adapter.ArticleAdapter
import com.dbeginc.dbweather.news.newspaper.adapter.page.presenter.ArticlesPagePresenterImpl
import com.dbeginc.dbweather.utils.holder.ConstantHolder.ARTICLES_DATA
import com.dbeginc.dbweather.utils.utility.getList
import com.dbeginc.dbweather.utils.utility.putList
import com.dbeginc.dbweather.viewmodels.news.ArticleModel
import com.dbeginc.dbweather.viewmodels.news.NewsPaperModel

/**
 * Created by darel on 10.10.17.
 *
 * Articles Page Fragment
 */
class ArticlesPageFragment : Fragment(), ArticlesPageContract.ArticlesPageView, UpdatableContainer {

    companion object {
        private val SOURCE_ID = "source_id"

        fun newInstance(sourceId: String, articles: List<ArticleModel>) : ArticlesPageFragment {
            val fragment = ArticlesPageFragment()

            val args = Bundle()
            args.putList(ARTICLES_DATA, articles)
            args.putString(SOURCE_ID, sourceId)
            fragment.arguments = args

            return fragment
        }
    }

    private lateinit var presenter: ArticlesPageContract.ArticlesPagePresenter
    private lateinit var binding: FragmentArticlesPageBinding
    private val adapter: ArticleAdapter = ArticleAdapter(mutableListOf())
    private lateinit var pageId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = if (savedInstanceState == null) ArticlesPagePresenterImpl(arguments.getList<ArticleModel>(ARTICLES_DATA).toMutableList().sorted())
        else ArticlesPagePresenterImpl(savedInstanceState.getList<ArticleModel>(ARTICLES_DATA).toMutableList().sorted())

        pageId = if (savedInstanceState == null) arguments.getString(SOURCE_ID) else savedInstanceState.getString(SOURCE_ID)
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_articles_page, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.articlesList.adapter = adapter
        binding.articlesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putList(ARTICLES_DATA, adapter.getData())
        outState?.putString(SOURCE_ID, pageId)
    }

    override fun getUpdatableId(): String = pageId

    override fun <T> update(data: T) {
        val casted = data as? NewsPaperModel

        if (casted != null) presenter.updateModel(casted.children)
    }

    /************************* Articles Page View Part *************************/
    override fun setupView() {
        presenter.loadArticles()
    }

    override fun cleanState() {
        presenter.unBind()
    }

    override fun displayArticles(articles: List<ArticleModel>) = adapter.update(articles)

    override fun toString(): String {
        return "${this::class.java.simpleName}:${adapter.getData().first().sourceId}"
    }
}