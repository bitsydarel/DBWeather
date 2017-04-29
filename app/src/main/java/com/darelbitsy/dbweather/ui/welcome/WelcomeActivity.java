package com.darelbitsy.dbweather.ui.welcome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.ui.main.WeatherActivity;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.PREFS_NAME;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

public class WelcomeActivity extends Activity implements IWelcomeActivityView {
    private Intent mIntent;
    private boolean isSubscriptionDone;
    private WelcomeActivityPresenter mPresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        mPresenter = new WelcomeActivityPresenter(getApplicationContext(), this, AndroidSchedulers.mainThread());

        mIntent = new Intent(getApplicationContext(),
                WeatherActivity.class);

        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (sharedPreferences
                .getBoolean(ConstantHolder.FIRST_RUN, true)) {

            DatabaseOperation.newInstance(this).initiateNewsSourcesTable();
            mPresenter.getWeather();
            isSubscriptionDone = true;

        } else {
            mPresenter.loadWeather();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.clearData();
    }

    @Override
    public void addWeatherToWeatherActivityIntent(@NonNull final List<WeatherInfo> weatherInfoList) {
        mIntent.putParcelableArrayListExtra(WEATHER_INFO_KEY, (ArrayList<? extends Parcelable>) weatherInfoList);
        if (isSubscriptionDone) {
            mPresenter.getNews();
        } else {
            mPresenter.loadNews();
        }
    }

    @Override
    public void addNewsToWeatherActivityIntent(@NonNull final List<Article> articles) {
        mIntent.putParcelableArrayListExtra(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
        startActivity(mIntent);
        mPresenter.clearData();
        finish();
    }
}
