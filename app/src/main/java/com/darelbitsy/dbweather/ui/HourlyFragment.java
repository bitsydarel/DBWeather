package com.darelbitsy.dbweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.R;
import com.darelbitsy.dbweather.WeatherApi;

/**
 * Created by Darel Bitsy on 11/02/17.
 */
public class HourlyFragment extends android.app.Fragment {
    private WeatherApi mWeather;

    /*public static HourlyFragment newInstance(WeatherApi weather) {
        HourlyFragment hourlyFragment = new HourlyFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainActivity1.WEATHER_DATA_KEY, weather);
        hourlyFragment.setArguments(args);
        return hourlyFragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mWeather = getArguments().getParcelable(MainActivity1.WEATHER_DATA_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hourly_fragment, container, false);
    }
}
