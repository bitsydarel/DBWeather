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
    private RecyclerView mHourlyForecastRecyclerView;
    private ImageView mHourlyIcon;
    private TextView mHourlySummary;
    private TextView mCheckEmpty;


    private Hour[] mHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        mHourlyForecastRecyclerView = (RecyclerView) findViewById(R.id.hourlyRecyclerView);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_WEATHER);
        String[] hourlyInfo = intent.getStringArrayExtra(MainActivity.HOURLY_INFO);

        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);
        mHourlyIcon = (ImageView) findViewById(R.id.hourlyIcon);
        mHourlyIcon.setImageResource(mHours[0].getIconId(hourlyInfo[0]));

        mHourlySummary = (TextView) findViewById(R.id.hourlySummary);
        mCheckEmpty = (TextView) findViewById(R.id.checkEmpty);
        mHourlySummary.setText(hourlyInfo[1]);

        HourAdapter adapter = new HourAdapter(mHours);
        if(adapter.getItemCount() == 0) {
            mCheckEmpty.setVisibility(View.VISIBLE);
            mHourlyForecastRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            mHourlyForecastRecyclerView.setAdapter(adapter);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            mHourlyForecastRecyclerView.setLayoutManager(layoutManager);

            mHourlyForecastRecyclerView.setHasFixedSize(true);
        }
    }

}
