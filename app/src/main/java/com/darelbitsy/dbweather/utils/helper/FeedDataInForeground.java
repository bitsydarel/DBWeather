package com.darelbitsy.dbweather.utils.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.darelbitsy.dbweather.utils.broadcastreceivers.SyncDataReceiver;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.models.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.models.provider.weather.NetworkWeatherProvider;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.List;

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
        final NetworkInfo networkInfo;
        final ConnectivityManager manager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        if (isAvailable) {
            AndroidThreeTen.init(mContext);

            new NetworkNewsProvider()
                    .getNews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.from(Looper.getMainLooper()))
                    .subscribeWith(new DisposableSingleObserver<List<Article>>() {
                        @Override
                        public void onSuccess(final List<Article> articles) {
                            mDatabase.saveLastWeatherServerSync();
                            mDatabase.saveNewses(articles);
                            mDatabase.saveLastNewsServerSync();
                        }

                        @Override
                        public void onError(final Throwable throwable) {
                            Log.i(ConstantHolder.TAG, "Error in feedDataInForeground for news: " + throwable.getMessage());
                        }
                    });

            new NetworkWeatherProvider()
                    .getWeather()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Weather>() {
                        @Override
                        public void onSuccess(@NonNull final Weather weather) {
                            mDatabase.saveLastWeatherServerSync();
                            mDatabase.saveCurrentWeather(weather.getCurrently());
                            mDatabase.saveDailyWeather(weather.getDaily().getData());
                            mDatabase.saveHourlyWeather(weather.getHourly().getData());
                            mDatabase.saveWeatherData(weather);
                            if (weather.getMinutely() != null) {
                                mDatabase
                                        .saveMinutelyWeather(weather.getMinutely().getData());
                            }
                            if (weather.getAlerts() != null) { mDatabase.saveAlerts(weather.getAlerts()); }

                        }

                        @Override
                        public void onError(@NonNull final Throwable throwable) {
                            Log.i(ConstantHolder.TAG, "Error in feedDataInForeground for weather: " + throwable.getMessage());
                        }
                    });

            Log.i(ConstantHolder.TAG, "Done fetching data from weather and news api");
        } else {
            Log.i(ConstantHolder.TAG, "No internet connection trying to fetch data in one hours");
        }

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
