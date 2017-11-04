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

package com.dbeginc.dbweather.news.lives.livedetail.view

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dbeginc.dbweather.BuildConfig
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.databinding.ActivityLiveDetailBinding
import com.dbeginc.dbweather.news.lives.livedetail.LiveDetailContract
import com.dbeginc.dbweather.utils.glide.BlurBackgroundTarget
import com.dbeginc.dbweather.utils.glide.BlurTransformation
import com.dbeginc.dbweather.utils.holder.ConstantHolder.LIVES_DATA
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweather.viewmodels.news.LiveModel
import com.dbeginc.dbweatherdata.ConstantHolder.YOUTUBE_THUMBNAIL_URL
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import javax.inject.Inject

/**
 * Created by darel on 21.10.17.
 *
 * Live Detail Activity
 */
class LiveDetailActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener, LiveDetailContract.LiveDetailView {
    @Inject lateinit var presenter: LiveDetailContract.LiveDetailPresenter
    private lateinit var binding: ActivityLiveDetailBinding
    private lateinit var live: LiveModel
    private var player: YouTubePlayer? = null

    companion object {
        private const val RECOVERY_DIALOG_REQUEST = 7125
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectLiveDetailDep(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_detail)

        live = if (savedState == null) intent.getParcelableExtra(LIVES_DATA) else savedState.getParcelable(LIVES_DATA)

        binding.liveStream.initialize(BuildConfig.YOUTUBE_API_KEY, this)
        setupToolbar()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(LIVES_DATA, live)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
    }

    override fun onInitializationSuccess(youtubeProvider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        player = youTubePlayer

        if(!wasRestored) {
            player?.cueVideo(live.url)
            player?.setShowFullscreenButton(true)
            player?.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT)
            player?.fullscreenControlFlags = YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION
            player?.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION)
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

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.onBackClicked()
    }

    /************************* Live Detail Activity *************************/
    override fun setupView() {
        binding.liveDetailLayout.setBlurBackground(live.url)

        presenter.checkIfLiveFavorite()
        presenter.loadLive()
    }

    override fun cleanState() {
        presenter.unBind()
    }

    override fun displayYoutube(live: LiveModel) {
        this.live = live
        player?.loadVideo(live.url)
    }

    override fun shareLive() {
    }

    override fun showUpdatingStatus() = binding.liveUpdateStatus.show()

    override fun hideUpdatingStatus() = binding.liveUpdateStatus.remove()

    override fun getLiveName(): String = live.name

    override fun liveNotFavorite() {
        binding.detailToolbar.menu.findItem(R.id.liveDetailBookmark).setIcon(R.drawable.ic_not_bookmark_white)
    }

    override fun liveFavorite() {
        binding.detailToolbar.menu.findItem(R.id.liveDetailBookmark).setIcon(R.drawable.ic_bookmarked)
    }

    override fun goBackToLiveList() {
        player?.release()
        finish()
    }

    override fun showError(localizedMessage: String) {
        binding.liveDetailLayout.snack(localizedMessage)
    }

    private fun setupToolbar() {
        binding.detailToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.detailToolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        binding.detailToolbar.inflateMenu(R.menu.live_detail_menu)
        binding.detailToolbar.title = live.name

        binding.detailToolbar.setOnMenuItemClickListener { item ->
            when(item?.itemId) {
                R.id.liveDetaiLShare -> presenter.onShare()
                R.id.liveDetailBookmark -> presenter.onBookmark()
            }

            return@setOnMenuItemClickListener true
        }
    }

    private fun ViewGroup.setBlurBackground(url: String?) {
        if (url != null && url.isNotEmpty()) {
            Glide.with(context)
                    .load(YOUTUBE_THUMBNAIL_URL.format(url))
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(context)))
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .into(BlurBackgroundTarget(this))
        }
    }
}