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

package com.dbeginc.dbweather.news.newspaper.articledetail.view

import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityArticleDetailBinding
import com.dbeginc.dbweather.news.newspaper.articledetail.ArticleDetailContract
import com.dbeginc.dbweather.utils.holder.ConstantHolder.ARTICLES_DATA
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.viewmodels.news.ArticleModel
import javax.inject.Inject


/**
 * Created by darel on 07.10.17.
 *
 * Article Detail
 */
class ArticleDetailActivity : BaseActivity(), ArticleDetailContract.ArticleDetailView {
    @Inject lateinit var presenter: ArticleDetailContract.ArticleDetailPresenter
    private lateinit var binding: ActivityArticleDetailBinding
    private val sharedWith by lazy { getString(R.string.share_with) }
    private val shareArticleText by lazy { getString(R.string.share_article_text) }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectArticleDetailDep(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail)

        binding.article = if (savedState == null) intent.getParcelableExtra(ARTICLES_DATA) else savedState.getParcelable(ARTICLES_DATA)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(ARTICLES_DATA, binding.article)
    }

    /******************** Article Detail Activity ********************/
    override fun setupView() {
        setSupportActionBar(binding.articleDetailToolbar)

        binding.articleDetailToolbar.setNavigationOnClickListener { presenter.onBack() }

        binding.shareArticleFab.setOnClickListener { presenter.onShare() }

        binding.openFullArticle.setOnClickListener { presenter.onAction() }

        presenter.loadArticle()
    }

    override fun cleanState() {
        presenter.unBind()
    }

    override fun displayArticle(article: ArticleModel) {
        binding.article = article
        binding.executePendingBindings()
    }

    override fun shareArticle() {
        val shareIntent = Intent()

        shareIntent.apply {
            action = Intent.ACTION_SEND
            type = MIMETYPE_TEXT_PLAIN
            putExtra(Intent.EXTRA_TEXT, shareArticleText.plus(Uri.parse(binding.article?.url)))
        }

        startActivity(Intent.createChooser(shareIntent, sharedWith))
    }

    override fun goBackToArticles() = finish()

    override fun getSource(): String = binding.article!!.sourceId

    override fun getArticleUrl(): String = binding.article!!.url

    override fun openFullArticle() {
        startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse(binding.article?.url)), getString(R.string.open_with)))
    }
}