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

package com.dbeginc.dbweather.news.newspaper.adapter.page.adapter

import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ArticleItemBinding
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweathernews.viewmodels.ArticleModel
import java.util.*

/**
 * Created by darel on 10.10.17.
 *
 * Article Adapter
 */
class ArticleAdapter(data: List<ArticleModel>, private val sizeProvider: ViewPreloadSizeProvider<ArticleModel>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>(), ListPreloader.PreloadModelProvider<ArticleModel> {
    private var container: RecyclerView? = null

    private var articles: LinkedList<ArticleModel> = LinkedList(data.sorted())

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArticleViewHolder {
        val articleViewHolder = ArticleViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.article_item, parent, false))

        sizeProvider.setView(articleViewHolder.binding.articleImage)

        return articleViewHolder
    }

    override fun onBindViewHolder(holder: ArticleViewHolder?, position: Int) {
        holder?.bindArticle(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    override fun getPreloadRequestBuilder(article: ArticleModel): RequestBuilder<Drawable> {
        return Glide.with(container?.context)
                .load(article.urlToImage)
                .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
    }

    override fun getPreloadItems(position: Int): MutableList<ArticleModel> {
        val article = articles[position]
        return if (article.urlToImage == null) Collections.emptyList() else Collections.singletonList(article)
    }

    @Synchronized
    fun update(newData: List<ArticleModel>) {
        /*
        * Calculating the difference between the the current data and the new data on background
        * Returning an typedArray and new instance so the data is immutable
        * (nobody will modify it during the calculation)
        */
        val sorted = newData.sorted()

        val result = DiffUtil.calculateDiff(ArticleDiffUtils(articles, sorted))

        articles = LinkedList(sorted)

        result.dispatchUpdatesTo(this@ArticleAdapter)
    }

    inner class ArticleViewHolder(val binding: ArticleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.articleLayout.setOnClickListener { goToArticleDetail() }
        }

        fun bindArticle(article: ArticleModel) {
            binding.article = article

            Glide.with(container?.context)
                    .load(article.urlToImage)
                    .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                    .into(binding.articleImage)

            binding.executePendingBindings()
        }

        private fun goToArticleDetail() = Navigator.goToArticleDetail(binding)
    }

    fun getData(): List<ArticleModel> = articles

}