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

package com.dbeginc.dbweather.base

import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.AsyncListDiffer
import android.support.v7.util.AdapterListUpdateCallback
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView

abstract class BaseAdapter<T, V : RecyclerView.ViewHolder>(differenceCalculator: DiffUtil.ItemCallback<T>) : RecyclerView.Adapter<V>() {
    private val adapterHelper: AsyncListDiffer<T> = AsyncListDiffer(
            AdapterListUpdateCallback(this),
            AsyncDifferConfig.Builder<T>(differenceCalculator).build()
    )

    override fun getItemCount(): Int = adapterHelper.currentList.size

    fun getItemForPosition(position: Int): T = adapterHelper.currentList[position]

    fun updateData(newData: List<T>) = adapterHelper.submitList(newData)

    fun cancelItemDeletion(position: Int) = notifyItemChanged(position)

    fun remoteItemAt(position: Int) = adapterHelper.submitList(adapterHelper.currentList.drop(position))

    fun getData(): List<T> = adapterHelper.currentList

}