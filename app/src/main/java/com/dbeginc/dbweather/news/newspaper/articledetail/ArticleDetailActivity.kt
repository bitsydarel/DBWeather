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

package com.dbeginc.dbweather.news.newspaper.articledetail

import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ArticleDetailFeatureBinding
import com.dbeginc.dbweather.utils.holder.ConstantHolder.ARTICLES_DATA
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailPresenter
import com.dbeginc.dbweathernews.articledetail.contract.ArticleDetailView
import com.dbeginc.dbweathernews.viewmodels.ArticleModel
import javax.inject.Inject


/**
 * Created by darel on 07.10.17.
 *
 * Article Detail
 */
class ArticleDetailActivity : BaseActivity(), ArticleDetailView {
    @Inject lateinit var presenter: ArticleDetailPresenter
    private lateinit var binding: ArticleDetailFeatureBinding
    private val sharedWith by lazy { getString(R.string.share_with) }
    private val shareArticleText by lazy { getString(R.string.share_article_text) }
    private val defaultAuthor by lazy { getString(R.string.default_author_name) }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectArticleDetailDep(this)

        binding = DataBindingUtil.setContentView(this, R.layout.article_detail_feature)

        binding.article = if (savedState == null) intent.getParcelableExtra(ARTICLES_DATA) else savedState.getParcelable(ARTICLES_DATA)

        presenter.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(ARTICLES_DATA, binding.article)
    }

    /******************** Article Detail Activity ********************/
    override fun setupView() {
        setSupportActionBar(binding.articleDetailToolbar)

        binding.articleDetailToolbar.setNavigationOnClickListener { presenter.onExitAction() }

        binding.shareArticleFab.setOnClickListener { presenter.onShareAction() }

        binding.openFullArticle.setOnClickListener { presenter.onAction() }

        presenter.loadArticle()
    }

    override fun cleanState() = presenter.unBind()

    override fun displayArticle(article: ArticleModel) {
        binding.article = article
        binding.executePendingBindings()
    }

    override fun getDefaultAuthorName(): String = defaultAuthor

    override fun shareArticle() {
        val shareIntent = Intent()

        shareIntent.apply {
            action = Intent.ACTION_SEND
            type = MIMETYPE_TEXT_PLAIN
            putExtra(Intent.EXTRA_TEXT, shareArticleText.plus(Uri.parse(binding.article?.url)))
        }

        startActivity(Intent.createChooser(shareIntent, sharedWith))
    }

    override fun showLoading() = binding.loadingStatus.show()

    override fun hideLoading() = binding.loadingStatus.remove()

    override fun getSourceName(): String = binding.article!!.sourceId

    override fun close() = finish()

    override fun getArticleUrl(): String = binding.article!!.url

    override fun openFullArticle() = startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, Uri.parse(binding.article?.url)), getString(R.string.open_with)))

    override fun showError(error: String) = binding.articleDetailLayout.snack(error, duration=Snackbar.LENGTH_LONG)
}