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

package com.dbeginc.dbweather.weather.view

import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dbeginc.dbweather.R
import com.dbeginc.dbweather.base.BaseFragment
import com.dbeginc.dbweather.databinding.FragmentWeatherTabBinding
import com.dbeginc.dbweather.utils.animations.widgets.RainFallView
import com.dbeginc.dbweather.utils.animations.widgets.SnowFallView
import com.dbeginc.dbweather.utils.broadcastreceivers.NotificationCanceler
import com.dbeginc.dbweather.utils.helper.ColorManager
import com.dbeginc.dbweather.utils.helper.LocationObserver
import com.dbeginc.dbweather.utils.holder.ConstantHolder
import com.dbeginc.dbweather.utils.holder.ConstantHolder.*
import com.dbeginc.dbweather.utils.utility.*
import com.dbeginc.dbweatherweather.viewmodels.AlertWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.LocationWeatherModel
import com.dbeginc.dbweatherweather.viewmodels.WeatherModel
import com.dbeginc.dbweather.weather.WeatherTabContract
import com.dbeginc.dbweather.weather.adapters.daily.DayAdapter
import com.dbeginc.dbweather.weather.adapters.hourly.HourAdapter
import com.github.clans.fab.FloatingActionButton
import java.util.*
import javax.inject.Inject

/**
 * Created by darel on 22.09.17.
 *
 * Weather Tab Fragment
 */
class WeatherTabFragment : BaseFragment(), WeatherTabContract.WeatherTabView, SearchView.OnSuggestionListener {
    @Inject lateinit var presenter: WeatherTabContract.WeatherTabPresenter

