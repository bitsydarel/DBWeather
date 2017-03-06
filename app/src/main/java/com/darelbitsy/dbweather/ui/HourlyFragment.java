package com.darelbitsy.dbweather.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 11/02/17.
 * Hourly Weather data fragment
 */
public class HourlyFragment extends android.app.Fragment {

    ViewGroup hourlyLayout;

    /*public static HourlyFragment newInstance(WeatherApi weather) {
        HourlyFragment hourlyFragment = new HourlyFragment();
        Bundle args = new Bundle();
        args.putParcelable(MainActivity.WEATHER_DATA_KEY, weather);
        hourlyFragment.setArguments(args);
        return hourlyFragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mWeather = getArguments().getParcelable(MainActivity.WEATHER_DATA_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hourly_fragment, container, false);
        hourlyLayout = (ViewGroup) view.findViewById(R.id.hourlyLayout);
        hourlyLayout.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        moveButton(v);
                        return true;
                    }
                }
        );
        return view;
    }

    private void moveButton(View v) {
        View buttontoMove = v.findViewById(R.id.hourlyTesting);

        //Change position of the view
        ConstraintLayout.LayoutParams positionRules = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );


        //Change size of the view

    }
}
