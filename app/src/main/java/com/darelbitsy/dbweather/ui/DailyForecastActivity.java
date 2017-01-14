package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.DayAdapters;
import com.darelbitsy.dbweather.weather.Day;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Arrays;

public class DailyForecastActivity extends Activity {

    private RelativeLayout mActivityDailyForecast;
    private RecyclerView mDailyRecyclerView;
    private TextView mLocationDaily;
    private TextView mCheckEmpty;
    private ColorManager mColorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        AndroidThreeTen.init(this);
        mColorManager = new ColorManager();

        mActivityDailyForecast = (RelativeLayout) findViewById(R.id.activity_daily_forecast);
        mDailyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
        mLocationDaily = (TextView) findViewById(R.id.locationDaily);
        mCheckEmpty = (TextView) findViewById(R.id.checkEmpty);

        mActivityDailyForecast.setBackgroundResource(mColorManager.getDrawableForParent()[0]);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_WEATHER);
        Day[] mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapters adapters = new DayAdapters(mDays, this);

        if(adapters.getItemCount() > 0) {
            mDailyRecyclerView.setAdapter(adapters);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            mDailyRecyclerView.setLayoutManager(layoutManager);
            mDailyRecyclerView.setHasFixedSize(true);
        } else {
            mDailyRecyclerView.setVisibility(View.INVISIBLE);
            mCheckEmpty.setVisibility(View.VISIBLE);
        }


        mLocationDaily.setText(intent.getStringExtra(MainActivity.CITYNAME));

    }
}
