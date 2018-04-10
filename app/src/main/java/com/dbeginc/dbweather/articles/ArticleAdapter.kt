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

import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
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
import com.dbeginc.dbweather.base.BaseAdapter
import com.dbeginc.dbweather.base.BaseDataDiff
import com.dbeginc.dbweather.databinding.ArticleLayoutBinding
import com.dbeginc.dbweathernews.viewmodels.ArticleModel
import java.util.*

/**
 * Created by darel on 10.10.17.
 *
 * Article Adapter
 */
class ArticleAdapter(
        private val containerBridge: ArticleActionBridge,
        private val sizeProvider: ViewPreloadSizeProvider<String>
) : BaseAdapter<ArticleModel, ArticleAdapter.ArticleViewHolder>(differenceCalculator = ArticleDifferenceCalculator()), ListPreloader.PreloadModelProvider<String> {
    private var container: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        container = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val articleViewHolder = ArticleViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.article_layout,
                parent,
                false
        ), containerBridge)

        sizeProvider.setView(articleViewHolder.binding.articleImage)

        return articleViewHolder
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bindArticle(getItemForPosition(position))
    }

    override fun getPreloadRequestBuilder(imageUrl: String): RequestBuilder<Drawable> {
        return Glide.with(container?.context)
                .load(imageUrl)
                .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
    }

    override fun getPreloadItems(position: Int): MutableList<String> {
        val articleImageUrl = getItemForPosition(position).imageUrl

        return if (articleImageUrl == null) Collections.emptyList() else Collections.singletonList(articleImageUrl)
    }

    inner class ArticleViewHolder(internal val binding: ArticleLayoutBinding, private val containerBridge: ArticleActionBridge) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.articleLayout.setOnClickListener {
                containerBridge.goToArticleDetail(
                        article = binding.article!!
                )
            }
        }

        fun bindArticle(article: ArticleModel) {
            binding.article = article

            Glide.with(binding.articleImage)
                    .load(article.imageUrl)
                    .apply(RequestOptions.errorOf(R.drawable.no_image_icon))
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                    .into(binding.articleImage)

            binding.executePendingBindings()
        }
    }

    /**
     * Created by darel on 10.10.17.
     *
     * Article Difference Calculator
     */
    class ArticleDifferenceCalculator : BaseDataDiff<ArticleModel>() {
        override fun areItemsTheSame(oldItem: ArticleModel?, newItem: ArticleModel?): Boolean =
                oldItem?.url == newItem?.url
    }

}