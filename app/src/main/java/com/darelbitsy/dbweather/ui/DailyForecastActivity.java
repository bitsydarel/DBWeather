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
    private int mBackgroundColor = new ColorManager().getDrawableForParent()[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        AndroidThreeTen.init(this);

        RecyclerView dailyRecyclerView = (RecyclerView) findViewById(R.id.dailyRecyclerView);
        TextView locationDaily = (TextView) findViewById(R.id.locationDaily);
        TextView checkEmpty = (TextView) findViewById(R.id.checkEmpty);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_WEATHER);
        Day[] days = Arrays.copyOf(parcelables, parcelables.length, Day[].class);


        if(days == null || days.length == 0 ) {
            dailyRecyclerView.setVisibility(View.GONE);
            checkEmpty.setVisibility(View.VISIBLE);

        } else {
            DayAdapters adapters = new DayAdapters(days, this);
            dailyRecyclerView.setAdapter(adapters);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            dailyRecyclerView.setLayoutManager(layoutManager);
            dailyRecyclerView.setHasFixedSize(true);
        }

        locationDaily.setText(intent.getStringExtra(MainActivity.CITYNAME));
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
