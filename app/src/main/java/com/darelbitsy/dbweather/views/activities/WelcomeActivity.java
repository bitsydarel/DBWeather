package com.darelbitsy.dbweather.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.models.helper.DatabaseOperation;
import com.darelbitsy.dbweather.models.api.adapters.helper.GetNewsesHelper;
import com.darelbitsy.dbweather.models.api.adapters.helper.GetWeatherHelper;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.utility.AppUtil;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.darelbitsy.dbweather.models.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

public class WelcomeActivity extends Activity {
    private Intent mIntent;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private boolean isSubscriptionDone = false;
    private DatabaseOperation mDatabase;

    private final DisposableSingleObserver<ArrayList<Article>> mNewsObserver = new DisposableSingleObserver<ArrayList<Article>>() {
        @Override
        public void onSuccess(final ArrayList<Article> newses) {
            Log.i(ConstantHolder.TAG, "Inside the newsObserver WelcomeActivity");
            mIntent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, newses);
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
                subscriptions.add(GetNewsesHelper.newInstance(WelcomeActivity.this)
                        .getNewsesFromApi()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(mNewsObserver));

            } else {
                subscriptions.add(GetNewsesHelper.newInstance(WelcomeActivity.this)
                        .getNewsesFromDatabase(mDatabase)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(mNewsObserver));
            }
        }

        @Override
        public void onError(final Throwable e) {
            Log.i(ConstantHolder.TAG, "Error in welcome activity: "
                    + e.getMessage());

            subscriptions.add(GetWeatherHelper.newInstance(WelcomeActivity.this)
                    .getObservableWeatherFromDatabase(mDatabase)
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
                MainActivity.class);
        mSharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (AppUtil.isNetworkAvailable(getApplicationContext()) && mSharedPreferences
                .getBoolean(ConstantHolder.FIRST_RUN, true)) {
            subscriptions.add(GetWeatherHelper.newInstance(this)
                    .getObservableWeatherFromApi(mDatabase)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(mWeatherObserver));
            isSubscriptionDone = true;

        } else {
            subscriptions.add(GetWeatherHelper.newInstance(this)
                    .getObservableWeatherFromDatabase(mDatabase)
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
