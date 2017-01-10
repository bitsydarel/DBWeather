package com.darelbitsy.dbweather.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.RelativeLayout;

import com.darelbitsy.dbweather.ColorManager;
import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.adapters.DayAdapters;
import com.darelbitsy.dbweather.weather.Day;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Arrays;

public class DailyForecastActivity extends ListActivity {

    RelativeLayout mDailyForecastActivity;
    //TextView mDailyTemperatureLabel;

    private ColorManager mColorManager;
    private Day[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        AndroidThreeTen.init(this);

        mDailyForecastActivity = (RelativeLayout) findViewById(R.id.activity_daily_forecast);

        mColorManager = new ColorManager();
        setDailyActivityBackground();

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_WEATHER);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapters adapters = new DayAdapters(this, mDays);
        setListAdapter(adapters);
    }

    public void setDailyActivityBackground() {
        int[] colors =  mColorManager.getDrawableForParent();

        mDailyForecastActivity.setBackgroundResource(colors[0]);
    }
}
