package com.darelbitsy.dbweather.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.extensions.helper.DatabaseOperation;
import com.darelbitsy.dbweather.extensions.holder.ConstantHolder;
import com.darelbitsy.dbweather.extensions.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;
import com.darelbitsy.dbweather.provider.news.DatabaseNewsProvider;
import com.darelbitsy.dbweather.provider.news.NetworkNewsProvider;
import com.darelbitsy.dbweather.provider.weather.DatabaseWeatherProvider;
import com.darelbitsy.dbweather.provider.weather.NetworkWeatherProvider;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.darelbitsy.dbweather.extensions.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

public class WelcomeActivity extends Activity {
    private Intent mIntent;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private boolean isSubscriptionDone = false;
    private DatabaseOperation mDatabase;

    private final DisposableSingleObserver<List<Article>> mNewsObserver = new DisposableSingleObserver<List<Article>>() {
        @Override
        public void onSuccess(final List<Article> newses) {
            Log.i(ConstantHolder.TAG, "Inside the newsObserver WelcomeActivity");
            mIntent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) newses);
            startActivity(mIntent);
            finish();
        }

        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in welcome activity: "+e.getMessage());
        }
    };

    private SharedPreferences mSharedPreferences;
    private final DisposableSingleObserver<Weather> mWeatherObserver = new DisposableSingleObserver<Weather>() {
        @Override
        public void onSuccess(final Weather weather) {
            Log.i(ConstantHolder.TAG, "Inside the WeatherObserver WelcomeActivity");
            mIntent.putExtra(ConstantHolder.WEATHER_DATA_KEY, weather);

            if (isSubscriptionDone && mSharedPreferences
                    .getBoolean(ConstantHolder.FIRST_RUN, true)) {
                mDatabase.initiateNewsSourcesTable();
                //TODO:DB Need to remove this code after refactoring
                subscriptions.add(new NetworkNewsProvider(getApplicationContext())
                        .getNews()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(mNewsObserver));

            } else {
                //TODO:DB Need to remove this code after refactoring
                subscriptions.add(new DatabaseNewsProvider(getApplicationContext())
                        .getNews()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(mNewsObserver));
            }
        }

        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in welcome activity: "
                    + e.getMessage());

            //TODO:DB Need to remove this code after refactoring
            subscriptions.add(new DatabaseWeatherProvider(getApplicationContext())
                    .getWeather()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(mWeatherObserver));
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        mDatabase = DatabaseOperation.newInstance(this);
        mIntent = new Intent(getApplicationContext(),
                WeatherActivity.class);
        mSharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (AppUtil.isNetworkAvailable(getApplicationContext()) && mSharedPreferences
                .getBoolean(ConstantHolder.FIRST_RUN, true)) {

            //TODO:DB Need to remove this code after refactoring
            subscriptions.add(new NetworkWeatherProvider(getApplicationContext())
                    .getWeather()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(mWeatherObserver));
            isSubscriptionDone = true;

        } else {
            //TODO:DB Need to remove this code after refactoring
            subscriptions.add(new DatabaseWeatherProvider(getApplicationContext())
                    .getWeather()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(mWeatherObserver));
        }
    }

    @Override
    protected void onDestroy() {
        subscriptions.dispose();
        super.onDestroy();
    }
}
