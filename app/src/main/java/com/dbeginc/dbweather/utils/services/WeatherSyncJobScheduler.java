package com.dbeginc.dbweather.utils.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.api.adapters.WeatherRestAdapter;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.observers.DisposableCompletableObserver;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.TAG;

/**
 * Created by Bitsy Darel on 23.05.17.
 * Weather Job Scheduler
 * Sync weather data
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class WeatherSyncJobScheduler extends JobService {
    @Inject
    WeatherRestAdapter mWeatherRestAdapter;

    private final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.getInstance();
    private DisposableCompletableObserver disposableCompletableObserver;

    public WeatherSyncJobScheduler() {
        super();
        DBWeatherApplication.getComponent()
                .inject(this);
    }

    @Override
    public boolean onStartJob(final JobParameters params) {

        disposableCompletableObserver = Completable.create(completableEmitter -> {
            try {
                final DatabaseOperation databaseOperation = DatabaseOperation.getInstance(getApplicationContext());

                final Double[] coordinates = WeatherUtil.getCoordinates(databaseOperation);
                final Weather weather = mWeatherRestAdapter.getWeather(coordinates[0],
                        coordinates[1]);

                weather.setCityName(WeatherUtil.getLocationName(getApplicationContext(),
                        coordinates[0],
                        coordinates[1]));

                databaseOperation.saveWeatherData(weather);
                databaseOperation.saveCoordinates(weather.getLatitude(),
                        weather.getLongitude());

                if (weather.getCurrently() != null) {
                    databaseOperation.saveCurrentWeather(weather.getCurrently());
                }

                if (weather.getDaily() != null) {
                    databaseOperation.saveDailyWeather(weather.getDaily()
                            .getData());
                }

                if (weather.getHourly() != null) {
                    databaseOperation.saveHourlyWeather(weather.getHourly()
                            .getData());
                }

                if (weather.getMinutely() != null) {
                    databaseOperation.saveMinutelyWeather(weather.getMinutely()
                            .getData());
                }

                if (weather.getAlerts() != null) {
                    databaseOperation.saveAlerts(weather.getAlerts());
                }

                completableEmitter.onComplete();

            } catch (final Exception error) {
                completableEmitter.onError(error);
            }

        }).subscribeOn(schedulersProvider.getWeatherScheduler())
                .observeOn(schedulersProvider.getUIScheduler())
                .unsubscribeOn(schedulersProvider.getWeatherScheduler())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        jobFinished(params, true);
                        Log.i(TAG, "Weather SYNC FROM JOBSCHEDULER " + "IS DONE");
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        jobFinished(params, true);
                        Crashlytics.logException(throwable);
                    }
                });

        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        if (disposableCompletableObserver != null) { disposableCompletableObserver.dispose(); }
        return true;
    }
}
