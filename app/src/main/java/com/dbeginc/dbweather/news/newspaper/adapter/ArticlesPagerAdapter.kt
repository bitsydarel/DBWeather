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
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dbeginc.dbweather.news.newspaper.adapter.page.ArticlesPageFragment
import com.dbeginc.dbweathercommon.utils.CustomPagerAdapter
import com.dbeginc.dbweathercommon.utils.UpdatableContainer
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel
import java.util.*

/*
 * Create by Darel Bitsy
 *
 * Articles Adapter
 */
class ArticlesPagerAdapter(data: List<NewsPaperModel>, private val fragmentManager: FragmentManager) : CustomPagerAdapter(fragmentManager) {
    private var newsPapers: LinkedList<NewsPaperModel> = LinkedList(data)
    private var temporaryIds: List<String>? = null

    override fun getItem(position: Int): Fragment {
        val newsPaper = newsPapers[position]

        return ArticlesPageFragment.newInstance(newsPaper.name, newsPaper.children)
    }

    override fun startUpdate(container: ViewGroup?) {
        fragmentManager.fragments
                .filterIsInstance<UpdatableContainer>()
                .forEach {
                    (it as Fragment).onCreateView(
                            LayoutInflater.from(it.context),
                            container,
                            null
                    )
                }
    }

    override fun finishUpdate(container: ViewGroup) {
        super.finishUpdate(container)
        // Cleanup  the temporary list of ids
        temporaryIds = null
    }

    override fun getUniqueIdentifier(position: Int): String = temporaryIds?.getOrNull(position) ?: newsPapers[position].name

    override fun getCount(): Int = newsPapers.size

    override fun getPageTitle(position: Int): CharSequence = newsPapers[position].name

    override fun getItemPosition(`object`: Any): Int {
        val updatableFragment = `object` as UpdatableContainer

        // check if newsPaper is not in dataset
        // if is not we need to create the page so we return [POSITION_NONE]
        if (newsPapers.firstOrNull { (name) -> name == updatableFragment.getUpdatableId() } == null) return POSITION_NONE

        // check if we already have an instance of the fragment
        val founded = fragmentManager.fragments
                .filterIsInstance(UpdatableContainer::class.java)
                .firstOrNull { page -> page.getUpdatableId() == updatableFragment.getUpdatableId() } != null

        return if (founded) POSITION_UNCHANGED else POSITION_NONE
    }

    fun getData(): List<NewsPaperModel> = newsPapers

    @Synchronized
    fun refresh(newData: List<NewsPaperModel>) {

        if (newsPapers.isNotEmpty().and(newsPapers.size == newData.size)) {
            newsPapers = LinkedList(newData)

            update(newData)

        } else {
            /**
             * make an copy of the old news papers unique ids
             * it's required to handle auto update of page
             * when new newspapers are added dynamically
             */
            newsPapers.map { (name) -> name }
                    .also { ids -> temporaryIds = ids }

            newsPapers = LinkedList(newData)

            notifyDataSetChanged()
        }
    }
}
