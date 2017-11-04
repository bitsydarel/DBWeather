package com.dbeginc.dbweather.weather

import android.util.Log
import com.crashlytics.android.Crashlytics
import com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG
import com.dbeginc.dbweather.utils.utility.addTo
import com.dbeginc.dbweather.viewmodels.weather.LocationWeatherModel
import com.dbeginc.dbweather.viewmodels.weather.toViewModel
import com.dbeginc.dbweatherdomain.entities.requests.weather.WeatherRequest
import com.dbeginc.dbweatherdomain.entities.weather.Location
import com.dbeginc.dbweatherdomain.usecases.weather.GetAllUserLocations
import com.dbeginc.dbweatherdomain.usecases.weather.GetLocations
import com.dbeginc.dbweatherdomain.usecases.weather.GetWeather
import com.dbeginc.dbweatherdomain.usecases.weather.GetWeatherByLocation
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * Created by darel on 28.05.17.
 * Weather view presenter
 */

class WeatherTabPresenterImpl(private val getLocations: GetLocations,
                              private val getWeather: GetWeather,
                              private val getWeatherByLocation: GetWeatherByLocation,
                              private val getAllUserLocationsCommand: GetAllUserLocations,
                              locationSearchEvent: BehaviorSubject<List<Location>>) : WeatherTabContract.WeatherTabPresenter {

    private lateinit var view: WeatherTabContract.WeatherTabView
    private val rxSubscriptions = CompositeDisposable()
    private val retryRequest = PublishSubject.create<Unit>()
    private var searchLocations: List<LocationWeatherModel> = listOf()

    init {
        locationSearchEvent.subscribe(
                { locations -> searchLocations = locations.map { location -> location.toViewModel() } },
                { error -> notifyLoadError(error) }
        ).addTo(rxSubscriptions)
    }

    override fun onGpsLocationChange(latitude: Double, longitude: Double) {
        view.defineGpsLatitude(latitude=latitude)
        view.defineGpsLongitude(longitude=longitude)
    }

    override fun bind(view: WeatherTabContract.WeatherTabView) {
        this.view = view
        this.view.setupView()
    }

    override fun unBind() {
        getWeather.clean()
        getWeatherByLocation.clean()
        getLocations.clean()
        getAllUserLocationsCommand.clean()
        rxSubscriptions.clear()
    }

    override fun getWeatherForCity(location: LocationWeatherModel) {
        getWeatherByLocation.execute(WeatherRequest(latitude = location.latitude, longitude = location.longitude, arg = location.name))
                .doOnSubscribe { _ -> view.displayLoadingWeatherStatus() }
                .doAfterTerminate { view.hideLoadingWeatherStatus() }
                .map { weather -> weather.toViewModel() }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .subscribe(
                        { weatherModel ->
                            view.displayWeather(weatherModel)
                            view.defineUserLocation(weatherModel.location)
                            view.isCurrentLocation(false)
                        },
                        { error -> notifyWeatherError(error) }
                ).addTo(rxSubscriptions)
    }

    override fun getWeather() {
        loadWeather()
    }

    override fun loadUserCities() {
        getAllUserLocationsCommand.execute(Unit)
                .doOnSubscribe { view.displayUpdatingStatus() }
                .doAfterTerminate { view.hideUpdatingStatus() }
                .map { locations -> locations.map { location -> location.toViewModel() } }
                .subscribe(
                        { locations -> view.defineUserLocations(locations) },
                        { error -> notifyLoadError(error) }
                ).addTo(rxSubscriptions)
    }

    override fun onLocationSelected(position: Int) {
        if (searchLocations.isNotEmpty()) {
            getWeatherForCity(searchLocations[position])
            view.isCurrentLocation(false)
        }
    }

    private fun loadWeather() {
        getWeather.execute(WeatherRequest(view.getLatitude(), view.getLongitude(), view.getCurrentLocation()))
                .doOnSubscribe { view.displayLoadingWeatherStatus() }
                .doAfterTerminate { view.hideLoadingWeatherStatus() }
                .map { weather -> weather.toViewModel() }
                .retryWhen { upstream -> upstream.flatMap { retryRequest.toFlowable(BackpressureStrategy.LATEST) } }
                .subscribe(
                        { weatherModel ->
                            view.displayWeather(weatherModel)
                            view.defineGpsLocation(weatherModel.location)
                            view.isCurrentLocation(true)
                        },
                        { error -> notifyWeatherError(error) }
                ).addTo(rxSubscriptions)
    }

    override fun retryWeatherRequest() = retryRequest.onNext(Unit)

    private fun notifyLoadError(error: Throwable) {
        view.showError(error.localizedMessage)
        Log.e(TAG, error.localizedMessage, error)
        Crashlytics.logException(error)
    }

    private fun notifyWeatherError(error: Throwable) {
        view.showWeatherError(error.localizedMessage)
        Log.e(TAG, error.localizedMessage, error)
        Crashlytics.logException(error)
    }
}