    private val floatingButtonLayoutParams by lazy { ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT) }
    private val locationIcon by lazy { getLocationsIcon() }
    private val currentLocationIcon by lazy { currentLocationIcon() }
    private val locationMenuIcon by lazy { getLocationsMenuIcon() }
    private val animationLayoutParams by lazy { getLayoutParameter() }

    private lateinit var dailyWeatherAdapter: DayAdapter
    private lateinit var hourlyWeatherAdapter: HourAdapter
    private lateinit var binding: FragmentWeatherTabBinding
    private lateinit var componentName: ComponentName
    private lateinit var locationChangeEvent: LocationObserver
    private var weatherData: WeatherModel? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        Injector.injectWeatherTabDep(this)
        if (savedState != null) {
            weatherData = savedState.getParcelable(WEATHER_INFO_KEY)
            componentName = savedState.getParcelable(COMPONENT_NAME_KEY)
        }

        hourlyWeatherAdapter = HourAdapter(weatherData?.hourly ?: emptyList())

        dailyWeatherAdapter = DayAdapter(weatherData?.daily ?: emptyList())

        if (applicationPreferences.isGpsOn()) {
            locationChangeEvent = LocationObserver(activity!!)
            locationChangeEvent.observe(this, android.arch.lifecycle.Observer<android.location.Location> { location -> handleLocationUpdate(location) })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        componentName = activity!!.componentName
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(WEATHER_INFO_KEY, weatherData)
        outState.putParcelable(COMPONENT_NAME_KEY, componentName)
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

        presenter.bind(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        cleanState()
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        presenter.onUserLocationSelected(position)
        return true
    }

    override fun onSuggestionClick(position: Int): Boolean {
        presenter.onUserLocationSelected(position)
        return true
    }

    /********************************* View Part *********************************/

    override fun setupView() {
        setupLocationsMenu()

        setupDailyWeather(dailyLayoutManager =LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false))

        setupHourlyWeather(hourlyLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))

        binding.searchLocationView.setupLookupFeature()

        binding.srlContainer.setOnRefreshListener { askForWeather() }

        presenter.loadUserCities()

        askForWeather()
    }

    override fun cleanState() = presenter.unBind()

    override fun displayWeather(weather: WeatherModel) {
        weatherData = weather

        binding.currentWeatherLayout?.current = weatherData!!.current

        dailyWeatherAdapter.updateData(weatherData!!.daily)

        hourlyWeatherAdapter.updateData(weatherData!!.hourly)

        if (weatherData!!.alerts != null) {
            showWeatherAlerts(weatherData!!.alerts!!)
        }

        binding.weatherTabLayout.setBackgroundResource(ColorManager.getBackgroundColor(weatherData!!.current.icon))

        when(weather.current.icon) {
            R.drawable.rain -> binding.weatherTabLayout.showRainingAnimation()
            R.drawable.snow -> binding.weatherTabLayout.showSnowFallAnimation()
            else -> binding.weatherTabLayout.removeWeatherAnimation()
        }
    }

    override fun getGpsLatitude(): Double = applicationPreferences.getGpsLatitude()

    override fun getGpsLongitude(): Double = applicationPreferences.getGpsLongitude()

    override fun getGpsLocation(): String = applicationPreferences.getGpsLocation()

    override fun defineGpsCoordinates(location: LocationWeatherModel) = applicationPreferences.updateGpsCoordinates(location.name, location.latitude, location.longitude)

    override fun defineLastPreviewedCoordinates(location: LocationWeatherModel) {
        applicationPreferences.updateLastPreviewedCoordinates(
                location.name.plus(",${location.countryName},${location.countryCode}".format()),
                location.latitude,
                location.longitude
        )
    }

    override fun showUserLocations(locations: List<LocationWeatherModel>) {

        val locationToNotDisplay = (binding.floatingLocationsMenu.childCount.minus(1) downTo 0)
                .mapNotNull { binding.floatingLocationsMenu.getChildAt(it) }
                .filter { view -> view is FloatingActionButton }
                .mapNotNull { view -> (view as FloatingActionButton).labelText }

        locations.filterNot {
            location -> locationToNotDisplay.contains(location.name.plus(", ").plus(location.countryCode))
        }.filterIndexed { index, _ -> index <= 5 }
                .forEach { location ->
                    binding.floatingLocationsMenu.addMenuButton(
                            FloatingActionButton(context).apply {
                                labelText = location.name.plus(", ").plus(location.countryCode)
                                setImageDrawable(locationIcon)
                                colorNormal = Color.WHITE
                                setColorPressedResId(R.color.appColorPrimary)
                                setColorRippleResId(R.color.appColorPrimaryDark)
                                layoutParams = floatingButtonLayoutParams
                                setOnClickListener { presenter.getWeatherForCity(location) }
                            }
                    )
                }
    }

    override fun changeCurrentLocationSource(isGpsLocation: Boolean) = applicationPreferences.updateCurrentLocationSource(isGpsLocation)

    override fun displayLoadingWeatherStatus() {
        binding.srlContainer.isRefreshing = true
    }

    override fun hideLoadingWeatherStatus() {
        binding.srlContainer.isRefreshing = false
    }

    override fun displayUpdatingStatus() = binding.loadingStatus.show()

    override fun hideUpdatingStatus() = binding.loadingStatus.remove()

    override fun showWeatherError(message: String) {
        Snackbar.make(binding.weatherTabLayout, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.RED)
                .setAction(R.string.retry, { _ -> presenter.retryWeatherRequest() })
    }

    override fun showError(message: String) = binding.weatherTabLayout.snack(message, duration=Snackbar.LENGTH_LONG)

    /******************************************************************************************************************/
    private fun askForWeather() {
        if (applicationPreferences.isCurrentLocationFromGps()) presenter.getWeather() else presenter.getWeatherForCity(getUserLocation())
    }

    private fun handleLocationUpdate(location: android.location.Location?) {
        if (location != null) {
            presenter.onGpsLocationChanged(latitude=location.latitude, longitude=location.longitude)
            presenter.getWeather()
        }
    }

    private fun getUserLocation() : LocationWeatherModel {
        val location = applicationPreferences.getLastPreviewedLocation().split(",")
        val latitude = applicationPreferences.getLastPreviewedLatitude()
        val longitude = applicationPreferences.getLastPreviewedLongitude()

        return if (location.size == 1) LocationWeatherModel("", latitude, longitude, "", "")
        else LocationWeatherModel(name = location[0], countryCode=location[2], countryName=location[1], latitude=latitude, longitude=longitude)
    }

    /******************************************************************************************************************/

    private fun ConstraintLayout.showRainingAnimation() {
        val snowAnimation = findViewById<SnowFallView>(SnowFallView.VIEW_ID)

        if (snowAnimation != null) removeView(snowAnimation)

        if (findViewById<RainFallView>(RainFallView.VIEW_ID) == null) addView(RainFallView(context), animationLayoutParams)
    }

    private fun ConstraintLayout.showSnowFallAnimation() {
        val rainAnimation = findViewById<RainFallView>(RainFallView.VIEW_ID)

        if (rainAnimation != null) removeView(rainAnimation)

        if (findViewById<SnowFallView>(SnowFallView.VIEW_ID) == null) addView(SnowFallView(context), animationLayoutParams)
    }

    private fun ConstraintLayout.removeWeatherAnimation() {
        removeView(findViewById<SnowFallView>(SnowFallView.VIEW_ID))
        removeView(findViewById<RainFallView>(RainFallView.VIEW_ID))
    }

    private fun setupDailyWeather(dailyLayoutManager: LinearLayoutManager) {
        binding.dailyWeatherRCV.apply {
            adapter = dailyWeatherAdapter
            layoutManager = dailyLayoutManager
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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

        for (alert in alerts) {
            val builder = NotificationCompat.Builder(activity!!, WEATHER_ALERT_CHANNEL_ID)

            browserIntent.data = if (alert.uri.isEmpty()) Uri.EMPTY else Uri.parse(alert.uri)

            val notificationId = WEATHER_ALERT_ID.xor(randomize)

            val locations = alert.regions.joinToString()


            builder.setContentInfo(getString(R.string.notification_alert_city).format(locations))
                    .setContentTitle(alert.title)
                    .setContentText(alert.description)
                    .addAction(R.drawable.ic_close_black, cancel, getDismissIntent(notificationId, context!!))
                    .addAction(R.drawable.ic_internet, openInBrowser, PendingIntent.getActivity(activity, notificationId, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(notificationSound)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(alert.description))
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setChannelId(WEATHER_ALERT_CHANNEL_ID)

            notificationManager.notify(notificationId, builder.build())
        }
    }

    private fun SearchView.setupLookupFeature() {
        val searchManager = context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(componentName))
        setIconifiedByDefault(false)
        isSubmitButtonEnabled = false
        setOnSuggestionListener(this@WeatherTabFragment)
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

    fun onVoiceQuery(query: String?) {
        activity?.contentResolver
                ?.query(Uri.parse(ConstantHolder.SEARCH_QUERY_URI.format(query)),
                        null,
                        null,
                        null,
                        null,
                        null
                )
    }

    private fun getDismissIntent(notificationId : Int, context: Context ) : PendingIntent {
        val intent = Intent(context, NotificationCanceler::class.java)
        intent.putExtra(NOTIFICATION_KEY, notificationId)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun setupLocationsMenu() {
        binding.floatingLocationsMenu.menuIconView.setImageDrawable(locationMenuIcon)

        binding.currentLocationMenuItem.setImageDrawable(currentLocationIcon)

        binding.currentLocationMenuItem.setOnClickListener { presenter.getWeather() }

        binding.currentLocationMenuItem.labelText = getGpsLocation()
    }

    private fun getLayoutParameter(): ConstraintLayout.LayoutParams {
        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)

        params.apply {
            topToTop = binding.weatherTabLayout.id
            leftToLeft = binding.weatherTabLayout.id
            rightToRight = binding.weatherTabLayout.id
        }

        return params
    }

    private fun getLocationsMenuIcon() : Drawable {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawableCompat.create(resources, R.drawable.add_location_icon, if (activity != null) activity!!.theme else null)!!
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.add_location_icon, if (activity != null) activity!!.theme else null)!!
        }
    }

    private fun currentLocationIcon() : Drawable {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawableCompat.create(resources, R.drawable.current_location_icon, if (activity != null) activity!!.theme else null)!!
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.current_location_icon, if (activity != null) activity!!.theme else null)!!
        }
    }

    private fun getLocationsIcon(): Drawable {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawableCompat.create(resources, R.drawable.city_location_icon, if (activity != null) activity!!.theme else null)!!
        } else {
            ResourcesCompat.getDrawable(resources, R.drawable.city_location_icon, if (activity != null) activity!!.theme else null)!!
        }
    }
}