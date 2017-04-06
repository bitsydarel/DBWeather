package com.darelbitsy.dbweather.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.controller.api.adapters.helper.GetNewsesHelper;
import com.darelbitsy.dbweather.controller.api.adapters.network.NewsRestAdapter;
import com.darelbitsy.dbweather.controller.api.adapters.network.WeatherAdapter;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.Article;
import com.darelbitsy.dbweather.model.news.NewsResponse;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.helper.receiver.SyncDataReceiver;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Darel Bitsy on 03/02/17.
 */

public class FeedDataInForeground {
    private Context mContext;
    private DatabaseOperation mDatabase;

    public FeedDataInForeground(Context context) {
        mContext = context;
        mDatabase = DatabaseOperation.newInstance(context);
    }

    public void performSync() {
        if (AppUtil.isNetworkAvailable(mContext)) {
            AndroidThreeTen.init(mContext);
            WeatherAdapter weatherApi = new WeatherAdapter(mContext);
            GetNewsesHelper.newInstance(mContext)
                    .getNewsesFromApi()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.from(Looper.getMainLooper()))
                    .subscribeWith(new DisposableSingleObserver<ArrayList<Article>>() {
                        @Override
                        public void onSuccess(ArrayList<Article> articles) {
                            mDatabase.saveNewses(articles);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.i(ConstantHolder.TAG, "Error in feedDataInForeground: " + throwable.getMessage());
                        }
                    });

            try {
                Double[] coordinates = mDatabase.getCoordinates();
                Weather weather = weatherApi.getWeather(coordinates[0], coordinates[1]);

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

            } catch (IOException e) {
                Log.i(ConstantHolder.TAG, "Error while fetching data in background, error = " + e.getMessage());
            }

            Log.i(ConstantHolder.TAG, "Done fetching data from weather and news api");
        }

        Log.i(ConstantHolder.TAG, "No internet connection trying to fetch data in one hours");
        FeedDataInForeground.setNextSync(mContext);
    }

    public static void setNextSync(Context context) {
        AndroidThreeTen.init(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent syncTaskIntent = new Intent(context, SyncDataReceiver.class);
        syncTaskIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                .addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                7127,
                new Intent(context, SyncDataReceiver.class),
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 3600000, AlarmManager.INTERVAL_HOUR, pendingIntent);
    }
}
