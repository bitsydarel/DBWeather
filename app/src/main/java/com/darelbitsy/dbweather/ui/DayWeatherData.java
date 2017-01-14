package com.darelbitsy.dbweather.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ImageView;
import android.widget.TextView;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.R2;
import com.darelbitsy.dbweather.adapters.DayAdapters;
import com.darelbitsy.dbweather.weather.Day;
import com.jakewharton.threetenabp.AndroidThreeTen;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Darel Bitsy on 13/01/17.
 */

public class DayWeatherData extends Activity {

    private TextView mTemperatureLabel;
    private TextView mLocationLabel;
    private ImageView mIconImageView;
    private TextView mDayNameLabel;
    private TextView mHumidityValue;
    private TextView mSummaryLabel;
    private TextView mPrecipValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_weather_data);
        AndroidThreeTen.init(this);

        mTemperatureLabel = (TextView) findViewById(R.id.dayTemperatureLabel);
        mLocationLabel = (TextView) findViewById(R.id.dayLocationLabel);
        mIconImageView = (ImageView) findViewById(R.id.dayIconImageView);
        mDayNameLabel = (TextView) findViewById(R.id.dayNameLabelText);
        mHumidityValue = (TextView) findViewById(R.id.dayHumidityValue);
        mSummaryLabel = (TextView) findViewById(R.id.daySummaryLabel);
        mPrecipValue = (TextView) findViewById(R.id.dayPrecipValue);

        Intent intent = getIntent();
        Day day = intent.getParcelableExtra(DayAdapters.DAY_WEATHER);

        mTemperatureLabel.setText(day.getTemperatureMax() + "");
        mLocationLabel.setText(day.getCityName());
        mIconImageView.setImageResource(day.getIconId());
        mDayNameLabel.setText(day.getDayOfTheWeek());
        mHumidityValue.setText(day.getHumidity() + "");
        mSummaryLabel.setText(day.getSummary());
        mPrecipValue.setText(day.getPrecipChance() + "%");
    }
}
