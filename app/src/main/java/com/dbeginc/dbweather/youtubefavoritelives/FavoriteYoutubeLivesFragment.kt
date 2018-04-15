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

package com.dbeginc.dbweather.youtubefavoritelives

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.MainActivity
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentFavoriteYoutubeLivesLayoutBinding
import com.dbeginc.dbweather.utils.utility.goToYoutubeLiveDetailScreen
import com.dbeginc.dbweather.youtubelives.YoutubeLiveActionBridge
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweatherlives.favoriteyoutubelives.FavoriteYoutubeLivesViewModel
import com.dbeginc.dbweatherlives.manageyoutubelives.ManageYoutubeLivesViewModel
import com.dbeginc.dbweatherlives.viewmodels.YoutubeLiveModel

/**
 * Created by darel on 20.10.17.
 *
 * Favorite Lives Page Fragment
 */
class FavoriteYoutubeLivesFragment : BaseFragment(), MVMPVView, YoutubeLiveActionBridge, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var viewModel: FavoriteYoutubeLivesViewModel
    private lateinit var manageYoutubeLiveViewModel: ManageYoutubeLivesViewModel
    private lateinit var binding: FragmentFavoriteYoutubeLivesLayoutBinding
    private val favoriteYoutubeAdapter by lazy { FavoriteLiveAdapter(containerBridge = this) }
    override val stateObserver: Observer<RequestState> = Observer { onStateChanged(state = it!!) }
    private val favoritesObserver = Observer<List<YoutubeLiveModel>> {
        favoriteYoutubeAdapter.updateData(newData = it!!)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, factory)[FavoriteYoutubeLivesViewModel::class.java]

        manageYoutubeLiveViewModel = ViewModelProviders.of(this, factory)[ManageYoutubeLivesViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestState().observe(this, stateObserver)

        manageYoutubeLiveViewModel.getRequestState().observe(this, stateObserver)

        viewModel.getFavoriteYoutubeLives().observe(this, favoritesObserver)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_favorite_youtube_lives_layout,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.let { container ->
            container.setSupportActionBar(binding.favoriteYoutubeLivesToolbar)
            binding.favoriteYoutubeLivesToolbar.setNavigationOnClickListener { container.openNavigationDrawer() }
        }

        setupView()

        viewModel.loadFavoritesYoutubeLives()

    }

    override fun removeFromFavorite(youtubeLive: YoutubeLiveModel) {
        manageYoutubeLiveViewModel.removeFromFavorite(youtubeLive = youtubeLive)
    }

    override fun addToFavorite(youtubeLive: YoutubeLiveModel) {
        throw UnsupportedOperationException("Can't add to favorite in favorite screen")
    }

    override fun playYoutubeLive(youtubeLive: YoutubeLiveModel) {
        activity?.let {
            goToYoutubeLiveDetailScreen(
                    container = it,
                    youtubeLive = youtubeLive
            )
        }
    }

    /******************** Favorite Lives Tab View Part *********************/
    override fun setupView() {
        binding.favoriteYoutubeLivesContainer.setOnRefreshListener(this)

        binding.favoriteYoutubeLivesList.adapter = favoriteYoutubeAdapter

        binding.favoriteYoutubeLivesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.favoriteYoutubeLivesContainer.isRefreshing = true
            RequestState.COMPLETED -> binding.favoriteYoutubeLivesContainer.isRefreshing = false
            RequestState.ERROR -> onYoutubeLivesRequestFailed()
        }
    }


    override fun onRefresh() = viewModel.loadFavoritesYoutubeLives()

    private fun onYoutubeLivesRequestFailed() {
        binding.favoriteYoutubeLivesContainer.isRefreshing = false

        Snackbar.make(binding.favoriteYoutubeLivesLayout, R.string.lives_error_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry, { viewModel.loadFavoritesYoutubeLives() })
    }

}