package com.darelbitsy.dbweather.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.models.helper.DatabaseOperation;
import com.darelbitsy.dbweather.views.activities.MainActivity;
import com.darelbitsy.dbweather.views.adapters.listAdapter.NewsConfigurationAdapter;
import com.darelbitsy.dbweather.models.holder.ConstantHolder;
import com.darelbitsy.dbweather.models.datatypes.weather.Daily;
import com.darelbitsy.dbweather.models.datatypes.weather.Hourly;
import com.darelbitsy.dbweather.models.datatypes.weather.Weather;

/**
 * Created by Darel Bitsy on 11/02/17.
 */
public class NewsConfigurationActivity extends AppCompatActivity {
    private ImageView backToMainActivity;
    private DatabaseOperation database;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_configuration_activity);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.news_config_toolbar);
        setSupportActionBar(toolbar);

        database = DatabaseOperation.newInstance(this);
        final RecyclerView newsConfigurationRecyclerView = (RecyclerView)
                findViewById(R.id.news_config_recyclerView);

        final NewsConfigurationAdapter adapter = new NewsConfigurationAdapter(database.getNewsSources(),
                database);
        newsConfigurationRecyclerView.setAdapter(adapter);
        newsConfigurationRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        newsConfigurationRecyclerView.setHasFixedSize(true);

        backToMainActivity = (ImageView) toolbar.findViewById(R.id.backToMainActivity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        backToMainActivity.setOnClickListener(v -> {
            final Weather weather = database.getWeatherData();
            weather.setCurrently(database.getCurrentWeatherFromDatabase());

            weather.setDaily(new Daily());
            weather.getDaily().setData(database.getDailyWeatherFromDatabase());

            weather.setHourly(new Hourly());
            weather.getHourly().setData(database.getHourlyWeatherFromDatabase());

            weather.setAlerts(database.getAlerts());
            final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra(ConstantHolder.WEATHER_DATA_KEY, weather);
            intent.putParcelableArrayListExtra(ConstantHolder.NEWS_DATA_KEY, database.getNewFromDatabase());
            startActivity(intent);
            finish();
        });

    }
}
