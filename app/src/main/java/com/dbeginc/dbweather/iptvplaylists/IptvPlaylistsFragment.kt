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

package com.dbeginc.dbweather.iptvplaylists


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.SearchView
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentIptvPlaylistsBinding
import com.dbeginc.dbweather.utils.utility.goToIpTvPlaylistScreen
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweatherlives.iptvplaylists.IpTvPlayListsViewModel
import com.dbeginc.dbweatherlives.viewmodels.IpTvPlayListModel

/**
 * Iptv Playlists [BaseFragment] subclass.
 */
class IptvPlaylistsFragment : BaseFragment(), MVMPVView {
    private lateinit var binding: FragmentIptvPlaylistsBinding
    private lateinit var viewModel: IpTvPlayListsViewModel

    private val playListsAdapter by lazy {
        IpTvPlayListAdapter(onItemClick = this::onPlayListSelected)
    }

    private val ipTvPlayListsObserver = Observer<List<IpTvPlayListModel>> {
        playListsAdapter.updateData(newData = it!!)
    }

    override val stateObserver: Observer<RequestState> = Observer {
        onStateChanged(state = it!!)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.iptv_playlists_menu, menu)

        val searchView = menu.findItem(R.id.action_search)?.actionView as? SearchView

        searchView?.queryHint = getString(R.string.search_playlist)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotBlank()) viewModel.findPlayList(newText)
                return true
            }

        })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(this, factory)[IpTvPlayListsViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getIpTvPlayLists().observe(this, ipTvPlayListsObserver)
        viewModel.getRequestState().observe(this, stateObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_iptv_playlists,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedState: Bundle?) {
        super.onViewCreated(view, savedState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.iptvPlaylistsToolbar)

        setupView()

        viewModel.loadIpTvPlayLists()
    }

    override fun setupView() {
        binding.iptvPlaylistsSwipeRefresh.setOnRefreshListener { viewModel.loadIpTvPlayLists() }

        binding.iptvPlaylists.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.iptvPlaylists.adapter = playListsAdapter

        binding.iptvPlaylists.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.iptvPlaylistsSwipeRefresh.isRefreshing = true
            RequestState.COMPLETED -> binding.iptvPlaylistsSwipeRefresh.isRefreshing = false
            RequestState.ERROR -> onRequestFailed()
        }
    }

    private fun onRequestFailed() {
        binding.iptvPlaylistsSwipeRefresh.isRefreshing = false

        Snackbar.make(binding.iptvPlaylistsLayout, R.string.could_not_load_iptv_playlists, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) { viewModel.loadIpTvPlayLists() }
                .show()

    }

    private fun onPlayListSelected(selectedPlaylist: IpTvPlayListModel) {
        activity?.let {
            goToIpTvPlaylistScreen(
                    container = it,
                    emplacementId = R.id.main_content,
                    playlist = selectedPlaylist
            )
        }
    }

}
