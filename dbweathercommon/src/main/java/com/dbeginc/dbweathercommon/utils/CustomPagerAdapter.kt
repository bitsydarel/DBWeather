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

package com.dbeginc.dbweathercommon.utils

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async


/**
 * Created by darel on 15.10.17.
 *
 * Implementation of {@link android.support.v4.view.PagerAdapter} that
 * uses a {@link Fragment} to manage each page. This class also handles
 * saving and restoring of fragment's state.
 *
 * <p>Subclasses only need to implement {@link #getItem(int)}
 * and {@link #getCount()} to have a working adapter.
 *
 */
abstract class CustomPagerAdapter(private val fragmentManager: FragmentManager) : PagerAdapter() {
    private var currentTransaction: FragmentTransaction? = null
    private var currentPrimaryFragment: Fragment? = null

    private val pagerFragments: MutableMap<String, Fragment> = mutableMapOf()
    private val pagerFragmentStates: MutableMap<String, Fragment.SavedState> = mutableMapOf()

    companion object {
        private const val KEYS = "keys"
        private const val STATES = "states"
        private const val STATES_KEY = "-state"
    }

    fun <DATA: UpdatableModel>update(data: List<DATA>) {
        val updatableFragments = fragmentManager.fragments.filterIsInstance(UpdatableContainer::class.java)

        data.filter { model -> updatableFragments.indexOfFirst { updatable -> updatable.getUpdatableId() == model.getId() } != -1 }
                .mapNotNull { model -> updatableFragments.find { updatable -> updatable.getUpdatableId() == model.getId() }?.to(model) }
                .forEach { (updatable, model) -> updatable.update(model) }
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment

    abstract fun getUniqueIdentifier(position: Int) : String

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // get fragment unique identifier for the given position
        val uniqueIdentifier = getUniqueIdentifier(position)

        /**
         * Checking if we already have a fragment
         * at the following position we return it
         */
        if (pagerFragments.contains(uniqueIdentifier)) return pagerFragments[uniqueIdentifier]!!

        if (currentTransaction == null) currentTransaction = fragmentManager.beginTransaction()

        // we get a new fragment
        val fragment = getItem(position)

        fragment.setMenuVisibility(false)
        fragment.userVisibleHint = false

        // checked if we saved the state of fragment at the position
        if (pagerFragmentStates.contains(uniqueIdentifier)) fragment.setInitialSavedState(pagerFragmentStates[uniqueIdentifier])

        pagerFragments.put(uniqueIdentifier, fragment)
        currentTransaction?.add(container.id, fragment, uniqueIdentifier)

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val fragment = `object` as Fragment
        val uniqueId = getUniqueIdentifier(position)

        if (currentTransaction == null) currentTransaction = fragmentManager.beginTransaction()

        // If Pager adapter has been recently the fragment
        // inconsistency will be
        try {
            pagerFragmentStates.put(uniqueId, fragmentManager.saveFragmentInstanceState(fragment))

        } catch (fragmentNotAdded: IllegalStateException) {
            Toast.makeText(container.context, fragmentNotAdded.localizedMessage, Toast.LENGTH_LONG).show()
        }

        // remove the instance of the fragment
        pagerFragments.remove(uniqueId)

        currentTransaction?.remove(fragment)
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any?) {
        val fragment = `object` as Fragment?

        if (fragment !== currentPrimaryFragment) {
            currentPrimaryFragment?.setMenuVisibility(false)
            currentPrimaryFragment?.userVisibleHint = false

            fragment?.setMenuVisibility(true)
            fragment?.userVisibleHint = true

            currentPrimaryFragment = fragment

        }
    }

    override fun finishUpdate(container: ViewGroup) {
        if (currentTransaction != null) {
            currentTransaction?.commitAllowingStateLoss()
            currentTransaction = null
            fragmentManager.executePendingTransactions()
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = (`object` as? Fragment)?.view === view

    override fun saveState(): Parcelable? {
        var states: Bundle? = null

        // Getting fragments currently in fragmentManager
        val addedFragments = fragmentManager.fragments

        // Getting all fragment that
        // are not necessary to be saved
        val fragmentsToSkip =  pagerFragments.filterNot { (_, fragment) -> addedFragments.contains(fragment) }.keys

        fragmentsToSkip.forEach { key ->
            pagerFragments.remove(key)
            pagerFragmentStates.remove(key)
        }

        if (pagerFragmentStates.isNotEmpty()) {

            states = Bundle()

            val fragmentSavedStates = arrayOfNulls<Fragment.SavedState>(pagerFragmentStates.size)
            val fragmentSavedStatesKeys = arrayOfNulls<String>(pagerFragmentStates.size)

            pagerFragmentStates.entries.forEachIndexed { index, entry ->
                fragmentSavedStatesKeys[index] = entry.key
                fragmentSavedStates[index] = entry.value
            }

            states.putStringArray(KEYS, fragmentSavedStatesKeys)
            states.putParcelableArray(STATES, fragmentSavedStates)
        }

        pagerFragments.entries.forEach { entry ->
            if (states == null) states = Bundle()
            fragmentManager.putFragment(states, entry.key.plus(STATES_KEY), entry.value)
        }

        return states
    }

    override fun restoreState(savedStates: Parcelable?, loader: ClassLoader?) {
        if (savedStates != null) {
            val states = savedStates as Bundle
            states.classLoader = loader

            val fragmentSavedStatesKeys = states.getStringArray(KEYS)
            val fragmentSavedStates = states.getParcelableArray(STATES)

            pagerFragments.clear()
            pagerFragmentStates.clear()

            fragmentSavedStatesKeys?.zip(fragmentSavedStates)?.forEach {
                (key, state) -> pagerFragmentStates.put(key, state as Fragment.SavedState)
            }

            states.keySet()
                    .filter { key -> key.endsWith(STATES_KEY) }
                    .map { key -> key.substring(key.indexOf(STATES_KEY)) }
                    .forEach { key ->
                        val fragment = fragmentManager.getFragment(states, key)

                        if (fragment != null) {
                            pagerFragments.put(key, fragment)
                            fragment.setMenuVisibility(false)
                        }
                    }

        }
    }
}