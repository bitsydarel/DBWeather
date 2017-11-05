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
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ArticleItemBinding
import com.dbeginc.dbweather.utils.utility.toast
import com.dbeginc.dbweather.viewmodels.news.ArticleModel
import java.util.*

/**
 * Created by darel on 10.10.17.
 *
 * Article Adapter
 */
class ArticleAdapter(data: List<ArticleModel>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    private var container: RecyclerView? = null
    private val articles: LinkedList<ArticleModel> = LinkedList(data.sorted())

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        container = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        return ArticleViewHolder(DataBindingUtil.inflate(inflater, R.layout.article_item, parent, false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder?, position: Int) {
        holder?.bindArticle(articles[position])
    }

    override fun getItemCount(): Int = articles.size

    fun update(newData: List<ArticleModel>) {
        synchronized(this) {
            /*
             * Calculating the difference between the the current data and the new data on background
             * Returning an typedArray and new instance so the data is immutable
             * (nobody will modify it during the calculation)
             */
            val result = DiffUtil.calculateDiff(ArticleDiffUtils(articles, newData.sorted()))

            articles.clear()

            articles.addAll(newData.sorted())

            result.dispatchUpdatesTo(this@ArticleAdapter)

//            container?.post {
//
//            }
        }
    }

    inner class ArticleViewHolder(private val binding: ArticleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindArticle(article: ArticleModel) {
            binding.article = article
            binding.articleLayout.setOnClickListener { goToArticleDetail() }
            binding.executePendingBindings()
        }

        private fun goToArticleDetail() {
//        Navigator.goToArticleDetail(binding)
            binding.articleLayout.toast("Opened : ${binding.article?.title}")
        }
    }

    fun getData(): List<ArticleModel> = articles

}