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

package com.dbeginc.dbweather.iptvplaylistdetail


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentIpTvPlaylistDetailBinding
import com.dbeginc.dbweather.utils.utility.IPTV_PLAYLIST_KEY
import com.dbeginc.dbweather.utils.utility.goToIpTvLiveScreen
import com.dbeginc.dbweather.utils.utility.goToIpTvPlaylistsScreen
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweatherlives.iptvplaylistdetail.IpTvPlaylistDetailViewModel
import com.dbeginc.dbweatherlives.viewmodels.IpTvLiveModel

/**
 * IpTvPlaylistDetailFragment [BaseFragment] subclass.
 * Display list channels from a specific playlist
 */
class IpTvPlaylistDetailFragment : BaseFragment(), MVMPVView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var binding: FragmentIpTvPlaylistDetailBinding
    private lateinit var playlistId: String

    private val ipTvLivesAdapter: IpTvLivesAdapter by lazy {
        return@lazy IpTvLivesAdapter(onItemClick = this::onIpTvLiveSelected)
    }

    override val stateObserver: Observer<RequestState> = Observer { state ->
        state?.let { onStateChanged(state = it) }
    }

    private val iptvLivesObserver: Observer<List<IpTvLiveModel>> = Observer { iptvLives ->
        iptvLives?.let { ipTvLivesAdapter.updateData(newData = it) }
    }

    private val viewModel: IpTvPlaylistDetailViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[IpTvPlaylistDetailViewModel::class.java]
    }

    companion object {
        @JvmStatic
        fun newInstance(playlistId: String): IpTvPlaylistDetailFragment {
            val screen = IpTvPlaylistDetailFragment()

            screen.arguments = Bundle().apply {
                putString(IPTV_PLAYLIST_KEY, playlistId)
            }

            return screen
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        playlistId = if (savedState == null) arguments!!.getString(IPTV_PLAYLIST_KEY)
        else savedState.getString(IPTV_PLAYLIST_KEY)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(IPTV_PLAYLIST_KEY, playlistId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getIpTvLives().observe(this, iptvLivesObserver)

        onRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.iptv_playlist_detail_menu, menu)

        val searchView = menu.findItem(R.id.action_find_iptv)?.actionView as? android.support.v7.widget.SearchView

        searchView?.let {
            it.queryHint = getString(R.string.search_iptv_live)

            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = true

                override fun onQueryTextChange(query: String?): Boolean {
                    if (query != null && query.isNotBlank())
                        viewModel.findIpTvLive(playlistId = playlistId, possibleLiveName = query)
                    else viewModel.loadIpTvLives(playlistId = playlistId)
                    return true
                }

            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_ip_tv_playlist_detail,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.iptvPlaylistDetailToolbar)

        binding.iptvPlaylistDetailToolbar.setNavigationOnClickListener {
            activity?.let {
                goToIpTvPlaylistsScreen(container = it, emplacementId = R.id.main_content)
            }
        }

        setupView()
    }

    override fun setupView() {
        binding.playlistId = playlistId

        binding.iptvLives.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.iptvLives.adapter = ipTvLivesAdapter

        binding.iptvLivesContainer.setOnRefreshListener(this)
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.iptvLivesContainer.isRefreshing = true
            RequestState.COMPLETED -> binding.iptvLivesContainer.isRefreshing = false
            RequestState.ERROR -> onRequestFailed()
        }
    }

    override fun onRefresh() {
        viewModel.loadIpTvLives(playlistId = playlistId)
    }

    private fun onRequestFailed() {
        binding.iptvLivesContainer.isRefreshing = false

        Snackbar.make(binding.iptvPlaylistDetail, R.string.could_not_load_channels, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) { viewModel.loadIpTvLives(playlistId = playlistId) }
                .show()

    }

    private fun onIpTvLiveSelected(selectedIptvLive: IpTvLiveModel) {
        activity?.let {
            goToIpTvLiveScreen(
                    container = it,
                    iptvLive = selectedIptvLive
            )
        }
    }

}
