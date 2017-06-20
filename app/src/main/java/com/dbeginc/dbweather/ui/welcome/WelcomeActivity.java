package com.dbeginc.dbweather.ui.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.dbeginc.dbweather.DBWeatherApplication;
import com.dbeginc.dbweather.R;
import com.dbeginc.dbweather.models.datatypes.news.Article;
import com.dbeginc.dbweather.models.datatypes.weather.WeatherData;
import com.dbeginc.dbweather.ui.BaseActivity;
import com.dbeginc.dbweather.ui.intro.IntroductionActivity;
import com.dbeginc.dbweather.ui.main.DBWeatherActivity;

import java.util.ArrayList;
import java.util.List;

import static com.dbeginc.dbweather.utils.holder.ConstantHolder.NEWS_DATA_KEY;
import static com.dbeginc.dbweather.utils.holder.ConstantHolder.WEATHER_INFO_KEY;

/**
 * Created by Darel Bitsy on 13/02/17.
 * Welcome screen and initializer
 */

public class WelcomeActivity extends BaseActivity implements IWelcomeActivityView {
    private Intent mIntent;
    private WelcomeActivityPresenter mPresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        DBWeatherApplication.getComponent()
                .inject(this);

        mPresenter = new WelcomeActivityPresenter(getApplicationContext(), this);

        if (mAppDataProvider.isFirstRun()) {

            mAppDataProvider.setFirstRun(true);
            mPresenter.populateNewsDatabase();

        } else {
            mIntent = new Intent(getApplicationContext(),
                    DBWeatherActivity.class);
            mPresenter.loadWeather();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mAppDataProvider.isFirstRun()) {
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
    public void addWeatherToWeatherActivityIntent(@NonNull final WeatherData weatherData) {
        mIntent.putExtra(WEATHER_INFO_KEY, weatherData);
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

    @Override
    public void showWeatherErrorMessage() {
        Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.weather_error_message), Snackbar.LENGTH_LONG);
    }

    @Override
    public void showNewsErrorMessage() {
        Snackbar.make(getWindow().getDecorView().getRootView(), getString(R.string.news_error_message), Snackbar.LENGTH_LONG);
    }
}
