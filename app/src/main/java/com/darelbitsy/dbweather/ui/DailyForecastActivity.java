package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.DayAdapters;
import com.darelbitsy.dbweather.weather.Day;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Arrays;

public class DailyForecastActivity extends Activity {

    private static final String COLOR_BACKGROUND = "COLOR_BACKGROUND";
    private RecyclerView mDailyRecyclerView;
    private TextView mLocationDaily;
    private TextView mCheckEmpty;
    private int mBackgroundColor = new ColorManager().getDrawableForParent()[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        AndroidThreeTen.init(this);

        mDailyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
        mLocationDaily = (TextView) findViewById(R.id.locationDaily);
        mCheckEmpty = (TextView) findViewById(R.id.checkEmpty);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_WEATHER);
        Day[] days = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapters adapters = new DayAdapters(days, this);

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

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBackgroundColor = savedInstanceState.getInt(COLOR_BACKGROUND);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COLOR_BACKGROUND, mBackgroundColor);
    }
}
