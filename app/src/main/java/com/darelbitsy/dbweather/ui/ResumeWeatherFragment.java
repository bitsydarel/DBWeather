package com.darelbitsy.dbweather.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 11/02/17.
 */

public class ResumeWeatherFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.resume_weather_fragment, container, false);
        return view;
    }
}
