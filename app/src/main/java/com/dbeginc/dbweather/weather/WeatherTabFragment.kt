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

package com.dbeginc.dbweather.weather

import android.app.*
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
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.DBWeatherApp
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentWeatherTabBinding
import com.dbeginc.dbweather.di.WithDependencies
import com.dbeginc.dbweather.utils.broadcastreceivers.NotificationCanceler
import com.dbeginc.dbweather.utils.helper.LocationObserver
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.dbeginc.dbweather.utils.holder.ConstantHolder.*
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweather.weather.adapters.daily.DayAdapter
import com.dbeginc.dbweather.weather.adapters.hourly.HourAdapter
import com.dbeginc.dbweathercommon.utils.RequestState
import com.dbeginc.dbweatherweather.fullweather.WeatherViewModel
import com.dbeginc.dbweatherweather.fullweather.contract.WeatherView
import com.dbeginc.dbweatherweather.viewmodels.*
import com.github.clans.fab.FloatingActionButton
import io.reactivex.subjects.BehaviorSubject
import java.util.*

/**
 * Created by darel on 22.09.17.
 *
 * Weather Tab Fragment
 */
class WeatherTabFragment : BaseFragment(), WeatherView, WithDependencies, SearchView.OnSuggestionListener, android.arch.lifecycle.Observer<android.location.Location> {
    private lateinit var binding: FragmentWeatherTabBinding
    private lateinit var locationChangeEvent: LocationObserver
    private lateinit var viewModel: WeatherViewModel
    private val dailyWeatherAdapter = DayAdapter()
    private val hourlyWeatherAdapter = HourAdapter()
    override val state: BehaviorSubject<RequestState> = BehaviorSubject.create()
    private val mColors = SparseIntArray().apply {
        put(R.drawable.clear_day, R.color.clear_day_background)
        put(R.drawable.clear_night, R.drawable.clear_night_background)
        put(R.drawable.partly_cloudy, R.drawable.partly_cloudy_background)
        put(R.drawable.cloudy_night, R.drawable.cloudy_night_background)
        put(R.drawable.cloudy, R.drawable.cloudy_background)
        put(R.drawable.fog, R.color.fog_background)
        put(R.drawable.sleet, R.color.clear_day_background)
        put(R.drawable.snow, R.color.snow_background)
        put(R.drawable.wind, R.color.wind_background)
        put(R.drawable.rain, R.color.rain_background)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        viewModel = ViewModelProviders.of(activity, factory)[WeatherViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getDefaultWeather().observe(this,
                android.arch.lifecycle.Observer {
                    displayDefaultWeather(it!!)
                }
        )

        viewModel.getCustomWeather().observe(this,
                android.arch.lifecycle.Observer {
                    displayCustomWeather(it!!)
                }
        )

        viewModel.getUserLocations().observe(this,
                android.arch.lifecycle.Observer {
                    displayUserLocations(it!!)
                }
        )

        if (preferences.isGpsPermissionOn()) {
            locationChangeEvent = LocationObserver(activity!!)
            locationChangeEvent.observe(this, this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater.cloneInContext(android.view.ContextThemeWrapper(activity, R.style.AppTheme_Main_WeatherTab)),
                R.layout.fragment_weather_tab,
                container,
                false
        )
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        state.subscribe(this::onStateChanged)

        viewModel.presenter.bind(this)
    }

    override fun onChanged(newLocation: Location?) {
        if (newLocation != null) {
            val location = LocationWeatherModel(
                    preferences.getDefaultLocation(),
                    newLocation.latitude,
                    newLocation.longitude,
                    "",
                    ""
            )

            viewModel.loadWeather(state, location)
        }
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        viewModel.onUserLocationSelected(
                state,
                position,
                if (DBWeatherApp.WEATHER_SEARCH_RESULTS.hasValue()) DBWeatherApp.WEATHER_SEARCH_RESULTS.value else emptyList()
        )

        return true
    }

    override fun onSuggestionClick(position: Int): Boolean {
        viewModel.onUserLocationSelected(
                state,
                position,
                if (DBWeatherApp.WEATHER_SEARCH_RESULTS.hasValue()) DBWeatherApp.WEATHER_SEARCH_RESULTS.value else emptyList()
        )

        return true
    }

    /********************************* View Part *********************************/
    override fun setupView() {
        setupLocationsMenu()

        setupDailyWeather(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))

        setupHourlyWeather(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))

        binding.searchLocationView.setupLookupFeature()

        binding.srlContainer.setOnRefreshListener { askForWeather() }

        viewModel.loadUserCities()

        askForWeather()
    }

    override fun onStateChanged(state: RequestState) = when (state) {
        RequestState.LOADING -> binding.srlContainer.isRefreshing = true
        RequestState.COMPLETED -> binding.srlContainer.isRefreshing = false
        RequestState.ERROR -> onRequestWeatherFailed()
    }

    override fun defineDefaultCoordinates(location: LocationWeatherModel) = preferences.updateDefaultCoordinates(
            location.name,
            location.latitude,
            location.longitude
    )

    override fun defineCustomLocationCoordinates(location: LocationWeatherModel) = preferences.updateCustomCoordinates(
            location.name,
            location.latitude,
            location.longitude
    )

    override fun changeCurrentLocationType(isDefault: Boolean) = preferences.updateCurrentLocationType(isDefault)

