package com.dbeginc.dbweather.utils.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.models.api.adapters.WeatherRestAdapter;
import com.dbeginc.dbweather.models.datatypes.weather.Weather;
import com.dbeginc.dbweather.utils.helper.DatabaseOperation;
import com.dbeginc.dbweather.utils.utility.weather.WeatherUtil;

import javax.inject.Inject;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_SYNC_JOB_ID;

/**
 * Created by Bitsy Darel on 23.05.17.
 * Weather Sync Service
 */

public class WeatherSyncService extends IntentService {
    @Inject
    WeatherRestAdapter mWeatherRestAdapter;

    public WeatherSyncService() {
        super(WeatherSyncService.class.getSimpleName());
        DBWeatherApplication.getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        try {
            final DatabaseOperation databaseOperation = DatabaseOperation.getInstance(getApplicationContext());

            final Double[] coordinates = WeatherUtil.getCoordinates(databaseOperation);
            final Weather weather = mWeatherRestAdapter.getWeather(coordinates[0],
                    coordinates[1]);

            weather.setCityName(WeatherUtil.getLocationName(getApplicationContext(),
                    coordinates[0],
                    coordinates[1]));

            databaseOperation.saveWeatherData(weather);
            databaseOperation.saveCoordinates(weather.getLatitude(), weather.getLongitude());

            if (weather.getCurrently() != null) {
                databaseOperation.saveCurrentWeather(weather.getCurrently());
            }

            if (weather.getDaily() != null) {
                databaseOperation.saveDailyWeather(weather.getDaily().getData());
            }

            if (weather.getHourly() != null) {
                databaseOperation.saveHourlyWeather(weather.getHourly().getData());
            }

            if (weather.getMinutely() != null) {
                databaseOperation.saveMinutelyWeather(weather.getMinutely().getData());
            }

            if (weather.getAlerts() != null) { databaseOperation.saveAlerts(weather.getAlerts()); }

        } catch (final Exception error) { Crashlytics.logException(error); }

        final Intent weatherSyncService = new Intent(this, WeatherSyncService.class);
        weatherSyncService.setFlags(START_FLAG_REDELIVERY);

        final PendingIntent weatherServicePendingIntent = PendingIntent.getBroadcast(this,
                WEATHER_SYNC_JOB_ID,
                weatherSyncService,
                PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC,
                System.currentTimeMillis()+3600000,
                AlarmManager.INTERVAL_HOUR,
                weatherServicePendingIntent);
    }
}
