package com.darelbitsy.dbweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.WeatherApi;
import com.darelbitsy.dbweather.adapters.HourAdapter;
import com.darelbitsy.dbweather.weather.Hour;

import java.util.Arrays;

public class HourlyForecastActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);

        RecyclerView hourlyForecastRecyclerView = (RecyclerView) findViewById(R.id.hourlyRecyclerView);
        TextView hourlySummary = (TextView) findViewById(R.id.hourlySummary);
        TextView checkEmpty = (TextView) findViewById(R.id.checkEmpty);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_WEATHER);
        String[] hourlyInfo = intent.getStringArrayExtra(MainActivity.HOURLY_INFO);

        Hour[] hours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        if(hours == null || hours.length == 0) {
            checkEmpty.setVisibility(View.VISIBLE);
            hourlyForecastRecyclerView.setVisibility(View.GONE);

        } else {
            ImageView hourlyIcon = (ImageView) findViewById(R.id.hourlyIcon);
            hourlyIcon.setImageResource(hours[0].getIconId(hourlyInfo[0]));
            hourlySummary.setText(hourlyInfo[1]);

            HourAdapter adapter = new HourAdapter(hours);
            hourlyForecastRecyclerView.setAdapter(adapter);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            hourlyForecastRecyclerView.setLayoutManager(layoutManager);
            hourlyForecastRecyclerView.setHasFixedSize(true);
        }
    }

}
