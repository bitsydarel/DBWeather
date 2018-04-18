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

package com.dbeginc.dbweather.weather

import android.app.*
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.location.Location
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.dbeginc.dbweather.MainActivity
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentWeatherBinding
import com.dbeginc.dbweather.utils.broadcastreceivers.NotificationCanceler
import com.dbeginc.dbweather.utils.locations.WeatherLocationManager
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweathercommon.view.MVMPVView
import com.dbeginc.dbweatherweather.fullweather.WeatherViewModel
import com.dbeginc.dbweatherweather.viewmodels.AlertWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.WeatherLocationModel
import com.dbeginc.dbweatherweather.viewmodels.WeatherModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import java.util.*

/**
 * Created by darel on 22.09.17.
 *
 * Weather Tab Fragment
 */
class WeatherFragment : BaseFragment(), MVMPVView, WithSearchableData, SearchView.OnSuggestionListener, Observer<android.location.Location> {
    private lateinit var binding: FragmentWeatherBinding
    private var userLocations: List<WeatherLocationModel> = emptyList() // Quick fix for duplicate user locations

    private val locationChangeEvent: WeatherLocationManager by lazy {
        return@lazy WeatherLocationManager(context!!.applicationContext)
    }

    private val hourlyWeatherAdapter: HourAdapter by lazy {
        return@lazy HourAdapter()
    }

    private val dailyWeatherAdapter: DayAdapter by lazy {
        return@lazy DayAdapter()
    }

    override val stateObserver: Observer<RequestState> = Observer { state ->
        state?.let { onStateChanged(state = it) }
    }

    private val viewModel: WeatherViewModel by lazy {
        return@lazy ViewModelProviders.of(this, factory.get())[WeatherViewModel::class.java]
    }

    private val defaultWeatherObserver: Observer<WeatherModel> = Observer { weather ->
        weather?.let { displayWeather(weather = it, isDefault = true) }
    }

    private val customWeatherObserver: Observer<WeatherModel> = Observer { weather ->
        weather?.let { displayWeather(weather = it, isDefault = false) }
    }

    private val userLocationsObserver: Observer<List<WeatherLocationModel>> = Observer { location ->
        location?.let { displayUserLocations(locations = it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.weather_menu, menu)

        val findLocation = menu.findItem(R.id.action_search_location)

        val searchView: SearchView? = findLocation.actionView as? android.support.v7.widget.SearchView

        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as? SearchManager

        searchView?.apply {
            setSearchableInfo(searchManager?.getSearchableInfo(activity?.componentName))

            setIconifiedByDefault(true)

            isSubmitButtonEnabled = false

            setOnSuggestionListener(this@WeatherFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.action_refresh_weather) {
            askForWeather()
            true

        } else super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.support.v7.view.ContextThemeWrapper(activity, R.style.AppTheme)),
                R.layout.fragment_weather,
                container,
                false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.setSupportActionBar(binding.weatherToolbar)

        setupView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as? MainActivity)?.let { container ->
            binding.weatherToolbar.setNavigationOnClickListener {
                container.openNavigationDrawer()
            }
        }

        viewModel.getRequestState().observe(this, stateObserver)

        viewModel.getDefaultWeather().observe(this, defaultWeatherObserver)

        viewModel.getCustomWeather().observe(this, customWeatherObserver)

        viewModel.getUserLocations().observe(this, userLocationsObserver)

        if (preferences.get().isGpsPermissionOn()) {
            locationChangeEvent.observe(this, this)
        }

        viewModel.loadUserCities()

        askForWeather()

    }

    override fun onChanged(newLocation: Location?) {
        newLocation?.run {
            preferences.get().updateDefaultCoordinates(
                    city = preferences.get().getDefaultCity(),
                    countryCode = preferences.get().getDefaultCountryCode(),
                    latitude = latitude,
                    longitude = longitude
            )

            askForWeather()
        }
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        onSuggestion(position)
        return true
    }

    override fun onSuggestionClick(position: Int): Boolean {
        onSuggestion(position)
        return true
    }

    /********************************* View Part *********************************/
    override fun setupView() {
        binding.currentLocationMenuItem.apply {
            labelText = preferences.get().getDefaultCity()
            setImageResource(R.drawable.ic_current_location)
            setOnClickListener {
                viewModel.loadWeather(preferences.get().findDefaultLocation())
            }
        }

        binding.hourlyRecyclerView.adapter = hourlyWeatherAdapter

        binding.hourlyRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        binding.dailyWeatherRCV.adapter = dailyWeatherAdapter

        binding.dailyWeatherRCV.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.dailyWeatherRCV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

    }

    override fun onStateChanged(state: RequestState) {
        when (state) {
            RequestState.LOADING -> return
            RequestState.COMPLETED -> return
            RequestState.ERROR -> onRequestWeatherFailed()
        }
    }

