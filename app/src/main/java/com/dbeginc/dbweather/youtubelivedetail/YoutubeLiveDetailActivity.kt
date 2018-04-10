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

package com.dbeginc.dbweather.youtubelivedetail

import android.content.ClipDescription
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import com.dbeginc.dbweather.BuildConfig
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ActivityYoutubeLiveDetailBinding
import com.dbeginc.dbweather.utils.utility.YOUTUBE_LIVE_KEY
import com.dbeginc.dbweather.utils.utility.YOUTUBE_RECOVERY_DIALOG_REQUEST
import com.dbeginc.dbweather.utils.utility.snack
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer

class YoutubeLiveDetailActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    private lateinit var binding: ActivityYoutubeLiveDetailBinding
    private var player: YouTubePlayer? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_youtube_live_detail)

        binding.youtubeLive = if (savedState == null) intent.getParcelableExtra(YOUTUBE_LIVE_KEY) else savedState.getParcelable(YOUTUBE_LIVE_KEY)

        binding.youtubePlayer.initialize(BuildConfig.YOUTUBE_API_KEY, this)

        binding.youtubeLiveDetailToolbar.apply {
            setNavigationOnClickListener { finish() }

            if (menu.size() < 1) inflateMenu(R.menu.youtube_live_detail_menu)

            setOnMenuItemClickListener {
                val shareIntent = Intent()

                shareIntent.apply {
                    action = Intent.ACTION_SEND
                    type = ClipDescription.MIMETYPE_TEXT_PLAIN
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.share_live_text).format(Uri.parse(binding.youtubeLive?.url)))
                }

                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_with)))
                return@setOnMenuItemClickListener true
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(YOUTUBE_LIVE_KEY, binding.youtubeLive)
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == YOUTUBE_RECOVERY_DIALOG_REQUEST) {
            binding.youtubePlayer.initialize(BuildConfig.YOUTUBE_API_KEY, this)
        }
    }

    override fun onInitializationSuccess(p0: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?, wasRestored: Boolean) {
        player = youTubePlayer

        if (wasRestored.not()) {
            player?.apply {
                cueVideo(binding.youtubeLive?.url)
                setShowFullscreenButton(true)
                setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION)
            }
        }
    }

    override fun onInitializationFailure(p0: YouTubePlayer.Provider?, error: YouTubeInitializationResult) {
        if (error.isUserRecoverableError) error.getErrorDialog(this, YOUTUBE_RECOVERY_DIALOG_REQUEST).show()
        else binding.youtubeLiveDetailLayout.snack(error.name)
    }

}
