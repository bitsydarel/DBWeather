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

package com.dbeginc.dbweather.news.lives.livedetail

import android.content.ClipDescription
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import com.dbeginc.dbweather.BuildConfig
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.LiveDetailFeatureBinding
import com.dbeginc.dbweather.utils.holder.ConstantHolder.LIVES_DATA
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailPresenter
import com.dbeginc.dbweathernews.livedetail.contract.LiveDetailView
import com.dbeginc.dbweathernews.viewmodels.LiveModel
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import javax.inject.Inject

/**
 * Created by darel on 21.10.17.
 *
 * Live Detail Activity
 */
class LiveDetailActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener, LiveDetailView {
    @Inject lateinit var presenter: LiveDetailPresenter
    private lateinit var binding: LiveDetailFeatureBinding
    private val sharedWith by lazy { getString(R.string.share_with) }
    private val shareLiveText by lazy { getString(R.string.share_live_text) }
    private var player: YouTubePlayer? = null

    companion object {
        private const val RECOVERY_DIALOG_REQUEST = 7125
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectLiveDetailDep(this)

        binding = DataBindingUtil.setContentView(this, R.layout.live_detail_feature)

        binding.live = if (savedState == null) intent.getParcelableExtra(LIVES_DATA) else savedState.getParcelable(LIVES_DATA)

        binding.liveStream.initialize(BuildConfig.YOUTUBE_API_KEY, this)

        presenter.bind(this)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(LIVES_DATA, binding.live)
    }

    override fun onDestroy() {
        super.onDestroy()

        cleanState()
    }

    override fun onInitializationSuccess(youtubeProvider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        player = youTubePlayer

        if(!wasRestored) {
            player?.apply {
                cueVideo(binding.live?.url)
                setShowFullscreenButton(true)
                setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
                fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
                addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION)
            }
        }
    }

    override fun onInitializationFailure(youtubeProvider: YouTubePlayer.Provider?, error: YouTubeInitializationResult) {
        if (error.isUserRecoverableError) error.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show()
        else binding.liveDetailLayout.snack(error.name)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            binding.liveStream.initialize(BuildConfig.YOUTUBE_API_KEY, this)
        }
    }

    /************************* Live Detail Activity *************************/
    override fun setupView() {
        binding.detailToolbar.apply {
            setNavigationOnClickListener { presenter.onExitAction() }

            if (menu.size() < 2) inflateMenu(R.menu.live_detail_menu)

            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.liveDetaiLShare -> presenter.onShare()
                    R.id.liveDetailBookmark -> presenter.onBookmark()
                }
                return@setOnMenuItemClickListener true
            }
        }

        presenter.checkIfLiveFavorite()

        presenter.loadLive()
    }

    override fun cleanState() = presenter.unBind()

    override fun displayLive(live: LiveModel) {
        binding.live = live
        player?.loadVideo(live.url)
    }

    override fun shareLive() {
        val shareIntent = Intent()

        shareIntent.apply {
            action = Intent.ACTION_SEND
            type = ClipDescription.MIMETYPE_TEXT_PLAIN
            putExtra(Intent.EXTRA_TEXT, shareLiveText.format(Uri.parse(binding.live?.url)))
        }

        startActivity(Intent.createChooser(shareIntent, sharedWith))
    }

    override fun showLoading() = binding.liveUpdateStatus.show()

    override fun hideLoading() = binding.liveUpdateStatus.remove()

    override fun getLiveName(): String = binding.live!!.name

    override fun showLiveIsNotFavorite() {
        binding.detailToolbar.menu?.findItem(R.id.liveDetailBookmark)?.setIcon(R.drawable.ic_not_bookmark_white)
    }

    override fun showLiveIsFavorite() {
        binding.detailToolbar.menu?.findItem(R.id.liveDetailBookmark)?.setIcon(R.drawable.ic_bookmarked)
    }

    override fun close() {
        player?.release()
        finish()
    }

    override fun showError(error: String) = binding.liveDetailLayout.snack(error)

}