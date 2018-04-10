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

package com.dbeginc.dbweather.iptvlivedetail

import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityIpTvLiveBinding
import com.dbeginc.dbweather.utils.exoplayer.defaultBandwidthMeter
import com.dbeginc.dbweather.utils.exoplayer.getPreferedDataSourceFactoryForUrl
import com.dbeginc.dbweather.utils.utility.IPTV_LIVE_DATA
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweatherdomain.Logger
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Lazy
import javax.inject.Inject

class IpTvLiveActivity : BaseActivity() {
    @Inject
    lateinit var logger: Lazy<Logger>
    private lateinit var binding: ActivityIpTvLiveBinding
    private lateinit var exoPlayer: ExoPlayer

    private val playerListener = object : Player.DefaultEventListener() {
        override fun onPlayerError(playerError: ExoPlaybackException) {
            logger.get().logError(error = playerError)
            binding.iptvLiveDetailLayout.snack(message = playerError.unexpectedException.localizedMessage)
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ip_tv_live)

        binding.ipTvLive = if (savedState == null) intent.getParcelableExtra(IPTV_LIVE_DATA)
        else savedState.getParcelable(IPTV_LIVE_DATA)

        binding.executePendingBindings()

        setSupportActionBar(binding.iptvLiveDetailToolbar)

        binding.iptvLiveDetailToolbar.setNavigationOnClickListener {
            supportFinishAfterTransition()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(IPTV_LIVE_DATA, binding.ipTvLive)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > Build.VERSION_CODES.M)
            initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= Build.VERSION_CODES.M && binding.iptvLivePlayer.player == null)
            initializePlayer()
    }

    override fun onPause() {
        exoPlayer.removeListener(playerListener)

        exoPlayer.release()

        super.onPause()
    }

    override fun onStop() {
        exoPlayer.removeListener(playerListener)

        exoPlayer.release()

        super.onStop()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(applicationContext),
                DefaultTrackSelector(AdaptiveTrackSelection.Factory(defaultBandwidthMeter)),
                DefaultLoadControl()
        )

        exoPlayer.addListener(playerListener)

        binding.ipTvLive?.url?.let {
            buildMediaSource(streamToPlay = Uri.parse(it), dataSourceFactory = buildDataSourceFactory())

            binding.iptvLivePlayer.player = exoPlayer

            exoPlayer.playWhenReady = true
        }
    }

    private fun buildMediaSource(streamToPlay: Uri, dataSourceFactory: DataSource.Factory) {
        exoPlayer.prepare(
                ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(streamToPlay)
        )
    }

    private fun buildDataSourceFactory(): DataSource.Factory = DefaultDataSourceFactory(
            applicationContext,
            defaultBandwidthMeter,
            DefaultDataSourceFactory(
                    applicationContext,
                    defaultBandwidthMeter,
                    getPreferedDataSourceFactoryForUrl(url = binding.ipTvLive?.url
                            ?: "http://github.com/bitsydarel")
            )
    )

}
