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

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.config.ConfigurationTabContract
import com.dbeginc.dbweather.config.managelocations.view.ManageLocationsActivity
import com.dbeginc.dbweather.config.managesources.view.ManageSourcesActivity
import com.dbeginc.dbweather.databinding.FragmentConfigTabBinding
import com.dbeginc.dbweather.utils.utility.Injector
import com.dbeginc.dbweather.utils.utility.hide
import com.dbeginc.dbweather.utils.utility.show
import com.dbeginc.dbweather.utils.utility.snack
import com.google.android.gms.ads.AdRequest
import javax.inject.Inject

/**
 * Created by darel on 24.10.17.
 *
 * Configuration Fragment
 */
class ConfigurationTabFragment : BaseFragment(), ConfigurationTabContract.ConfigurationTabView, View.OnClickListener {
    @Inject lateinit var presenter: ConfigurationTabContract.ConfigurationTabPresenter
    private lateinit var binding: FragmentConfigTabBinding
    private val configurationSaved by lazy { getString(R.string.configuration_changed) }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectConfigurationDep(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.bind(this)
    }

    override fun onStop() {
        super.onStop()
        cleanState()
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

    override fun onClick(view: View) {
        when(view.id) {
            R.id.manageLocationsLabel -> presenter.onManageLocation()
            R.id.manageSourcesLabel -> presenter.onManageSources()
            R.id.helpLabel -> presenter.onHelp()
        }
    }

    /**************************** Configuration Custom View ****************************/
    override fun setupView() {
        setupAds()
        binding.manageLocationsLabel.setOnClickListener(this)
        binding.manageSourcesLabel.setOnClickListener(this)
        binding.helpLabel.setOnClickListener(this)

        binding.weatherNotificationSwitch.setOnCheckedChangeListener { _, isOn -> presenter.onWeatherNotification(isOn) }
        binding.translateNewsPaperSwitch.setOnCheckedChangeListener { _, isOn -> presenter.onNewsPaperTranslation(isOn) }

        presenter.loadConfigurations()
    }

    override fun cleanState() {
        presenter.unBind()
    }

    override fun goToManageLocationScreen() {
        startActivity(Intent(context, ManageLocationsActivity::class.java))
    }

    override fun goToManageSourcesScreen() {
        startActivity(Intent(context, ManageSourcesActivity::class.java))
    }

    override fun goToHelpScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayWeatherNotificationStatus(isOn: Boolean) {
        binding.weatherNotificationSwitch.isChecked = isOn
    }

    override fun displayNewsPaperTranslationStatus(isOn: Boolean) {
        binding.translateNewsPaperSwitch.isChecked = isOn
    }

    override fun showUpdatingStatus() = binding.updateStatus.show()

    override fun hideUpdatingStatus() = binding.updateStatus.hide()

    override fun showStatusChanged() {
        binding.configTabLayout.snack(configurationSaved, duration=Snackbar.LENGTH_SHORT)
    }

    override fun showError(message: String) {
        binding.configTabLayout.snack(message)
    }

    private fun setupAds() {
        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build()
        binding.configTabAd.loadAd(adRequest)
    }
}