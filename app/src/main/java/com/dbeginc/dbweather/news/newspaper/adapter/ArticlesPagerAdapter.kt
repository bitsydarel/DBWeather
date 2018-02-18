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
import android.support.v4.util.ArrayMap
import android.support.v4.view.PagerAdapter
import com.dbeginc.dbweather.news.newspaper.adapter.page.ArticlesPageFragment
import com.dbeginc.dbweathercommon.utils.SmartFragmentStatePagerAdapter
import com.dbeginc.dbweathercommon.utils.UpdatableContainer
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel

/**
 * Created by darel on 12.02.18.
 *
 *
 */
class ArticlesPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter<ArticlesPageFragment>(fragmentManager) {
    private val newsPapers: ArrayMap<Int, NewsPaperModel> = ArrayMap()
    private var temporaryIds: ArrayMap<Int, NewsPaperModel>? = null

    override fun getItem(position: Int): Fragment {
        val newsPaper = newsPapers.getValue(position)

        return ArticlesPageFragment.newInstance(newsPaper.name, newsPaper.children)
    }

    override fun getItemPosition(`object`: Any?): Int {
        val updatableFragment = `object` as UpdatableContainer

        // find the page index with the following news source name
        val pagePosition = newsPapers.findKey(updatableFragment.getUpdatableId())

        /**
         * if the page position is null that mean page was not present in the previous list
         * so we need to create a new fragment for it
         * if page position from the new list is the same as the one on the old list
         * no new to create new one
         * if none of the condition are met, we need to create new fragment at that position
         */
        val shouldBeRecreated = when {
            pagePosition == null -> PagerAdapter.POSITION_NONE
            newsPapers[pagePosition] == temporaryIds?.get(pagePosition) -> PagerAdapter.POSITION_UNCHANGED
            else -> PagerAdapter.POSITION_NONE
        }

        if (shouldBeRecreated == PagerAdapter.POSITION_UNCHANGED) updatableFragment.update(newsPapers[pagePosition])

        return shouldBeRecreated
    }

    override fun getPageTitle(position: Int): CharSequence = newsPapers.getValue(position).name

    override fun getCount(): Int = newsPapers.size

    @Synchronized
    fun refresh(newData: List<NewsPaperModel>) {
        // if dataset is empty and the new dataset is the same as the current one
        // we just update the dataset
        if (newsPapers.isNotEmpty() and (newsPapers.size == newData.size)) {
            newData.sortedBy { (name) -> name }
                    .forEachIndexed { index, newsPaperModel ->
                        newsPapers[index] = newsPaperModel
                    }

            // we update visible fragments with new data
            newsPapers.forEach { (position, data) -> getRegisteredFragment(position)?.update(data) }

        } else {
            // create copy of current dataset
            temporaryIds = ArrayMap(newsPapers)

            // update the dataset with new one
            newData.sortedBy { (name) -> name }
                    .forEachIndexed { index, newsPaperModel ->
                        newsPapers[index] = newsPaperModel
                    }

            // notify viewPagers that dataset size changed
            notifyDataSetChanged()
        }
    }

    private fun Map<Int, NewsPaperModel>.findKey(arg: String): Int? {
        val founded = filter { it.value.name == arg }

        return if (founded.isEmpty()) null
        else founded.toList()[0].first
    }

}