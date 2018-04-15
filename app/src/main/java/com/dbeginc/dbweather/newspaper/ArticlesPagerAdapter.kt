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

package com.dbeginc.dbweather.newspaper

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.util.ArrayMap
import android.support.v4.view.PagerAdapter
import com.dbeginc.dbweather.articles.ArticlesFragment
import com.dbeginc.dbweathercommon.utils.SmartFragmentStatePagerAdapter
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import java.lang.ref.WeakReference

/**
 * Created by darel on 12.02.18.
 *
 *
 */
class ArticlesPagerAdapter(fragmentManager: FragmentManager) : SmartFragmentStatePagerAdapter<ArticlesFragment>(fragmentManager) {
    private val newsPapers: ArrayMap<Int, NewsPaperModel> = ArrayMap()
    private var temporaryIds: WeakReference<ArrayMap<Int, NewsPaperModel>?> = WeakReference(null)

    override fun getItem(position: Int): Fragment {
        val newsPaper = newsPapers.getValue(position)

        return ArticlesFragment.newInstance(
                sourceId = newsPaper.id,
                sourceName = newsPaper.name
        )
    }

    override fun getItemPosition(`object`: Any): Int {
        // find the page index with the following news source channelName
        val newsPaperId = (`object` as? ArticlesFragment)?.newsPaperId

        val pagePosition = if (newsPaperId == null) PagerAdapter.POSITION_NONE else newsPapers.findKey(newsPaperId)

        /**
         * if the page position is null that mean page was not present in the previous list
         * so we need to create a new fragment for it
         * if page position from the new list is the same as the one on the old list
         * no new to create new one
         * if none of the condition are met, we need to create new fragment at that position
         */
        return when {
            pagePosition == null -> PagerAdapter.POSITION_NONE
            newsPapers[pagePosition] == temporaryIds.get()?.get(pagePosition) -> PagerAdapter.POSITION_UNCHANGED
            else -> PagerAdapter.POSITION_NONE
        }
    }

    override fun getPageTitle(position: Int): CharSequence = newsPapers.getValue(position).name

    override fun getCount(): Int = newsPapers.size

    fun refresh(newData: List<NewsPaperModel>) {
        // create copy of current dataset
        temporaryIds = WeakReference(ArrayMap(newsPapers))

        // update the dataset with new one
        newData.forEachIndexed { index, newsPaperModel ->
            newsPapers[index] = newsPaperModel
        }

        // notify viewPagers that dataset size changed
        notifyDataSetChanged()
    }

    private fun Map<Int, NewsPaperModel>.findKey(arg: String): Int? {
        val founded = asSequence().filter { it.value.name == arg }

        return if (founded.count() == 0) null
        else founded.first().key
    }

}