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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_weather_data);
        AndroidThreeTen.init(this);

        TextView temperatureLabel = (TextView) findViewById(R.id.dayTemperatureLabel);
        TextView locationLabel = (TextView) findViewById(R.id.dayLocationLabel);
        ImageView iconImageView = (ImageView) findViewById(R.id.dayIconImageView);
        TextView dayNameLabel = (TextView) findViewById(R.id.dayNameLabelText);
        TextView humidityValue = (TextView) findViewById(R.id.dayHumidityValue);
        TextView summaryLabel = (TextView) findViewById(R.id.daySummaryLabel);
        TextView precipValue = (TextView) findViewById(R.id.dayPrecipValue);

        Intent intent = getIntent();
        Day day = intent.getParcelableExtra(DayAdapters.DAY_WEATHER);

        temperatureLabel.setText(day.getTemperatureMax() + "");
        locationLabel.setText(day.getCityName());
        iconImageView.setImageResource(day.getIconId());
        dayNameLabel.setText(day.getDayOfTheWeek());
        humidityValue.setText(day.getHumidity() + "%");
        summaryLabel.setText(day.getSummary());
        precipValue.setText(day.getPrecipChance() + "%");
    }
}
