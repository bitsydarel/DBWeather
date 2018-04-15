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
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentNewsPaperDetailBinding
import com.dbeginc.dbweather.utils.utility.NEWSPAPER_KEY
import com.dbeginc.dbweather.utils.utility.goToManageNewsPapersScreen
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweathernews.managenewspapers.ManageNewsPapersViewModel
import com.dbeginc.dbweathernews.newspaperdetail.NewsPaperDetailViewModel
import com.dbeginc.dbweathernews.viewmodels.NewsPaperModel


/**
 * A simple [Fragment] subclass.
 */
class NewsPaperDetailFragment : BaseFragment(), MVMPVView {
    private lateinit var binding: FragmentNewsPaperDetailBinding
    private lateinit var viewModel: NewsPaperDetailViewModel
    private lateinit var managerViewModel: ManageNewsPapersViewModel
    private lateinit var newsPaperDetail: NewsPaperModel
    private var fragmentMenu: Menu? = null
    override val stateObserver: Observer<RequestState> = Observer { onStateChanged(state = it!!) }
    private val newsPaperDetailObserver = Observer<NewsPaperModel> {
        binding.newsPaper = it

        fragmentMenu?.findItem(R.id.sourceDetailSubscribe)?.setIcon(
                if (binding.newsPaper!!.subscribed) R.drawable.ic_follow_icon_red
                else R.drawable.ic_un_follow_icon_black
        )

        binding.executePendingBindings()
    }

    companion object {
        @JvmStatic
        fun newInstance(newsPaper: NewsPaperModel): NewsPaperDetailFragment {
            val fragment = NewsPaperDetailFragment()

            fragment.arguments = Bundle().apply {
                putParcelable(NEWSPAPER_KEY, newsPaper)
            }

            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProviders.of(this, factory)[NewsPaperDetailViewModel::class.java]

        managerViewModel = ViewModelProviders.of(this, factory)[ManageNewsPapersViewModel::class.java]

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getNewsPaperDetail().observe(this, newsPaperDetailObserver)

    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        newsPaperDetail = if (savedState == null) arguments!!.getParcelable(NEWSPAPER_KEY)
        else savedState.getParcelable(NEWSPAPER_KEY)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(NEWSPAPER_KEY, binding.newsPaper)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.activity_source_detail_menu, menu)

        menu.findItem(R.id.sourceDetaiLLanguage)?.title = binding.newsPaper?.language

        menu.findItem(R.id.sourceDetailSubscribe)?.setIcon(
                if (binding.newsPaper?.subscribed == true) R.drawable.ic_follow_icon_red
                else R.drawable.ic_un_follow_icon_black
        )

        fragmentMenu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.sourceDetailSubscribe) {
            binding.newsPaper?.run {
                if (subscribed) {
                    subscribed = !subscribed
                    managerViewModel.subscribeTo(this)
                    item.setIcon(R.drawable.ic_un_follow_icon_black)

                } else {
                    subscribed = !subscribed
                    managerViewModel.subscribeTo(this)
                    item.setIcon(R.drawable.ic_follow_icon_red)
                }

                true
            } ?: false

        } else super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_news_paper_detail,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.newsPaperDetailToolbar)
    }

    override fun setupView() {
        binding.newsPaperDetailToolbar.setNavigationOnClickListener {
            activity?.let {
                goToManageNewsPapersScreen(
                        container = it,
                        emplacementId = R.id.main_content
                )
            }
        }

        binding.newsPaper = newsPaperDetail

        viewModel.loadNewsPaperDetail(newsPaperDetail.name)
    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> return
            RequestState.COMPLETED -> return
            RequestState.ERROR -> {
                Snackbar.make(binding.newsPaperDetailLayout, "Could not load newspaper detail", Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry) { viewModel.loadNewsPaperDetail(binding.newsPaper!!.name) }
                        .show()
            }
        }
    }

}