package com.darelbitsy.dbweather.models.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.darelbitsy.dbweather.models.api.adapters.network.WeatherRestAdapter;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.broadcastreceivers.SyncDataReceiver;
import com.darelbitsy.dbweather.models.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Darel Bitsy on 03/02/17.
 * This class get the data in background
 * and save to the database so User
 * will always have fresh data
 */

public class FeedDataInForeground {
    private Context mContext;
    private DatabaseOperation mDatabase;

    public FeedDataInForeground(final Context context) {
        mContext = context;
        mDatabase = DatabaseOperation.newInstance(context);
    }

    public void performSync() {
        if (AppUtil.isNetworkAvailable(mContext)) {
            AndroidThreeTen.init(mContext);
            final WeatherRestAdapter weatherApi = new WeatherRestAdapter(mContext);
            GetNewsesHelper.newInstance(mContext)
                    .getNewsesFromApi()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.from(Looper.getMainLooper()))
                    .subscribeWith(new DisposableSingleObserver<ArrayList<Article>>() {
                        @Override
                        public void onSuccess(final ArrayList<Article> articles) {
                            mDatabase.saveNewses(articles);
                            mDatabase.saveLastNewsServerSync();
                        }

                        @Override
                        public void onError(final Throwable throwable) {
                            Log.i(ConstantHolder.TAG, "Error in feedDataInForeground: " + throwable.getMessage());
                        }
                    });

            try {
                final Double[] coordinates = mDatabase.getCoordinates();
                final Weather weather = weatherApi.getWeather(coordinates[0], coordinates[1]);

                mDatabase.saveLastWeatherServerSync();
                mDatabase.saveWeatherData(weather);
                mDatabase.saveCurrentWeather(weather.getCurrently());
                mDatabase.saveHourlyWeather(weather.getHourly().getData());
                mDatabase.saveDailyWeather(weather.getDaily().getData());
                if (weather.getMinutely() != null) {
                    mDatabase
                            .saveMinutelyWeather(weather.getMinutely().getData());
                }
                if (weather.getAlerts() != null) { mDatabase.saveAlerts(weather.getAlerts()); }

            } catch (final IOException e) {
                Log.i(ConstantHolder.TAG, "Error while fetching data in background, error = " + e.getMessage());
            }

            Log.i(ConstantHolder.TAG, "Done fetching data from weather and news api");
        }

        Log.i(ConstantHolder.TAG, "No internet connection trying to fetch data in one hours");
        FeedDataInForeground.setNextSync(mContext);
    }

    public static void setNextSync(final Context context) {
        AndroidThreeTen.init(context);

        final AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        final Intent syncTaskIntent = new Intent(context, SyncDataReceiver.class);
        syncTaskIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                7127,
                new Intent(context, SyncDataReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 3600000, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }
}
