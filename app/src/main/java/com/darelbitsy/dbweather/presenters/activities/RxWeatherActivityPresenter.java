package com.darelbitsy.dbweather.presenters.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.extensions.utility.AppUtil;
import com.darelbitsy.dbweather.extensions.utility.weather.WeatherUtil;
import com.darelbitsy.dbweather.models.datatypes.geonames.GeoName;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Currently;
import com.darelbitsy.dbweather.models.datatypes.weather.DailyData;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.provider.repository.IUserCitiesRepository;
import com.darelbitsy.dbweather.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.provider.weather.NetworkWeatherProvider;
import com.darelbitsy.dbweather.views.activities.IWeatherActivityView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 22/04/17.
 * MainView Presenter with Rx
 */

public class RxWeatherActivityPresenter implements IWeatherActivityPresenter {

    private final DatabaseWeatherProvider mDatabaseWeatherProvider;
    private final NetworkWeatherProvider mNetworkWeatherProvider;
    private final DatabaseNewsProvider mDatabaseNewsProvider;
    private final NetworkNewsProvider mNetworkNewsProvider;
    private final RxSchedulersProvider mSchedulersProvider;
    private final Context mApplicationContext;
    private final IUserCitiesRepository mUserCitiesRepository;
    private final IWeatherActivityView<Pair<List<WeatherInfo>, List<HourlyData>>, List<Article>> mMainView;
    private final CompositeDisposable rxSubscriptions = new CompositeDisposable();

    public RxWeatherActivityPresenter(final Context context,
                                      final IUserCitiesRepository userCitiesRepository,
                                      final IWeatherActivityView<Pair<List<WeatherInfo>, List<HourlyData>>, List<Article>> mainView) {

        mApplicationContext = context.getApplicationContext();
        mUserCitiesRepository = userCitiesRepository;

        mDatabaseWeatherProvider = new DatabaseWeatherProvider(mApplicationContext);
        mNetworkWeatherProvider = new NetworkWeatherProvider(mApplicationContext);

        mDatabaseNewsProvider = new DatabaseNewsProvider(mApplicationContext);
        mNetworkNewsProvider = new NetworkNewsProvider(mApplicationContext);

        mSchedulersProvider = RxSchedulersProvider.newInstance();

        mMainView = mainView;

    }

    public void configureView() {
        loadUserCitiesMenu();
    }