    private fun onRequestWeatherFailed() {
        Snackbar.make(binding.weatherTabLayout, R.string.weather_error_message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry, { viewModel.presenter.retryWeatherRequest() })
    }

    private fun onWeatherStateChanged(icon: Int) {
        when (icon) {
            R.drawable.rain -> binding.weatherTabLayout.showRainingAnimation()
            R.drawable.snow -> binding.weatherTabLayout.showSnowFallAnimation()
            else -> binding.weatherTabLayout.removeWeatherAnimation()
        }
    }

    private fun displayDefaultWeather(weather: WeatherModel) {
        binding.weather = weather

        if (weather.alerts != null) showWeatherAlerts(weather.alerts!!)

        binding.weatherTabLayout.setBackgroundResource(mColors[weather.current.icon])

        onWeatherStateChanged(weather.current.icon)

        displayHourlyWeather(weather.hourly)

        displayDailyWeather(weather.daily)

        defineDefaultCoordinates(weather.location)
    }

    private fun displayCustomWeather(weather: WeatherModel) {
        binding.weather = weather

        if (weather.alerts != null) showWeatherAlerts(weather.alerts!!)

        binding.weatherTabLayout.setBackgroundResource(mColors[weather.current.icon])

        onWeatherStateChanged(weather.current.icon)

        displayHourlyWeather(weather.hourly)

        displayDailyWeather(weather.daily)

        defineCustomLocationCoordinates(weather.location)

        viewModel.loadUserCities()
    }

    private fun displayDailyWeather(daily: List<DayWeatherModel>) = dailyWeatherAdapter.updateData(daily)

    private fun displayHourlyWeather(hourly: List<HourWeatherModel>) = hourlyWeatherAdapter.updateData(hourly)

    private fun displayUserLocations(locations: List<LocationWeatherModel>) {
        val locationToSkip = binding.floatingLocationsMenu.availableLocations()

        val positions = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        locations.filterNot { location -> locationToSkip.contains(location.fullName()) }
                .filterIndexed { index, _ -> index <= 5 }
                .forEach { location -> binding.floatingLocationsMenu.addMenuButton(location.asFloatingActionButton(positions)) }
    }

    /******************************************************************************************************************/
    private fun SearchView.setupLookupFeature() {
        val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))

        setIconifiedByDefault(false)

        isSubmitButtonEnabled = false

        setOnSuggestionListener(this@WeatherTabFragment)
    }

    private fun askForWeather() {
        if (preferences.isCurrentLocationDefault())
            viewModel.loadWeather(state, preferences.findDefaultLocation())
        else
            viewModel.loadWeatherForCity(state, preferences.findCustomLocation())
    }

    private fun LocationWeatherModel.asFloatingActionButton(positions: ViewGroup.LayoutParams): FloatingActionButton {
        return FloatingActionButton(context)
                .apply {
                    labelText = fullName()
                    colorNormal = Color.WHITE
                    layoutParams = positions
                    setColorPressedResId(R.color.weatherTabPrimary)
                    setColorRippleResId(R.color.weatherTabPrimaryLight)
                    setImageResource(R.drawable.city_location_icon)
                    setOnClickListener {
                        viewModel.loadWeatherForCity(state, this@asFloatingActionButton)
                    }
                }
    }

    /******************************************************************************************************************/
    fun onVoiceQuery(query: String?) {
        binding.searchLocationView.setQuery(query, false)

        activity.contentResolver
                .query(Uri.parse(ConstantHolder.SEARCH_QUERY_URI.format(query)),
                        null,
                        null,
                        null,
                        null,
                        null
                ).close()
    }

    private fun setupLocationsMenu() {
        binding.floatingLocationsMenu.menuIconView.setImageResource(R.drawable.add_location_icon)

        binding.currentLocationMenuItem.apply {
            labelText = preferences.getDefaultLocation()
            setImageResource(R.drawable.current_location_icon)
            setOnClickListener {
                viewModel.loadWeather(state, preferences.findDefaultLocation())
            }
        }
    }

    private fun setupDailyWeather(dailyLayoutManager: LinearLayoutManager) {
        binding.dailyWeatherRCV.apply {
            adapter = dailyWeatherAdapter
            layoutManager = dailyLayoutManager
            setHasFixedSize(true)
            addItemDecoration(
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            )
        }
    }

    private fun setupHourlyWeather(hourlyLayoutManager: LinearLayoutManager) {
        binding.hourlyRecyclerView.apply {
            adapter = hourlyWeatherAdapter
            layoutManager = hourlyLayoutManager
        }
    }

    private fun showWeatherAlerts(alerts: List<AlertWeatherModel>) {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        initChannels(notificationManager)

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
                    .addAction(R.drawable.ic_close_black, cancel, getDismissIntent(notificationId, context!!))
                    .addAction(R.drawable.ic_internet, openInBrowser, PendingIntent.getActivity(activity, notificationId, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(notificationSound)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setChannelId(WEATHER_ALERT_CHANNEL_ID)

            notificationManager.notify(notificationId, builder.build())
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

    private fun getDismissIntent(notificationId : Int, context: Context ) : PendingIntent {
        val intent = Intent(context, NotificationCanceler::class.java).apply {
            putExtra(NOTIFICATION_KEY, notificationId)
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

}