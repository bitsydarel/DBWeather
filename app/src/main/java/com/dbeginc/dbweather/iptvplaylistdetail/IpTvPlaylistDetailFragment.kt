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
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentIpTvPlaylistDetailBinding
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweatherlives.iptvplaylistdetail.IpTvPlaylistDetailViewModel
import com.dbeginc.dbweatherlives.viewmodels.IpTvLiveModel

/**
 * IpTvPlaylistDetailFragment [BaseFragment] subclass.
 * Display list channels from a specific playlist
 */
class IpTvPlaylistDetailFragment : BaseFragment(), MVMPVView {
    private lateinit var binding: FragmentIpTvPlaylistDetailBinding
    private lateinit var playlistId: String

    private val ipTvLivesAdapter by lazy {
        IpTvLivesAdapter(onItemClick = this::onIpTvLiveSelected)
    }

    override val stateObserver: Observer<RequestState> = Observer {
        onStateChanged(state = it!!)
    }

    private val iptvLivesObserver: Observer<List<IpTvLiveModel>> = Observer {
        ipTvLivesAdapter.updateData(newData = it!!)
    }

    private val viewModel by lazy(mode = LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, factory.get())[IpTvPlaylistDetailViewModel::class.java]
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

        playlistId = if (savedState == null) arguments!!.getString(IPTV_PLAYLIST_KEY) else savedState.getString(IPTV_PLAYLIST_KEY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(IPTV_PLAYLIST_KEY, playlistId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getIpTvLives().observe(this, iptvLivesObserver)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

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

        viewModel.loadIpTvLives(playlistId = playlistId)
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> binding.iptvLiveLoading.show()
            RequestState.COMPLETED -> binding.iptvLiveLoading.hide()
            RequestState.ERROR -> onRequestFailed()
        }
    }

    private fun onRequestFailed() {
        binding.iptvLiveLoading.hide()

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