    @Override
    public void loadWeather() {
        rxSubscriptions.add(mDatabaseWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver()));
    }

    @Override
    public void loadNews() {
        rxSubscriptions.add(mDatabaseNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NewsObserver()));
    }

    @Override
    public void saveState(final Bundle save) {

    }

    @Override
    public void getWeather() {
        rxSubscriptions.add(mNetworkWeatherProvider.getWeather()
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver()));
    }

    @Override
    public void getWeatherForCity(@NonNull final String cityName,
                                  final double latitude,
                                  final double longitude) {

        if (AppUtil.isNetworkAvailable(mApplicationContext)) {
            rxSubscriptions.add(mNetworkWeatherProvider.getWeatherForCity(cityName, latitude, longitude)
                    .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new WeatherObserver()));

        } else {
            mMainView.showNetworkWeatherErrorMessage();
        }
    }

    @Override
    public void configureNewsMenu() {

    }

    @Override
    public void loadWeatherForCity(@NonNull final String cityName,
                                   final double latitude,
                                   final double longitude) {

        rxSubscriptions.add(mDatabaseWeatherProvider
                .getWeatherForCity(cityName, latitude, longitude)
                .subscribeOn(mSchedulersProvider.getWeatherScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new WeatherObserver()));
    }

    @Override
    public void loadUserCitiesMenu() {

        rxSubscriptions.add(mUserCitiesRepository.getUserCities()
                .subscribeOn(mSchedulersProvider.getDatabaseWorkScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<GeoName>>() {
                    @Override
                    public void onSuccess(@NonNull final List<GeoName> userCities) {
                        if (userCities.isEmpty()) {
                            mMainView.setupNavigationDrawerWithNoCities();

                        } else {
                            mMainView.setupNavigationDrawerWithCities(userCities);
                        }
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        mMainView.setupNavigationDrawerWithNoCities();
                    }
                }));
    }

    @Override
    public void getNews() {
        rxSubscriptions.add(mNetworkNewsProvider.getNews()
                .subscribeOn(mSchedulersProvider.getNewsScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new NewsObserver()));
    }

    private class NewsObserver extends DisposableSingleObserver<List<Article>> {
        @Override
        public void onSuccess(@NonNull final List<Article> articles) {
            mMainView.showNews(articles);
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkNewsErrorMessage();
        }
    }

    private class WeatherObserver extends DisposableSingleObserver<Weather> {

        @Override
        public void onSuccess(@NonNull final Weather weather) {
            mMainView.showWeather(new Pair<>(parseWeather(weather), weather.getHourly().getData()));
        }

        @Override
        public void onError(final Throwable throwable) {
            mMainView.showNetworkWeatherErrorMessage();
        }

        private WeatherInfo convertToWeatherInfo(@NonNull final String locationName,
                                                 @NonNull final DailyData day,
                                                 @NonNull final WeatherInfo weatherInfo,
                                                 @Nullable final String timeZone) {

            weatherInfo.isCurrentWeather.set(false);

            weatherInfo.locationName.set(locationName);
            weatherInfo.icon.set(WeatherUtil.getIconId(day.getIcon()));
            weatherInfo.summary.set(day.getSummary());

            weatherInfo.time.set(WeatherUtil.getDayOfTheWeek(day.getTime(), timeZone));

            weatherInfo.temperature.set(WeatherUtil.getTemperatureInInt(day.getTemperatureMax()));
            weatherInfo.apparentTemperature.set(WeatherUtil.getTemperatureInInt(day.getApparentTemperatureMax()));

            weatherInfo.windSpeed.set(String.format(Locale.ENGLISH,
                    mApplicationContext.getString(R.string.humidity_value),
                    WeatherUtil.getWindSpeedMeterPerHour(day.getWindSpeed())));

            weatherInfo.humidity.set(String.format(Locale.ENGLISH,
                    mApplicationContext.getString(R.string.humidity_value),
                    WeatherUtil.getHumidityPourcentage(day.getHumidity())));

            weatherInfo.cloudCover.set(String.format(Locale.ENGLISH,
                    mApplicationContext.getString(R.string.cloudCoverValue),
                    WeatherUtil.getCloudCoverPourcentage(day.getCloudCover())));

            if (day.getPrecipType() == null) {
                weatherInfo.precipitationType
                        .set(mApplicationContext.getString(R.string.precipitation_default_value));

            } else {
                weatherInfo.precipitationType
                        .set(String.format(Locale.getDefault(),
                                mApplicationContext.getString(R.string.precipeChanceTypeLabel),
                                day.getPrecipType()));
            }

            weatherInfo.precipitationProbability.set(String.format(Locale.getDefault(),
                    mApplicationContext.getString(R.string.precipChanceValue),
                    WeatherUtil.getPrecipPourcentage(day.getPrecipProbability())));

            weatherInfo.sunrise.set(WeatherUtil.getFormattedTime(day.getSunriseTime(), timeZone));
            weatherInfo.sunset.set(WeatherUtil.getFormattedTime(day.getSunsetTime(), timeZone));

            return weatherInfo;
        }

        private List<WeatherInfo> parseWeather(final Weather weather) {
            final List<WeatherInfo> weatherInfoList = new ArrayList<>();
            final Calendar calendar = Calendar.getInstance();
            final String currentDayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK,
                    Calendar.LONG,
                    Locale.getDefault());

            int count = 0;
            boolean isTodaySet = false;
            boolean isTomorrowSet = false;

            Integer currentDayIndex = null;

            while (count < 7) {
                for (final DailyData day : weather.getDaily().getData()) {
                    if (count == 7) {
                        break;
                    }

                    final WeatherInfo weatherInfo = new WeatherInfo();

                    if (!isTodaySet &&
                            currentDayName.equalsIgnoreCase(WeatherUtil.getDayOfTheWeek(day.getTime(),
                                    weather.getTimezone()))) {

                        count = 0;
                        final Currently currently = weather.getCurrently();

                        weatherInfo.isCurrentWeather.set(true);

                        weatherInfo.locationName.set(weather.getCityName());
                        weatherInfo.icon.set(WeatherUtil.getIconId(currently.getIcon()));
                        weatherInfo.summary.set(currently.getSummary());

                        if ("rain".equalsIgnoreCase(currently.getIcon())) {
                            weatherInfo.videoBackgroundFile.set(R.raw.rain_background);

                        } else if ("snow".equalsIgnoreCase(currently.getIcon())) {
                            weatherInfo.videoBackgroundFile.set(R.raw.snow_background);
                        }

                        if ("sleet".equalsIgnoreCase(currently.getIcon())) {
                            weatherInfo.setSleet(true);
                        }

                        weatherInfo.time.set(String.format(Locale.getDefault(),
                                mApplicationContext.getString(R.string.time_label),
                                WeatherUtil.getFormattedTime(currently.getTime(), weather.getTimezone())));

                        weatherInfo.temperature.set(WeatherUtil.getTemperatureInInt(currently.getTemperature()));
                        weatherInfo.apparentTemperature.set(WeatherUtil.getTemperatureInInt(currently.getApparentTemperature()));

                        weatherInfo.windSpeed.set(String.format(Locale.ENGLISH,
                                mApplicationContext.getString(R.string.humidity_value),
                                WeatherUtil.getWindSpeedMeterPerHour(currently.getWindSpeed())));

                        weatherInfo.humidity.set(String.format(Locale.ENGLISH,
                                mApplicationContext.getString(R.string.humidity_value),
                                WeatherUtil.getHumidityPourcentage(currently.getHumidity())));

                        weatherInfo.cloudCover.set(String.format(Locale.ENGLISH,
                                mApplicationContext.getString(R.string.cloudCoverValue),
                                WeatherUtil.getCloudCoverPourcentage(currently.getCloudCover())));

                        if (currently.getPrecipType() == null) {
                            weatherInfo.precipitationType.set(mApplicationContext.getString(R.string.precipitation_default_value));

                        } else {
                            weatherInfo.precipitationType.set(String.format(Locale.getDefault(),
                                    mApplicationContext.getString(R.string.precipeChanceTypeLabel),
                                    currently.getPrecipType()));
                        }

                        weatherInfo.precipitationProbability.set(String.format(Locale.getDefault(),
                                mApplicationContext.getString(R.string.precipChanceValue),
                                WeatherUtil.getPrecipPourcentage(currently.getPrecipProbability())));

                        weatherInfo.sunrise.set(WeatherUtil.getFormattedTime(day.getSunriseTime(), weather.getTimezone()));
                        weatherInfo.sunset.set(WeatherUtil.getFormattedTime(day.getSunsetTime(), weather.getTimezone()));

                        weatherInfoList.add(count, weatherInfo);

                        currentDayIndex = count++;
                        isTodaySet = true;

                    } else if (currentDayIndex != null
                            && count == (currentDayIndex + 1)) {

                        weatherInfoList.add(1, convertToWeatherInfo(weather.getCityName(),
                                day, weatherInfo, weather.getTimezone()));

                        count++;
                        isTomorrowSet = true;

                    } else if (isTodaySet && isTomorrowSet) {

                        weatherInfoList.add(count,
                                convertToWeatherInfo(weather.getCityName(), day, weatherInfo, weather.getTimezone()));

                        count++;
                    }
                }
            }
            return weatherInfoList;
        }
    }
}
