package com.darelbitsy.dbweather.ui.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.darelbitsy.dbweather.DBWeatherApplication;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.models.datatypes.news.Article;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.ui.introduction.IntroductionActivity;
import com.darelbitsy.dbweather.ui.main.WeatherActivity;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.FIRST_RUN;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.darelbitsy.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

public class WelcomeActivity extends Activity implements IWelcomeActivityView {
    private Intent mIntent;
    private WelcomeActivityPresenter mPresenter;
    @Inject
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        DBWeatherApplication.getComponent()
                .inject(this);

        mPresenter = new WelcomeActivityPresenter(getApplicationContext(), this);

        if (sharedPreferences
                .getBoolean(FIRST_RUN, true)) {

            sharedPreferences.edit()
                    .putBoolean(FIRST_RUN, true)
                    .apply();
            DatabaseOperation.getInstance(this).initiateNewsSourcesTable();

        } else {
            mIntent = new Intent(getApplicationContext(),
                    WeatherActivity.class);
            mPresenter.loadWeather();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sharedPreferences.getBoolean(FIRST_RUN, true)) {
            startActivity(new Intent(this, IntroductionActivity.class));
            finish();
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
        mPresenter.loadNews();
    }

    @Override
    public void addNewsToWeatherActivityIntent(@NonNull final List<Article> articles) {
        mIntent.putParcelableArrayListExtra(NEWS_DATA_KEY, (ArrayList<? extends Parcelable>) articles);
        startActivity(mIntent);
        mPresenter.clearData();
        finish();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}
