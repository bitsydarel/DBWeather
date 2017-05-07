package com.darelbitsy.dbweather.ui.config;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.databinding.NewsConfigurationActivityBinding;
import com.darelbitsy.dbweather.models.datatypes.weather.HourlyData;
import com.darelbitsy.dbweather.models.datatypes.weather.WeatherInfo;
import com.darelbitsy.dbweather.models.provider.AppDataProvider;
import com.darelbitsy.dbweather.models.provider.schedulers.RxSchedulersProvider;
import com.darelbitsy.dbweather.ui.config.adapter.NewsConfigurationAdapter;
import com.darelbitsy.dbweather.ui.main.WeatherActivity;
import com.darelbitsy.dbweather.utils.helper.DatabaseOperation;
import com.darelbitsy.dbweather.utils.holder.ConstantHolder;
import com.darelbitsy.dbweather.utils.utility.weather.WeatherUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * Created by Darel Bitsy on 11/02/17.
 */
public class NewsConfigurationActivity extends AppCompatActivity {
    private DatabaseOperation database;
    private NewsConfigurationActivityBinding mNewsBinding;
    private AppDataProvider mAppDataProvider;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNewsBinding = DataBindingUtil.setContentView(this, R.layout.news_configuration_activity);
        setSupportActionBar(mNewsBinding.newsConfigToolbar.rootNewsConfigToolbar);

        database = DatabaseOperation.getInstance(this);

        final NewsConfigurationAdapter adapter = new NewsConfigurationAdapter(database.getNewsSources(),
                database);

        mNewsBinding.newsConfigRecyclerView.setAdapter(adapter);
        mNewsBinding.newsConfigRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        mNewsBinding.newsConfigRecyclerView.setHasFixedSize(true);
        mAppDataProvider = new AppDataProvider(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNewsBinding.newsConfigToolbar.backToMainActivity.setOnClickListener(v -> {

            final RxSchedulersProvider schedulersProvider = RxSchedulersProvider.newInstance();
            mAppDataProvider.getWeatherFromDatabase()
                    .subscribeOn(schedulersProvider.getWeatherScheduler())
                    .observeOn(schedulersProvider.getComputationThread())
                    .map(weather -> WeatherUtil.parseWeather(weather, getApplicationContext()))
                    .observeOn(schedulersProvider.getUIScheduler())
                    .subscribeWith(new DisposableSingleObserver<Pair<List<WeatherInfo>, List<HourlyData>>>() {
                        @Override
                        public void onSuccess(final Pair<List<WeatherInfo>, List<HourlyData>> weather) {
                            final Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
                            intent.putParcelableArrayListExtra(ConstantHolder.WEATHER_INFO_KEY, (ArrayList<? extends Parcelable>) weather.first);
                            intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, DatabaseOperation.getInstance(getApplicationContext()).getNewFromDatabase());
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onError(final Throwable throwable) {
                            //TODO: Show Message Error and Prompt for retry
                        }
                    });

            /*final Weather weather = database.getWeatherData();
            weather.setCurrently(database.getCurrentWeatherFromDatabase());

            weather.setDaily(new Daily());
            weather.getDaily().setData(database.getDailyWeatherFromDatabase());

            weather.setHourly(new Hourly());
            weather.getHourly().setData(database.getHourlyWeatherFromDatabase());

            weather.setAlerts(database.getAlerts());
            final Intent intent = new Intent(getApplicationContext(), WeatherActivity.class);
            intent.putParcelableArrayListExtra(ConstantHolder.WEATHER_INFO_KEY, (ArrayList<? extends Parcelable>) WeatherUtil.parseWeather(weather, getApplicationContext()));
            intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, database.getNewFromDatabase());
            startActivity(intent);
            finish();*/
        });
    }
}
