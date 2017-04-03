package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.database.DatabaseOperation;
import com.darelbitsy.dbweather.helper.holder.ConstantHolder;
import com.darelbitsy.dbweather.controller.api.adapters.helper.GetNewsesHelper;
import com.darelbitsy.dbweather.controller.api.adapters.helper.GetWeatherHelper;
import com.darelbitsy.dbweather.helper.services.NewsDatabaseService;
import com.darelbitsy.dbweather.helper.utility.AppUtil;
import com.darelbitsy.dbweather.model.news.Article;
import com.darelbitsy.dbweather.model.weather.Weather;
import com.darelbitsy.dbweather.helper.services.WeatherDatabaseService;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.darelbitsy.dbweather.helper.holder.ConstantHolder.PREFS_NAME;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

public class WelcomeActivity extends Activity {
    private Intent mIntent;
    private final CompositeDisposable subscriptions = new CompositeDisposable();
    private DisposableSingleObserver<ArrayList<Article>> mNewsObserver;
    private DisposableSingleObserver<Weather> mWeatherObserver;
    private boolean isSubscriptionDone = false;
    private DatabaseOperation mDatabase;


    private void setObservers() {
        mNewsObserver = new DisposableSingleObserver<ArrayList<Article>>() {
            @Override
            public void onSuccess(ArrayList<Article> newses) {
                Log.i(ConstantHolder.TAG, "Inside the newsObserver WelcomeActivity");
                mIntent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, newses);
                startActivity(mIntent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                Log.i(ConstantHolder.TAG, "Error in welcome activity: "+e.getMessage());
            }
        };

        mWeatherObserver = new DisposableSingleObserver<Weather>() {
            @Override
            public void onSuccess(Weather weather) {
                Log.i(ConstantHolder.TAG, "Inside the WeatherObserver WelcomeActivity");
                mIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                mIntent.putExtra(ConstantHolder.WEATHER_DATA_KEY, weather);

                if (isSubscriptionDone && getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .getBoolean(ConstantHolder.FIRST_RUN, true)) {
                    subscriptions.add(new GetNewsesHelper(WelcomeActivity.this)
                            .getNewsesFromApi()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(mNewsObserver));

                } else {
                    subscriptions.add(new GetNewsesHelper(WelcomeActivity.this)
                            .getNewsesFromDatabase(mDatabase)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(mNewsObserver));
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(ConstantHolder.TAG, "Error in welcome activity: "
                        + e.getMessage());
            }
        };
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        mDatabase = new DatabaseOperation(this);
        setObservers();

        if (AppUtil.isNetworkAvailable(this)) {
            subscriptions.add(new GetWeatherHelper(this)
                    .getObservableWeatherFromApi(mDatabase)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(mWeatherObserver));
            isSubscriptionDone = true;

        } else {
            subscriptions.add(new GetWeatherHelper(this)
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
