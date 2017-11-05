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

package com.dbeginc.dbweather.news.newspaper.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.dbeginc.dbweather.news.CustomPagerAdapter
import com.dbeginc.dbweather.news.UpdatableContainer
import com.dbeginc.dbweather.news.newspaper.adapter.page.view.ArticlesPageFragment
import com.dbeginc.dbweather.viewmodels.news.NewsPaperModel
import java.util.*

/*
 * Create by Darel Bitsy
 *
 * Articles Adapter
 */
class ArticlesPagerAdapter(data: List<NewsPaperModel>, fragmentManager: FragmentManager) : CustomPagerAdapter(fragmentManager) {
    private val newsPapers: LinkedList<NewsPaperModel> = LinkedList(data)

    override fun getItem(position: Int): Fragment {
        val newsPaper = newsPapers[position]
        return ArticlesPageFragment.newInstance(newsPaper.name, newsPaper.children)
    }

    override fun getUniqueIdentifier(position: Int): String = newsPapers[position].name

    override fun getCount(): Int = newsPapers.size

    override fun getPageTitle(position: Int): CharSequence = newsPapers[position].name

    override fun getItemPosition(`object`: Any?): Int {
        val updatableFragment = `object` as? UpdatableContainer

        val founded = newsPapers.firstOrNull { newsPaper -> updatableFragment?.getUpdatableId() == newsPaper.getId() } != null

        return if (founded) POSITION_UNCHANGED else POSITION_NONE
    }

    fun getData(): List<NewsPaperModel> = newsPapers

    fun refresh(newData: List<NewsPaperModel>) {
        synchronized(this) {
            if (newsPapers.isNotEmpty().and(newsPapers.size == newData.size)) {
                fillMe(newData)
                update(newData)

            } else {
                fillMe(newData)
                notifyDataSetChanged()
            }
        }
    }

    private fun fillMe(newData: List<NewsPaperModel>) {
        newsPapers.clear()
        newsPapers.addAll(newData.sortedBy { newsPaper -> newsPaper.name })
    }
}