    override fun onSearchQuery(query: String?) {
        activity?.contentResolver
                ?.query(Uri.parse(SEARCH_QUERY_URI.format(query)),
                        null,
                        null,
                        null,
                        null,
                        null
                )?.close()
    }

    private fun onRequestWeatherFailed() {
        Snackbar.make(binding.weatherLayout, R.string.weather_error_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry) { askForWeather() }
    }

    private fun onWeatherStateChanged(icon: Int) {
        when (icon) {
            R.drawable.rain -> binding.weatherLayoutContainer.showRainingAnimation()
            R.drawable.snow -> binding.weatherLayoutContainer.showSnowFallAnimation()
            else -> binding.weatherLayoutContainer.removeWeatherAnimation()
        }
    }

    private fun onSuggestion(position: Int) {
        viewModel.onUserLocationSelected(
                position,
                if (WEATHER_SEARCH_RESULTS.hasValue()) WEATHER_SEARCH_RESULTS.value
                else emptyList()
        )
    }

    private fun displayWeather(weather: WeatherModel, isDefault: Boolean) {
        if (isDefault) preferences.get().updateDefaultCoordinates(
                city = weather.location.name,
                countryCode = weather.location.countryCode,
                latitude = weather.location.latitude,
                longitude = weather.location.longitude
        ) else preferences.get().updateCustomCoordinates(
                city = weather.location.name,
                countryCode = weather.location.countryCode,
                latitude = weather.location.latitude,
                longitude = weather.location.longitude
        )

        binding.weather = weather

        binding.current = weather.current

        weather.alerts?.let { validAlerts -> showWeatherAlerts(validAlerts) }

        onWeatherStateChanged(weather.current.icon)

        dailyWeatherAdapter.updateData(weather.daily)

        hourlyWeatherAdapter.updateData(weather.hourly)

        preferences.get().updateCurrentLocationType(isDefault = isDefault)

        viewModel.loadUserCities()
    }

    private fun displayUserLocations(locations: List<WeatherLocationModel>) {
        if (userLocations.containsAll(locations)) return
        else {
            val positions = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            launch(UI) {
                val filterWithIndexed = withContext(context = CommonPool) {
                    // Remove locations already in the locations menu
                    val toBeAdded = locations.filterNot { userLocations.contains(it) }
                    // update the data with the new one
                    userLocations = locations

                    return@withContext toBeAdded.asSequence()
                            .filterIndexed { index, _ -> index <= 5 }
                }

                filterWithIndexed
                        .forEach { location ->
                            binding.floatingLocationsMenu.addMenuButton(
                                    location.asFloatingActionButton(
                                            positions = positions,
                                            context = this@WeatherFragment.context!!,
                                            onClick = viewModel::loadWeatherForCity
                                    )
                            )
                        }
            }
        }
    }

    private fun askForWeather() {
        if (preferences.get().isCurrentLocationDefault()) viewModel.loadWeather(preferences.get().findDefaultLocation())
        else viewModel.loadWeatherForCity(preferences.get().findCustomLocation())
    }

    private fun showWeatherAlerts(alerts: List<AlertWeatherModel>) {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        notificationManager?.let {
            initChannels(notificationManager = it)

            val randomize = Random().nextInt()

            val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val browserIntent = Intent()
                    .setAction(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)

            val cancel = getString(android.R.string.cancel)

            val openInBrowser = getString(R.string.open_with)

            for ((_, title, description, uri, _, _, regions) in alerts) {
                val builder = NotificationCompat.Builder(activity!!, WEATHER_ALERT_CHANNEL_ID)

                browserIntent.data = if (uri.isEmpty()) Uri.EMPTY else Uri.parse(uri)

                val notificationId = WEATHER_ALERT_ID.xor(randomize)

                val locations = regions.joinToString()

                builder.setContentInfo(getString(R.string.notification_alert_city).format(locations))
                        .setContentTitle(title)
                        .setContentText(description)
                        .addAction(R.drawable.ic_close, cancel, getDismissIntent(notificationId, context!!))
                        .addAction(R.drawable.ic_internet, openInBrowser, PendingIntent.getActivity(activity, notificationId, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setSound(notificationSound)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setChannelId(WEATHER_ALERT_CHANNEL_ID)

                it.notify(notificationId, builder.build())
            }
        }
    }

    private fun initChannels(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT < 26 || notificationManager.getNotificationChannel(WEATHER_ALERT_CHANNEL_ID) != null) return

        val channel = NotificationChannel(WEATHER_ALERT_CHANNEL_ID, WEATHER_ALERT_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

        channel.apply {
            description = getString(R.string.weather_alert_channel_desc)
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(channel)
    }

    private fun getDismissIntent(notificationId: Int, context: Context): PendingIntent {
        val intent = Intent(context, NotificationCanceler::class.java).apply {
            putExtra(NOTIFICATION_KEY, notificationId)
        }

        return PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

}