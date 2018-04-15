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

package com.dbeginc.dbweather.articledetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipDescription
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.view.GravityCompat
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityArticleDetailBinding
import com.dbeginc.dbweather.utils.services.DBWeatherExternalContentManager
import com.dbeginc.dbweather.utils.utility.ARTICLE_KEY
import com.dbeginc.dbweather.utils.utility.getColorPrimaryDark
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweathernews.articledetail.ArticleDetailViewModel
import com.dbeginc.dbweathernews.viewmodels.ArticleModel

class ArticleDetailActivity : BaseActivity(), MVMPVView {
    private lateinit var binding: ActivityArticleDetailBinding
    private lateinit var customTabsIntent: CustomTabsIntent

    private val viewModel: ArticleDetailViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[ArticleDetailViewModel::class.java]
    }

    override val stateObserver: Observer<RequestState> = Observer {
        onStateChanged(state = it!!)
    }

    private val articleObserver: Observer<ArticleModel> = Observer {
        binding.article = it

        binding.executePendingBindings()
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val slideFromEndOfScreen = android.transition.Slide(GravityCompat.END)

            slideFromEndOfScreen.interpolator = LinearOutSlowInInterpolator()

            window.enterTransition = slideFromEndOfScreen
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_detail)

        binding.article = if (savedState == null) intent.getParcelableExtra(ARTICLE_KEY)
        else savedState.getParcelable(ARTICLE_KEY)

        customTabsIntent = CustomTabsIntent.Builder(DBWeatherExternalContentManager.retrieveSession())
                .setToolbarColor(getColorPrimaryDark())
                .enableUrlBarHiding()
                .setShowTitle(true)
                .setInstantAppsEnabled(false)
                .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right)
                .setCloseButtonIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_arrow))
                .build()

        DBWeatherExternalContentManager.prepareBrowserForUrl(binding.article!!.url)

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getArticleDetail().observe(this, articleObserver)

        setSupportActionBar(binding.articleDetailToolbar)

        setupView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARTICLE_KEY, binding.article)
    }

    override fun setupView() {
        binding.articleDetailToolbar.setNavigationOnClickListener { supportFinishAfterTransition() }

        binding.shareArticleFab.setOnClickListener { shareArticle() }

        binding.openFullArticle.setOnClickListener { openFullArticle() }

        binding.article?.let { validArticle ->
            viewModel.loadArticleDetail(
                    articleSourceId = validArticle.sourceId,
                    articleUrl = validArticle.url
            )
        }
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> return
            RequestState.COMPLETED -> return
            RequestState.ERROR -> requestFailed()
        }
    }

    private fun openFullArticle() {
        val isAvailable: Boolean = DBWeatherExternalContentManager.retrieveSession() != null
        val articleUrl = Uri.parse(binding.article?.url)

        if (isAvailable) customTabsIntent.launchUrl(this, articleUrl)
        else {
            startActivity(Intent.createChooser(
                    Intent(Intent.ACTION_VIEW, articleUrl),
                    getString(R.string.open_with)
            ))
        }
    }

    private fun shareArticle() {
        val shareIntent = Intent()

        val sharedWith = getString(R.string.share_with)

        val shareArticleText = getString(R.string.share_article_text)

        shareIntent.apply {
            action = Intent.ACTION_SEND
            type = ClipDescription.MIMETYPE_TEXT_PLAIN
            putExtra(Intent.EXTRA_TEXT, shareArticleText.plus(Uri.parse(binding.article?.url)))
        }

        startActivity(Intent.createChooser(shareIntent, sharedWith))
    }

    private fun requestFailed() {
        binding.articleDetailLayout.snack(message = "Could not load the artice detail")
    }
}
