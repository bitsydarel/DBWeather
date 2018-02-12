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

package com.dbeginc.dbweather.config.view

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.config.presenter.ConfigurationTabPresenter
import com.dbeginc.dbweather.databinding.FragmentConfigTabBinding
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.utils.utility.Navigator
import com.dbeginc.dbweather.utils.utility.snack
import com.google.android.gms.ads.AdRequest
import javax.inject.Inject

/**
 * Created by darel on 24.10.17.
 *
 * Configuration Fragment
 */
class ConfigurationTabFragment : BaseFragment(), ConfigurationTabView, WithDependencies, View.OnClickListener {
    @Inject
    lateinit var presenter: ConfigurationTabPresenter
    private lateinit var binding: FragmentConfigTabBinding

    override fun onResume() {
        super.onResume()

        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("687D1ACC5C0ACF7F698DBA9A4E258FFA")
                .build()

        binding.configTabAd.loadAd(adRequest)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_Main_ConfigTab)),
                R.layout.fragment_config_tab,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        cleanState()
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.manageLocationsLabel -> presenter.onManageLocation(this)
            R.id.manageSourcesLabel -> presenter.onManageSources(this)
        }
    }

    /**************************** Configuration Custom View ****************************/
    override fun setupView() {
        binding.manageLocationsLabel.setOnClickListener(this)

        binding.manageSourcesLabel.setOnClickListener(this)

        binding.weatherNotificationSwitch.setOnCheckedChangeListener { _, isOn -> presenter.onWeatherNotification(this, isOn) }

        binding.translateNewsPaperSwitch.setOnCheckedChangeListener { _, isOn -> presenter.onNewsPaperTranslation(this, isOn) }

        presenter.loadConfigurations(this)
    }

    override fun cleanState() = presenter.unBind()

    override fun goToManageLocationScreen() = Navigator.goToManageLocationScreen(this)

    override fun goToManageSourcesScreen() = Navigator.goToManageSourcesScreen(this)

    override fun displayWeatherNotificationStatus(isOn: Boolean) {
        binding.weatherNotificationSwitch.isChecked = isOn
    }

    override fun displayNewsPaperTranslationStatus(isOn: Boolean) {
        binding.translateNewsPaperSwitch.isChecked = isOn
    }

    override fun showMessage(message: String) = binding.configTabLayout.snack(message)

}