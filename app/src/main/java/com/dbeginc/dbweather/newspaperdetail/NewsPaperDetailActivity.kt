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

package com.dbeginc.dbweather.newspaperdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.ActivityNewsPaperDetailBinding
import com.dbeginc.dbweather.utils.utility.NEWSPAPER_KEY
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweathernews.managenewspapers.ManageNewsPapersViewModel
import com.dbeginc.dbweathernews.newspaperdetail.NewsPaperDetailViewModel
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel

class NewsPaperDetailActivity : BaseActivity(), MVMPVView {
    private lateinit var binding: ActivityNewsPaperDetailBinding
    private var toolbarMenu: Menu? = null
    private var newspaperStateBackup: NewsPaperModel? = null

    private val viewModel: NewsPaperDetailViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[NewsPaperDetailViewModel::class.java]
    }

    private val managerViewModel: ManageNewsPapersViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[ManageNewsPapersViewModel::class.java]
    }

    override val stateObserver: Observer<RequestState> = Observer {
        onStateChanged(state = it!!)
    }

    private val newsPaperDetailObserver: Observer<NewsPaperModel> = Observer { newData ->
        newData?.let {
            binding.newsPaper = it

            toolbarMenu?.findItem(R.id.newsPaperSubscriptionStatus)?.setIcon(
                    if (it.subscribed) R.drawable.ic_follow_icon_red
                    else R.drawable.ic_un_follow_icon_black
            )

            binding.executePendingBindings()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_newspaper_detail_menu, menu)

        toolbarMenu = menu

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        binding.newsPaper?.let {
            menu.findItem(R.id.newsPaperSubscriptionStatus)?.setIcon(
                    if (it.subscribed) R.drawable.ic_follow_icon_red
                    else R.drawable.ic_un_follow_icon_black
            )

            menu.findItem(R.id.newsPaperLanguage)?.title = it.language
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.newsPaperSubscriptionStatus -> {
                newspaperStateBackup = binding.newsPaper?.copy()

                binding.newsPaper?.run {
                    subscribed = !subscribed

                    managerViewModel.subscribeTo(this)

                    item.setIcon(if (subscribed) R.drawable.ic_follow_icon_red else R.drawable.ic_un_follow_icon_black)
                }
            }
        }

        return true
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_paper_detail)

        binding.newsPaper = if (savedState == null) intent.getParcelableExtra(NEWSPAPER_KEY)
        else savedState.getParcelable(NEWSPAPER_KEY)

        setSupportActionBar(binding.newsPaperDetailToolbar)

    }

    override fun onStart() {
        super.onStart()

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getNewsPaperDetail().observe(this, newsPaperDetailObserver)

        binding.newsPaperDetailToolbar.setNavigationOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAfterTransition()
            else finish()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(NEWSPAPER_KEY, binding.newsPaper)
    }

    override fun setupView() {
        binding.newsPaperDetailToolbar.setNavigationOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) finishAfterTransition()
            else finish()
        }

        binding.newsPaper?.let {
            viewModel.loadNewsPaperDetail(name = it.name)
        }

    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> return
            RequestState.COMPLETED -> newspaperStateBackup = null
            RequestState.ERROR -> {
                newspaperStateBackup?.let { backup ->
                    if (backup != binding.newsPaper) revertSubscriptionStatus(previousStatus = backup.subscribed)
                }

                binding.newsPaperDetailLayout.snack(message = "Could complete your request, please retry")

            }
        }
    }

    private fun revertSubscriptionStatus(previousStatus: Boolean) {
        binding.newsPaper?.subscribed = previousStatus

        toolbarMenu?.findItem(R.id.newsPaperSubscriptionStatus)
                ?.setIcon(
                        if (previousStatus) R.drawable.ic_follow_icon_red
                        else R.drawable.ic_un_follow_icon_black
                )
    }

}
