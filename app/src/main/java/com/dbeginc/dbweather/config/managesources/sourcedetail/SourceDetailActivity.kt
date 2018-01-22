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

package com.dbeginc.dbweather.config.managesources.sourcedetail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseActivity
import com.dbeginc.dbweather.databinding.SourceDetailFeatureBinding
import com.dbeginc.dbweather.utils.holder.ConstantHolder.SOURCE_KEY
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.remove
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweather.utils.utility.snack
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailPresenter
import com.dbeginc.dbweathernews.sourcedetail.contract.SourceDetailView
import com.dbeginc.dbweathernews.viewmodels.SourceModel
import javax.inject.Inject


class SourceDetailActivity : BaseActivity(), SourceDetailView {
    @Inject lateinit var presenter: SourceDetailPresenter
    private lateinit var binding: SourceDetailFeatureBinding
    private var sourceDetailMenu: Menu? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        Injector.injectSourceDetailDep(this)

        binding = DataBindingUtil.setContentView(this, R.layout.source_detail_feature)

        binding.source = if (savedState == null) intent.getParcelableExtra(SOURCE_KEY) else savedState.getParcelable(SOURCE_KEY)

        presenter.bind(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        cleanState()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putParcelable(SOURCE_KEY, binding.source)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_source_detail_menu, menu)

        menu?.findItem(R.id.sourceDetaiLLanguage)?.title = binding.source?.language

        menu?.findItem(R.id.sourceDetailSubscribe)?.setIcon(if (binding.source?.subscribed!!) R.drawable.ic_follow_icon_red else R.drawable.ic_un_follow_icon_black)

        sourceDetailMenu = menu

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.sourceDetailSubscribe -> presenter.onSubscribeAction()
        }
        return true
    }

    /************************ Source Detail Custom Part ************************/
    override fun setupView() {

        setSupportActionBar(binding.sourceDetailToolbar)

        binding.sourceDetailToolbar.setNavigationOnClickListener { presenter.onExitAction() }
    }

    override fun cleanState() = presenter.unBind()

    override fun getSource(): SourceModel = binding.source!!

    override fun showLoading() = binding.sourceDetailUpdateAnimation.show()

    override fun hideLoading() = binding.sourceDetailUpdateAnimation.remove()

    override fun showError(error: String) = binding.sourceDetailLayout.snack(error)

    override fun showSubscribedToSource() {
        sourceDetailMenu?.findItem(R.id.sourceDetailSubscribe)?.setIcon(R.drawable.ic_follow_icon_red)
    }

    override fun showUnSubscribedToSource() {
        sourceDetailMenu?.findItem(R.id.sourceDetailSubscribe)?.setIcon(R.drawable.ic_un_follow_icon_black)
    }

    override fun displaySourceDetail(source: SourceModel) {
        binding.source = source
        binding.executePendingBindings()
    }

    override fun close() = finish()

}
