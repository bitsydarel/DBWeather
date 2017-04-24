package com.darelbitsy.dbweather.views.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darelbitsy.dbweather.R;

/**
 * Created by Darel Bitsy on 11/02/17.
 */

public class AdMobFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.admobe_layout, container, false);
    }
}
